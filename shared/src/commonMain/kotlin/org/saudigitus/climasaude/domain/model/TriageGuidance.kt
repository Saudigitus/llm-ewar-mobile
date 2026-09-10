package org.saudigitus.climasaude.domain.model

data class TriageGuidance(
    val title: String,
    val steps: List<String>,
    val source: String,
    val alertIds: List<String> = emptyList()
) {
    val body: String get() = steps.joinToString("\n") { "• $it" }
}
