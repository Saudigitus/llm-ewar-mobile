package org.saudigitus.climasaude.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.saudigitus.climasaude.data.demo.DemoAlerts
import org.saudigitus.climasaude.data.demo.DemoTriage
import org.saudigitus.climasaude.data.local.dao.AlertDao
import org.saudigitus.climasaude.data.local.dao.AreaDao
import org.saudigitus.climasaude.data.local.dao.ChildDao
import org.saudigitus.climasaude.data.local.dao.ProfileDao
import org.saudigitus.climasaude.data.local.dao.TriageDao
import org.saudigitus.climasaude.data.local.entity.AreaEntity
import org.saudigitus.climasaude.data.local.entity.ProfileEntity
import org.saudigitus.climasaude.data.mapper.toDomain
import org.saudigitus.climasaude.data.remote.api.Dhis2Api
import org.saudigitus.climasaude.domain.error.AppError
import org.saudigitus.climasaude.domain.error.AppFailure
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.CatchmentArea
import org.saudigitus.climasaude.domain.model.Credentials
import org.saudigitus.climasaude.domain.model.UserProfile
import org.saudigitus.climasaude.domain.repository.AlertRepository
import org.saudigitus.climasaude.domain.repository.SessionRepository
import org.saudigitus.climasaude.platform.AutoSyncScheduler
import org.saudigitus.climasaude.platform.NudgeScheduler
import org.saudigitus.climasaude.platform.SecureCredentials

class SessionRepositoryImpl(
    private val profileDao: ProfileDao,
    private val areaDao: AreaDao,
    private val alertDao: AlertDao,
    private val childDao: ChildDao,
    private val triageDao: TriageDao,
    private val api: Dhis2Api,
    private val alertRepository: AlertRepository,
    private val secureCredentials: SecureCredentials,
    private val nudgeScheduler: NudgeScheduler,
    private val autoSyncScheduler: AutoSyncScheduler
) : SessionRepository {

    override val profile: Flow<UserProfile?> = profileDao.observeActive().map { entity ->
        entity?.toDomain(areaDao.forUser(entity.id))
    }

    override val areas: Flow<List<CatchmentArea>> = profile.map { it?.areas.orEmpty() }

    override suspend fun currentProfile(): UserProfile? {
        val entity = profileDao.active() ?: return null
        return entity.toDomain(areaDao.forUser(entity.id))
    }

    override suspend fun login(username: String, password: String): Boolean {
        val credentials = Credentials(username.trim(), password)
        val remote = api.authenticate(credentials)
        if (remote.organisationUnits.isEmpty()) throw AppFailure(AppError.NO_AREA)
        val previous = profileDao.active()
        secureCredentials.save(credentials)
        profileDao.deactivate()
        if (previous?.id != remote.id) {
            alertDao.clear()
            childDao.clearAllRecords()
        }
        areaDao.clearForUser(remote.id)
        areaDao.upsert(remote.organisationUnits.map { AreaEntity(remote.id, it.id, it.displayName) })
        profileDao.upsert(
            ProfileEntity(
                remote.id,
                remote.username,
                remote.displayName,
                demo = false,
                active = true,
                language = previous?.takeIf { it.id == remote.id }?.language
            )
        )
        profileDao.removeOthers(remote.id)
        areaDao.removeOtherUsers(remote.id)
        val synced = runCatching { alertRepository.refresh() }.isSuccess
        runCatching { autoSyncScheduler.requestSync() }
        return synced
    }

    override suspend fun enterDemo() {
        clearSession()
        val area = CatchmentArea("demo-area", "Distrito Piloto · Zona Norte")
        areaDao.upsert(listOf(AreaEntity(DEMO_USER_ID, area.id, area.name)))
        profileDao.upsert(ProfileEntity(DEMO_USER_ID, "john.doe", "John Doe", true, true))
        val demoAlerts = DemoAlerts.forArea(area)
        alertDao.replace(demoAlerts)
        val (children, triages) = DemoTriage.records(demoAlerts)
        children.forEach { childDao.insert(it) }
        triages.forEach { triageDao.insert(it) }
    }

    override suspend fun setLanguage(language: AppLanguage) = profileDao.setLanguage(language.name)

    override suspend fun logout() = clearSession()

    private suspend fun clearSession() {
        secureCredentials.clear()
        nudgeScheduler.clear()
        profileDao.clear()
        areaDao.clear()
        alertDao.clear()
        childDao.clearAllRecords()
    }

    private companion object {
        const val DEMO_USER_ID = "demo-ape"
    }
}
