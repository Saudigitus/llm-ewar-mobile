package org.saudigitus.climasaude.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun androidDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> =
    Room.databaseBuilder<AppDatabase>(
        context.applicationContext,
        context.getDatabasePath("climasaude.db").absolutePath
    )
