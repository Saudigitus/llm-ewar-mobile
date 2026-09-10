package org.saudigitus.climasaude.domain.model

data class Guidance(
    val headline: String,
    val steps: List<String>,
    val urgent: String?,
    val source: String
)
