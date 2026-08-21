package org.saudigitus.climasaude.domain.guidance

import org.saudigitus.climasaude.domain.model.Child
import org.saudigitus.climasaude.domain.model.Triage
import kotlin.test.Test
import kotlin.test.assertEquals

class TriageOrderingTest {
    @Test
    fun ordersChildrenByLatestVisitAndKeepsEveryVisitNewestFirst() {
        val children = listOf(
            Child("a", "Amina", 2, "2026-09-01T00:00:00Z", true),
            Child("b", "Paulo", 4, "2026-09-02T00:00:00Z", true)
        )

        fun visit(id: String, childId: String, date: String) =
            Triage(id, childId, date, false, false, false, false, "", true)

        val records = listOf(
            visit("a-old", "a", "2026-09-10T10:00:00Z"),
            visit("b-one", "b", "2026-09-12T10:00:00Z"),
            visit("a-new", "a", "2026-09-15T10:00:00Z")
        )

        val result = organizeFollowUps(children, records)

        assertEquals(listOf("a", "b"), result.map { it.child.id })
        assertEquals(listOf("a-new", "a-old"), result.first().triages.map { it.id })
        assertEquals(3, result.sumOf { it.triages.size })
    }
}
