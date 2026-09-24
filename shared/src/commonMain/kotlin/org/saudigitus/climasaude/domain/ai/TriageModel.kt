package org.saudigitus.climasaude.domain.ai

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** On-device language model used to write and translate triage recommendations. */
interface TriageModel {
    suspend fun complete(prompt: String): String?

    fun stream(prompt: String): Flow<String> = flow { complete(prompt)?.let { emit(it) } }
}
