package org.saudigitus.climasaude.domain.repository

import kotlinx.coroutines.flow.Flow
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.ChildFollowUp
import org.saudigitus.climasaude.domain.model.Triage
import org.saudigitus.climasaude.domain.model.TriageGuidance

interface TriageRepository {
    /** Every child of the active profile with their triages, newest first. */
    val followUps: Flow<List<ChildFollowUp>>

    /** Children and triages not yet uploaded. */
    val pendingCount: Flow<Int>

    suspend fun addTriage(
        childId: String, fever: Boolean, dangerSigns: Boolean, malariaTest: Boolean,
        referred: Boolean, notes: String
    ): Triage

    suspend fun refineRecommendation(
        triageId: String,
        onDraft: (TriageGuidance) -> Unit = {}
    ): Boolean

    /** Translates and caches a saved recommendation. Returns false when unavailable. */
    suspend fun translate(triageId: String, language: AppLanguage): Boolean
}
