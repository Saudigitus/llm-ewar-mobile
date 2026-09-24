package org.saudigitus.climasaude.data.sync

import org.saudigitus.climasaude.data.local.dao.ChildDao
import org.saudigitus.climasaude.data.local.dao.ProfileDao
import org.saudigitus.climasaude.data.local.dao.TriageDao
import org.saudigitus.climasaude.data.mapper.toEntity
import org.saudigitus.climasaude.data.mapper.toPayload
import org.saudigitus.climasaude.data.remote.api.ClimaSaudeApi
import org.saudigitus.climasaude.domain.error.AppError
import org.saudigitus.climasaude.domain.error.AppFailure
import org.saudigitus.climasaude.domain.model.TriageSyncResult
import org.saudigitus.climasaude.platform.SecureCredentials
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Two-way sync of children and triages. Pending local records are uploaded first; downloaded
 * records never overwrite a local copy that has not been synced yet.
 */
class TriageSyncRepository(
    private val profileDao: ProfileDao,
    private val childDao: ChildDao,
    private val triageDao: TriageDao,
    private val api: ClimaSaudeApi,
    private val credentials: SecureCredentials
) {
    @OptIn(ExperimentalTime::class)
    suspend fun sync(): TriageSyncResult {
        val profile = profileDao.active() ?: return TriageSyncResult(0, 0, 0, 0)
        if (profile.demo) return TriageSyncResult(0, 0, 0, 0)
        if (!api.configured) throw AppFailure(AppError.ALERTS_NOT_CONFIGURED)
        val auth = credentials.read() ?: throw AppFailure(AppError.SIGN_IN_AGAIN)
        suspend fun sessionChanged() = profileDao.active()?.id != profile.id

        var uploadedChildren = 0
        var uploadedTriages = 0
        for (child in childDao.pending(profile.id)) {
            api.putChild(auth, child.toPayload())
            if (sessionChanged()) return TriageSyncResult(uploadedChildren, uploadedTriages, 0, 0)
            childDao.markSynced(profile.id, child.id, Clock.System.now().toString())
            uploadedChildren++
        }
        for (triage in triageDao.pending(profile.id)) {
            api.putTriage(auth, triage.toPayload())
            if (sessionChanged()) return TriageSyncResult(uploadedChildren, uploadedTriages, 0, 0)
            triageDao.markSynced(profile.id, triage.id, Clock.System.now().toString())
            uploadedTriages++
        }

        val remoteChildren = api.children(auth).children
        val remoteTriages = api.triages(auth).triages
        if (sessionChanged()) return TriageSyncResult(uploadedChildren, uploadedTriages, 0, 0)
        val syncedAt = Clock.System.now().toString()
        var downloadedChildren = 0
        var downloadedTriages = 0
        for (child in remoteChildren) {
            if (child.userId != profile.id) continue
            val existing = childDao.find(profile.id, child.id)
            if (existing != null && existing.syncedAt == null) continue
            val incoming = child.toEntity(syncedAt)
            childDao.upsert(
                incoming.copy(
                    sex = incoming.sex ?: existing?.sex,
                    caregiver = incoming.caregiver ?: existing?.caregiver,
                    community = incoming.community ?: existing?.community,
                    areaId = incoming.areaId ?: existing?.areaId,
                    latitude = incoming.latitude ?: existing?.latitude,
                    longitude = incoming.longitude ?: existing?.longitude,
                    locationAccuracy = incoming.locationAccuracy ?: existing?.locationAccuracy
                )
            )
            downloadedChildren++
        }
        val knownChildren = childDao.ids(profile.id).toSet()
        for (triage in remoteTriages) {
            if (triage.userId != profile.id || triage.childId !in knownChildren) continue
            val existing = triageDao.find(profile.id, triage.id)
            if (existing != null && existing.syncedAt == null) continue
            val incoming = triage.toEntity(syncedAt)
            triageDao.upsert(
                incoming.copy(
                    recommendationTitle = incoming.recommendationTitle
                        ?: existing?.recommendationTitle,
                    recommendationBody = incoming.recommendationBody
                        ?: existing?.recommendationBody,
                    recommendationSource = incoming.recommendationSource
                        ?: existing?.recommendationSource,
                    recommendationSteps = incoming.recommendationSteps
                        ?: existing?.recommendationSteps,
                    recommendationAlertIds = incoming.recommendationAlertIds
                        ?: existing?.recommendationAlertIds
                )
            )
            downloadedTriages++
        }
        return TriageSyncResult(
            uploadedChildren,
            uploadedTriages,
            downloadedChildren,
            downloadedTriages
        )
    }
}
