package org.saudigitus.climasaude.domain.sync

import org.saudigitus.climasaude.domain.model.SyncOutcome

/** Refreshes alerts and exchanges follow-up records with the server, one run at a time. */
interface SyncCoordinator {
    suspend fun sync(): SyncOutcome
}
