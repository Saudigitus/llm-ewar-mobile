package org.saudigitus.climasaude.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.saudigitus.climasaude.data.local.entity.AreaEntity

@Dao
interface AreaDao {
    @Query("SELECT * FROM areas WHERE userId = :userId")
    suspend fun forUser(userId: String): List<AreaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(areas: List<AreaEntity>)

    @Query("DELETE FROM areas WHERE userId = :userId")
    suspend fun clearForUser(userId: String)

    @Query("DELETE FROM areas WHERE userId != :userId")
    suspend fun removeOtherUsers(userId: String)

    @Query("DELETE FROM areas")
    suspend fun clear()
}
