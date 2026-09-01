package org.saudigitus.climasaude.data.sync

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.saudigitus.climasaude.data.remote.api.ClimaSaudeApi
import org.saudigitus.climasaude.domain.model.SyncKind
import org.saudigitus.climasaude.domain.model.SyncOutcome
import org.saudigitus.climasaude.domain.repository.AlertRepository
import org.saudigitus.climasaude.domain.repository.SessionRepository
import org.saudigitus.climasaude.domain.sync.SyncCoordinator

class SyncCoordinatorImpl(
    private val sessionRepository: SessionRepository,
    private val alertRepository: AlertRepository,
    private val triageSyncRepository: TriageSyncRepository,
    private val api: ClimaSaudeApi
) : SyncCoordinator {
    private val mutex = Mutex()

    override suspend fun sync(): SyncOutcome = mutex.withLock {
        val profile =
            sessionRepository.currentProfile() ?: return@withLock SyncOutcome(SyncKind.NO_SESSION)
        if (profile.demo) return@withLock SyncOutcome(SyncKind.DEMO, alertRepository.refresh())
        if (!api.configured) return@withLock SyncOutcome(
            SyncKind.NOT_CONFIGURED,
            alertRepository.refresh()
        )
        val alerts = runCatching { alertRepository.refresh() }
        val records = runCatching { triageSyncRepository.sync() }
        alerts.exceptionOrNull()?.let { throw it }
        records.exceptionOrNull()?.let { throw it }
        SyncOutcome(SyncKind.COMPLETE, alerts.getOrThrow(), records.getOrThrow())
    }
}
