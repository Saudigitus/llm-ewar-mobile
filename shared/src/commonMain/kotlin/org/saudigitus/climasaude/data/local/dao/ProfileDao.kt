package org.saudigitus.climasaude.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.saudigitus.climasaude.data.local.entity.ProfileEntity

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles WHERE active = 1 LIMIT 1")
    fun observeActive(): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE active = 1 LIMIT 1")
    suspend fun active(): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: ProfileEntity)

    @Query("UPDATE profiles SET language = :language WHERE active = 1")
    suspend fun setLanguage(language: String)

    @Query("UPDATE profiles SET active = 0")
    suspend fun deactivate()

    @Query("DELETE FROM profiles WHERE id != :id")
    suspend fun removeOthers(id: String)

    @Query("DELETE FROM profiles")
    suspend fun clear()
}
