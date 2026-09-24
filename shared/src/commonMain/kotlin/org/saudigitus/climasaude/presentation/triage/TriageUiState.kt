package org.saudigitus.climasaude.presentation.triage

import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.CatchmentArea
import org.saudigitus.climasaude.domain.model.ChildFollowUp
import org.saudigitus.climasaude.domain.model.GeoPoint
import org.saudigitus.climasaude.domain.model.Triage
import org.saudigitus.climasaude.domain.model.TriageGuidance

data class TriageUiState(
    val children: List<ChildFollowUp> = emptyList(),
    val areas: List<CatchmentArea> = emptyList(),
    val selectedTriageId: String? = null,
    val justSavedTriage: Triage? = null,
    val search: String = "",
    val pendingCount: Int = 0,
    val busy: Boolean = false,
    val error: String? = null,
    val recommendationLanguage: AppLanguage = AppLanguage.PORTUGUESE,
    val translating: Boolean = false,
    val translationError: String? = null,
    val generatingIds: Set<String> = emptySet(),
    val drafts: Map<String, TriageGuidance> = emptyMap(),
    val childLocation: GeoPoint? = null,
    val locating: Boolean = false,
    val locationError: String? = null,
    val locationPermissionNeeded: Boolean = false
) {
    val triageCount: Int get() = children.sumOf { it.triages.size }
    val selectedTriage: Triage? get() = selectedTriageId?.let(::triage)
    val filteredChildren: List<ChildFollowUp>
        get() {
            val query = search.trim().searchKey()
            if (query.isEmpty()) return children
            return children.filter { item ->
                listOfNotNull(item.child.name, item.child.caregiver, item.child.community)
                    .any { it.searchKey().contains(query) }
            }
        }

    fun child(id: String): ChildFollowUp? = children.firstOrNull { it.child.id == id }

    /** Falls back to the triage just saved until the database flow emits it. */
    fun triage(id: String): Triage? =
        children.firstNotNullOfOrNull { child -> child.triages.firstOrNull { it.id == id } }
            ?: justSavedTriage?.takeIf { it.id == id }
}

/** Lowercase, accent-insensitive key so "joao" finds "João". */
private fun String.searchKey(): String = lowercase().map { char ->
    when (char) {
        'á', 'à', 'â', 'ã', 'ä' -> 'a'
        'é', 'è', 'ê', 'ë' -> 'e'
        'í', 'ì', 'î', 'ï' -> 'i'
        'ó', 'ò', 'ô', 'õ', 'ö' -> 'o'
        'ú', 'ù', 'û', 'ü' -> 'u'
        'ç' -> 'c'
        else -> char
    }
}.joinToString("")
