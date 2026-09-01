package org.saudigitus.climasaude.domain.model

data class SyncOutcome(
    val kind: SyncKind,
    val alerts: Int = 0,
    val records: TriageSyncResult? = null
)
