package org.saudigitus.climasaude.domain.model

data class TriageSyncResult(
    val uploadedChildren: Int,
    val uploadedTriages: Int,
    val downloadedChildren: Int,
    val downloadedTriages: Int
)
