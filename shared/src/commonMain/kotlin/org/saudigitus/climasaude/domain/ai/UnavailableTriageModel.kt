package org.saudigitus.climasaude.domain.ai

class UnavailableTriageModel : TriageModel {
    override suspend fun complete(prompt: String): String? = null
}
