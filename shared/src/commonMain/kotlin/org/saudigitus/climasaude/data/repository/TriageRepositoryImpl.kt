package org.saudigitus.climasaude.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import org.saudigitus.climasaude.data.local.dao.AlertDao
import org.saudigitus.climasaude.data.local.dao.AreaDao
import org.saudigitus.climasaude.data.local.dao.ChildDao
import org.saudigitus.climasaude.data.local.dao.ProfileDao
import org.saudigitus.climasaude.data.local.dao.TriageDao
import org.saudigitus.climasaude.data.local.dao.TriageTranslationDao
import org.saudigitus.climasaude.data.local.entity.TriageEntity
import org.saudigitus.climasaude.data.local.entity.TriageTranslationEntity
import org.saudigitus.climasaude.data.mapper.decodeStringList
import org.saudigitus.climasaude.data.mapper.encodeStringList
import org.saudigitus.climasaude.data.mapper.toDomain
import org.saudigitus.climasaude.data.prompt.TriagePrompt
import org.saudigitus.climasaude.domain.ai.TriageModel
import org.saudigitus.climasaude.domain.guidance.TriageRecommendationEngine
import org.saudigitus.climasaude.domain.guidance.organizeFollowUps
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.ChildFollowUp
import org.saudigitus.climasaude.domain.model.Triage
import org.saudigitus.climasaude.domain.model.TriageGuidance
import org.saudigitus.climasaude.domain.repository.TriageRepository
import org.saudigitus.climasaude.platform.AutoSyncScheduler
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TriageRepositoryImpl(
    private val profileDao: ProfileDao,
    private val areaDao: AreaDao,
    private val alertDao: AlertDao,
    private val childDao: ChildDao,
    private val triageDao: TriageDao,
    private val translationDao: TriageTranslationDao,
    private val recommendationEngine: TriageRecommendationEngine,
    private val model: TriageModel,
    private val autoSyncScheduler: AutoSyncScheduler
) : TriageRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val followUps: Flow<List<ChildFollowUp>> =
        profileDao.observeActive().flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList()) else combine(
                childDao.observeForUser(profile.id),
                triageDao.observeForUser(profile.id),
                translationDao.observeForUser(profile.id)
            ) { children, triages, translations ->
                val byTriage = translations.groupBy { it.triageId }
                organizeFollowUps(
                    children.map { it.toDomain() },
                    triages.map { it.toDomain(byTriage[it.id].orEmpty()) })
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val pendingCount: Flow<Int> = profileDao.observeActive().flatMapLatest { profile ->
        if (profile == null) flowOf(0) else combine(
            childDao.observePendingCount(profile.id),
            triageDao.observePendingCount(profile.id)
        ) { children, triages -> children + triages }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun addTriage(
        childId: String, fever: Boolean, dangerSigns: Boolean, malariaTest: Boolean,
        referred: Boolean, notes: String
    ): Triage {
        val profile = profileDao.active() ?: error("Sem sessão ativa")
        val child = childDao.find(profile.id, childId) ?: error("Criança indisponível")
        require(notes.length <= 500)
        val id = newId("triage")
        val entity = TriageEntity(
            id,
            profile.id,
            child.id,
            Clock.System.now().toString(),
            fever,
            dangerSigns,
            malariaTest,
            referred,
            notes.trim(),
            profile.demo
        )
        triageDao.insert(entity)
        val areaId = child.areaId ?: areaDao.forUser(profile.id).singleOrNull()?.id
        val alerts = areaId?.let { alertDao.forArea(it) }.orEmpty()
            .filter { profile.demo || !it.demo }
        val matching = TriagePrompt.matchingAlerts(entity, alerts)
        val alertIds = matching.map { it.id }
        val guidance = runCatching {
            model.complete(TriagePrompt.recommendation(child, entity, matching))
        }.getOrNull()
            ?.let { TriagePrompt.parse(it, "IA local · confirmar com o protocolo APE", alertIds) }
            ?.takeIf { !dangerSigns || TriagePrompt.hasUrgentReferral(it) }
            ?: recommendationEngine.guidance(
                fever, dangerSigns, malariaTest, referred, matching.map { it.level }, alertIds
            )
        triageDao.saveRecommendation(
            profile.id, id, guidance.title, guidance.body, guidance.source,
            encodeStringList(guidance.steps), encodeStringList(guidance.alertIds)
        )
        if (!profile.demo) runCatching { autoSyncScheduler.requestSync() }
        return triageDao.find(profile.id, id)!!.toDomain(emptyList())
    }

    override suspend fun translate(triageId: String, language: AppLanguage): Boolean {
        if (language == AppLanguage.PORTUGUESE) return true
        val profile = profileDao.active() ?: return false
        if (translationDao.find(profile.id, triageId, language.name) != null) return true
        val saved = triageDao.find(profile.id, triageId) ?: return false
        val title = saved.recommendationTitle ?: return false
        val steps = decodeStringList(saved.recommendationSteps).ifEmpty {
            saved.recommendationBody?.lines().orEmpty()
        }
        if (steps.isEmpty()) return false
        val guidance = TriageGuidance(title, steps, saved.recommendationSource.orEmpty())
        val response =
            runCatching { model.complete(TriagePrompt.translation(guidance, language)) }.getOrNull()
                ?: return false
        val parsed = TriagePrompt.parse(response, "IA local") ?: return false
        if (parsed.steps.size != steps.size) return false
        translationDao.insert(
            TriageTranslationEntity(
                triageId, profile.id, language.name, parsed.title, encodeStringList(parsed.steps)
            )
        )
        return true
    }
}
