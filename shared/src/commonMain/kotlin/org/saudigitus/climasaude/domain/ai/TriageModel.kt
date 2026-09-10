package org.saudigitus.climasaude.domain.ai

/** On-device language model used to write and translate triage recommendations. */
interface TriageModel {
    suspend fun complete(prompt: String): String?
}
