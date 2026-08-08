package org.saudigitus.climasaude.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.saudigitus.climasaude.data.local.entity.AlertEntity

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts ORDER BY CASE level WHEN 'RED' THEN 0 WHEN 'YELLOW' THEN 1 ELSE 2 END, updatedAt DESC")
    fun observeAll(): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE areaId = :areaId")
    suspend fun forArea(areaId: String): List<AlertEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(alerts: List<AlertEntity>)

    @Query("DELETE FROM alerts")
    suspend fun clear()

    @Transaction
    suspend fun replace(alerts: List<AlertEntity>) {
        clear()
        upsert(alerts)
    }
}
