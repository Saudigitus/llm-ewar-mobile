package org.saudigitus.climasaude.data.local.entity

import androidx.room.Entity

@Entity(tableName = "triage_translations", primaryKeys = ["triageId", "language"])
data class TriageTranslationEntity(
    val triageId: String,
    val userId: String,
    val language: String,
    val title: String,
    val steps: String
)
