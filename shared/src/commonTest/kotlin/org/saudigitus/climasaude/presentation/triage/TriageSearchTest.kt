package org.saudigitus.climasaude.presentation.triage

import org.saudigitus.climasaude.domain.model.Child
import org.saudigitus.climasaude.domain.model.ChildFollowUp
import kotlin.test.Test
import kotlin.test.assertEquals

class TriageSearchTest {
    @Test
    fun findsChildByCaregiverWithoutTypingAccents() {
        val children = listOf(
            ChildFollowUp(
                Child(
                    "a", "Amina", 2, "2026-09-01", true,
                    caregiver = "João M."
                ), emptyList()
            )
        )

        val matches = TriageUiState(children = children, search = "joao").filteredChildren

        assertEquals(listOf("a"), matches.map { it.child.id })
    }
}
