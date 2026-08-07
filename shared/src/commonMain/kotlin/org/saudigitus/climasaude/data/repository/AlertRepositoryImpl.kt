package org.saudigitus.climasaude.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.saudigitus.climasaude.data.demo.DemoAlerts
import org.saudigitus.climasaude.data.local.dao.AlertDao
import org.saudigitus.climasaude.data.local.dao.AreaDao
import org.saudigitus.climasaude.data.local.dao.ProfileDao
import org.saudigitus.climasaude.data.local.entity.AlertEntity
import org.saudigitus.climasaude.data.mapper.enumValueOrNull
import org.saudigitus.climasaude.data.mapper.toDomain
import org.saudigitus.climasaude.data.remote.api.ClimaSaudeApi
import org.saudigitus.climasaude.domain.error.AppError
import org.saudigitus.climasaude.domain.error.AppFailure
import org.saudigitus.climasaude.domain.guidance.GuidanceText
import org.saudigitus.climasaude.domain.model.Alert
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.Disease
import org.saudigitus.climasaude.domain.model.RiskLevel
import org.saudigitus.climasaude.domain.repository.AlertRepository
import org.saudigitus.climasaude.platform.NudgeScheduler
import org.saudigitus.climasaude.platform.SecureCredentials

class AlertRepositoryImpl(
    private val profileDao: ProfileDao,
    private val areaDao: AreaDao,
    private val alertDao: AlertDao,
    private val api: ClimaSaudeApi,
    private val secureCredentials: SecureCredentials,
    private val nudgeScheduler: NudgeScheduler
) : AlertRepository {

    override val alerts: Flow<List<Alert>> =
        alertDao.observeAll().combine(profileDao.observeActive()) { stored, profile ->
            if (profile == null) emptyList() else {
                val allowed = areaDao.forUser(profile.id).map { it.id }.toSet()
                stored.filter { it.areaId in allowed }.mapNotNull { it.toDomain() }
            }
        }

    override suspend fun refresh(): Int {
        val profile = profileDao.active() ?: return 0
        val areas = areaDao.forUser(profile.id)
        if (profile.demo) {
            val demoAlerts = areas.firstOrNull()?.let { DemoAlerts.forArea(it.toDomain()) }.orEmpty()
            alertDao.replace(demoAlerts)
            return demoAlerts.size
        }
        if (!api.configured) return 0
        val credentials = secureCredentials.read() ?: throw AppFailure(AppError.SIGN_IN_AGAIN)
        val allowedAreas = areas.map { it.id }.toSet()
        val incoming = api.alerts(credentials).alerts.mapNotNull { item ->
            val disease =
                enumValueOrNull<Disease>(item.disease.uppercase()) ?: return@mapNotNull null
            val level = enumValueOrNull<RiskLevel>(item.level.uppercase()) ?: return@mapNotNull null
            if (item.areaId !in allowedAreas || disease != Disease.MALARIA) return@mapNotNull null
            AlertEntity(
                item.id, item.areaId, item.areaName, disease.name, level.name,
                item.probability?.takeIf { it in 0.0..1.0 }, item.startsAt, item.endsAt,
                item.rainfallNote, item.updatedAt, false
            )
        }
        alertDao.replace(incoming)
        scheduleNudges(incoming)
        return incoming.size
    }

    private fun scheduleNudges(alerts: List<AlertEntity>) {
        val message = GuidanceText.forLanguage(AppLanguage.PORTUGUESE).visitFamilies
        alerts.filter { !it.demo && it.level != RiskLevel.GREEN.name }.forEach { alert ->
            runCatching { nudgeScheduler.schedule(alert.id, alert.areaName, alert.startsAt, message) }
        }
    }
}
