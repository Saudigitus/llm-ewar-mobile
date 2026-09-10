package org.saudigitus.climasaude.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val username: String,
    val name: String,
    val demo: Boolean,
    val active: Boolean,
    /** [org.saudigitus.climasaude.domain.model.AppLanguage] name; null means Portuguese. */
    val language: String? = null
)
