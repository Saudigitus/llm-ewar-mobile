package org.saudigitus.climasaude.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.saudigitus.climasaude.data.local.entity.TriageTranslationEntity

@Dao
interface TriageTranslationDao {
    @Query("SELECT * FROM triage_translations WHERE userId = :userId")
    fun observeForUser(userId: String): Flow<List<TriageTranslationEntity>>

    @Query("SELECT * FROM triage_translations WHERE userId = :userId AND triageId = :triageId AND language = :language LIMIT 1")
    suspend fun find(
        userId: String,
        triageId: String,
        language: String
    ): TriageTranslationEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(translation: TriageTranslationEntity)
}
