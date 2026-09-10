package org.saudigitus.climasaude.data.local.entity

import androidx.room.Entity

@Entity(tableName = "areas", primaryKeys = ["userId", "id"])
data class AreaEntity(val userId: String, val id: String, val name: String)
