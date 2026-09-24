package org.saudigitus.climasaude.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import org.saudigitus.climasaude.data.local.dao.AlertDao
import org.saudigitus.climasaude.data.local.dao.AreaDao
import org.saudigitus.climasaude.data.local.dao.ChildDao
import org.saudigitus.climasaude.data.local.dao.ProfileDao
import org.saudigitus.climasaude.data.local.dao.TriageDao
import org.saudigitus.climasaude.data.local.dao.TriageTranslationDao
import org.saudigitus.climasaude.data.local.entity.ChildEntity
import org.saudigitus.climasaude.data.local.entity.ProfileEntity
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
import kotlin.time.Duration.Companion.seconds
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
        val matching = matchingAlerts(profile, child, entity)
        val guidance = recommendationEngine.guidance(
            fever, dangerSigns, malariaTest, referred, matching.map { it.level }, matching.map { it.id }
        )
        triageDao.saveRecommendation(
            profile.id, id, guidance.title, guidance.body, guidance.source,
            encodeStringList(guidance.steps), encodeStringList(guidance.alertIds)
        )
        return triageDao.find(profile.id, id)!!.toDomain(emptyList())
    }

    override suspend fun refineRecommendation(
        triageId: String,
        onDraft: (TriageGuidance) -> Unit
    ): Boolean {
        val profile = profileDao.active() ?: return false
        try {
            val triage = triageDao.find(profile.id, triageId) ?: return false
            val current = triage.recommendationSource ?: return false
            if (current == AI_SOURCE) return false
            val child = childDao.find(profile.id, triage.childId) ?: return false
            val matching = matchingAlerts(profile, child, triage)
            var text = ""
            val finished = withTimeoutOrNull(RECOMMENDATION_TIMEOUT) {
                model.stream(TriagePrompt.recommendation(child, triage, matching))
                    .transformWhile { emit(it); !TriagePrompt.hasAllSteps(it) }
                    .catch { }
                    .collect { partial ->
                        text = partial
                        TriagePrompt.draft(partial)?.let(onDraft)
                    }
            } != null
            val usable = if (finished && !TriagePrompt.hasAllSteps(text)) text
            else TriagePrompt.finishedLines(text)
            val guidance = TriagePrompt.parse(usable, AI_SOURCE)
                ?.takeIf { !triage.dangerSigns || TriagePrompt.hasUrgentReferral(it) }
                ?: return false
            val replaced = triageDao.replaceRecommendation(
                profile.id, triageId, current, guidance.title, guidance.body, guidance.source,
                encodeStringList(guidance.steps)
            ) > 0
            if (replaced) translationDao.deleteForTriage(profile.id, triageId)
            return replaced
        } finally {
            if (!profile.demo) runCatching { autoSyncScheduler.requestSync() }
        }
    }

    private suspend fun matchingAlerts(profile: ProfileEntity, child: ChildEntity, triage: TriageEntity) =
        (child.areaId ?: areaDao.forUser(profile.id).singleOrNull()?.id)
            ?.let { alertDao.forArea(it) }.orEmpty()
            .filter { profile.demo || !it.demo }
            .let { TriagePrompt.matchingAlerts(triage, it) }

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

    private companion object {
        const val AI_SOURCE = "IA local · confirmar com o protocolo APE"
        val RECOMMENDATION_TIMEOUT = 60.seconds
    }
}
