package org.saudigitus.climasaude.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.saudigitus.climasaude.data.local.entity.ChildEntity

@Dao
interface ChildDao {
    @Query("SELECT * FROM children WHERE userId = :userId AND id = :childId LIMIT 1")
    suspend fun find(userId: String, childId: String): ChildEntity?

    @Query("SELECT * FROM children WHERE userId = :userId")
    fun observeForUser(userId: String): Flow<List<ChildEntity>>

    @Query("SELECT * FROM children WHERE userId = :userId AND demo = 0 AND syncedAt IS NULL ORDER BY createdAt")
    suspend fun pending(userId: String): List<ChildEntity>

    @Query("SELECT id FROM children WHERE userId = :userId")
    suspend fun ids(userId: String): List<String>

    @Query("SELECT COUNT(*) FROM children WHERE userId = :userId AND demo = 0 AND syncedAt IS NULL")
    fun observePendingCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(child: ChildEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(child: ChildEntity)

    @Query("UPDATE children SET syncedAt = :syncedAt WHERE id = :id AND userId = :userId")
    suspend fun markSynced(userId: String, id: String, syncedAt: String)

    @Query("DELETE FROM triage_translations")
    suspend fun clearTranslations()

    @Query("DELETE FROM triages")
    suspend fun clearTriages()

    @Query("DELETE FROM children")
    suspend fun clearChildren()

    /** Removes every child with its triages and translations in one transaction. */
    @Transaction
    suspend fun clearAllRecords() {
        clearTranslations()
        clearTriages()
        clearChildren()
    }
}
