package org.saudigitus.climasaude.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val triageMigration = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `children` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `name` TEXT NOT NULL, `ageYears` INTEGER, `createdAt` TEXT NOT NULL, `demo` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `triages` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `childId` TEXT NOT NULL, `recordedAt` TEXT NOT NULL, `fever` INTEGER NOT NULL, `dangerSigns` INTEGER NOT NULL, `malariaTest` INTEGER NOT NULL, `referred` INTEGER NOT NULL, `notes` TEXT NOT NULL, `demo` INTEGER NOT NULL, PRIMARY KEY(`id`))")
    }
}

val followUpMigration = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE `children` ADD COLUMN `sex` TEXT")
        connection.execSQL("ALTER TABLE `children` ADD COLUMN `caregiver` TEXT")
        connection.execSQL("ALTER TABLE `children` ADD COLUMN `community` TEXT")
        connection.execSQL("ALTER TABLE `children` ADD COLUMN `syncedAt` TEXT")
        connection.execSQL("ALTER TABLE `triages` ADD COLUMN `recommendationTitle` TEXT")
        connection.execSQL("ALTER TABLE `triages` ADD COLUMN `recommendationBody` TEXT")
        connection.execSQL("ALTER TABLE `triages` ADD COLUMN `recommendationSource` TEXT")
        connection.execSQL("ALTER TABLE `triages` ADD COLUMN `syncedAt` TEXT")
    }
}

val guidanceMigration = object : Migration(3, 4) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE `triages` ADD COLUMN `recommendationSteps` TEXT")
        connection.execSQL("ALTER TABLE `triages` ADD COLUMN `recommendationAlertIds` TEXT")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `triage_translations` (`triageId` TEXT NOT NULL, `userId` TEXT NOT NULL, `language` TEXT NOT NULL, `title` TEXT NOT NULL, `steps` TEXT NOT NULL, PRIMARY KEY(`triageId`, `language`))")
    }
}

val childAreaMigration = object : Migration(4, 5) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE `children` ADD COLUMN `areaId` TEXT")
    }
}

val profileLanguageMigration = object : Migration(5, 6) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE `profiles` ADD COLUMN `language` TEXT")
    }
}

val childLocationMigration = object : Migration(6, 7) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE `children` ADD COLUMN `latitude` REAL")
        connection.execSQL("ALTER TABLE `children` ADD COLUMN `longitude` REAL")
        connection.execSQL("ALTER TABLE `children` ADD COLUMN `locationAccuracy` REAL")
    }
}

/** Every schema migration, in version order. Append new migrations here. */
val ALL_MIGRATIONS: Array<Migration> =
    arrayOf(
        triageMigration,
        followUpMigration,
        guidanceMigration,
        childAreaMigration,
        profileLanguageMigration,
        childLocationMigration
    )
