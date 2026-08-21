package org.saudigitus.climasaude.domain.model

data class ChildFollowUp(val child: Child, val triages: List<Triage>) {
    val lastVisit: String? get() = triages.firstOrNull()?.recordedAt
}
