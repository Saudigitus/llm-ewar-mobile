package org.saudigitus.climasaude.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.saudigitus.climasaude.data.local.migration.ALL_MIGRATIONS

fun buildDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase =
    builder.addMigrations(*ALL_MIGRATIONS)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default).build()
