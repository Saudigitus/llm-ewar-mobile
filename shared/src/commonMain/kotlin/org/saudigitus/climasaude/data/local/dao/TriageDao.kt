package org.saudigitus.climasaude.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.saudigitus.climasaude.data.local.entity.TriageEntity

@Dao
interface TriageDao {
    @Query("SELECT * FROM triages WHERE userId = :userId AND id = :triageId LIMIT 1")
    suspend fun find(userId: String, triageId: String): TriageEntity?

    @Query("SELECT * FROM triages WHERE userId = :userId ORDER BY recordedAt DESC")
    fun observeForUser(userId: String): Flow<List<TriageEntity>>

    @Query("SELECT * FROM triages WHERE userId = :userId AND demo = 0 AND syncedAt IS NULL AND recommendationTitle IS NOT NULL ORDER BY recordedAt")
    suspend fun pending(userId: String): List<TriageEntity>

    @Query("SELECT COUNT(*) FROM triages WHERE userId = :userId AND demo = 0 AND syncedAt IS NULL")
    fun observePendingCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(triage: TriageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(triage: TriageEntity)

    @Query("UPDATE triages SET recommendationTitle = :title, recommendationBody = :body, recommendationSource = :source, recommendationSteps = :steps, recommendationAlertIds = :alertIds WHERE id = :id AND userId = :userId AND recommendationTitle IS NULL")
    suspend fun saveRecommendation(
        userId: String,
        id: String,
        title: String,
        body: String,
        source: String,
        steps: String,
        alertIds: String
    )

    @Query("UPDATE triages SET syncedAt = :syncedAt WHERE id = :id AND userId = :userId")
    suspend fun markSynced(userId: String, id: String, syncedAt: String)
}
