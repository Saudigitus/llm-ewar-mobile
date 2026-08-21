package org.saudigitus.climasaude.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "triages")
data class TriageEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val childId: String,
    val recordedAt: String,
    val fever: Boolean,
    val dangerSigns: Boolean,
    val malariaTest: Boolean,
    val referred: Boolean,
    val notes: String,
    val demo: Boolean,
    val recommendationTitle: String? = null,
    val recommendationBody: String? = null,
    val recommendationSource: String? = null,
    val syncedAt: String? = null,
    val recommendationSteps: String? = null,
    val recommendationAlertIds: String? = null
)
