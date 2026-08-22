package org.saudigitus.climasaude.presentation.triage

/** One-off results of [TriageViewModel] actions that the navigation graph reacts to. */
sealed interface TriageEvent {
    data class ChildSaved(val childId: String) : TriageEvent
    data class TriageSaved(val childId: String, val triageId: String) : TriageEvent
}
