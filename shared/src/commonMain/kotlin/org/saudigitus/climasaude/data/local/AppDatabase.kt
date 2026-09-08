package org.saudigitus.climasaude.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import org.saudigitus.climasaude.data.local.dao.AlertDao
import org.saudigitus.climasaude.data.local.dao.AreaDao
import org.saudigitus.climasaude.data.local.dao.ChildDao
import org.saudigitus.climasaude.data.local.dao.ProfileDao
import org.saudigitus.climasaude.data.local.dao.TriageDao
import org.saudigitus.climasaude.data.local.dao.TriageTranslationDao
import org.saudigitus.climasaude.data.local.entity.AlertEntity
import org.saudigitus.climasaude.data.local.entity.AreaEntity
import org.saudigitus.climasaude.data.local.entity.ChildEntity
import org.saudigitus.climasaude.data.local.entity.ProfileEntity
import org.saudigitus.climasaude.data.local.entity.TriageEntity
import org.saudigitus.climasaude.data.local.entity.TriageTranslationEntity

@Database(
    entities = [ProfileEntity::class, AreaEntity::class, AlertEntity::class, ChildEntity::class, TriageEntity::class, TriageTranslationEntity::class],
    version = 6,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun areaDao(): AreaDao
    abstract fun alertDao(): AlertDao
    abstract fun childDao(): ChildDao
    abstract fun triageDao(): TriageDao
    abstract fun triageTranslationDao(): TriageTranslationDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
