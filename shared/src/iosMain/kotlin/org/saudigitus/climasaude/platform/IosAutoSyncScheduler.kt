package org.saudigitus.climasaude.platform

import org.saudigitus.climasaude.syncWhenConnected

class IosAutoSyncScheduler : AutoSyncScheduler {
    override fun requestSync() {
        syncWhenConnected()
    }
}
