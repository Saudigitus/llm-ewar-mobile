package org.saudigitus.climasaude.data.prompt

import org.saudigitus.climasaude.data.local.entity.AlertEntity
import org.saudigitus.climasaude.data.local.entity.ChildEntity
import org.saudigitus.climasaude.data.local.entity.TriageEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TriagePromptTest {
    private val visit = TriageEntity(
        "visit", "ape", "child", "2026-09-23T09:00:00Z", true, false,
        false, false, "Febre", false
    )

    private fun alert(id: String, start: String, end: String, level: String = "RED") = AlertEntity(
        id, "area", "Zona Norte", "MALARIA", level, 0.8, start, end,
        null, "2026-09-22T10:00:00Z", false
    )

    @Test
    fun includesOnlyAlertsActiveOnVisitDate() {
        val matching = TriagePrompt.matchingAlerts(visit, listOf(
            alert("before", "2026-09-01", "2026-09-22"),
            alert("active", "2026-09-23", "2026-09-24"),
            alert("after", "2026-09-24", "2026-09-30")
        ))
        assertEquals(listOf("active"), matching.map { it.id })
    }

    @Test
    fun promptUsesSavedVisitAndAlertsWithoutChildIdentity() {
        val child = ChildEntity("child", "ape", "Amina", 2, "2026-09-01", false,
            "Feminino", "Sara", "Mavalo")
        val prompt = TriagePrompt.recommendation(child, visit, listOf(alert("active", "2026-09-23", "2026-09-24")))
        assertTrue(prompt.contains("febre: sim"))
        assertTrue(prompt.contains("risco RED"))
        assertFalse(prompt.contains("Amina"))
        assertFalse(prompt.contains("Sara"))
    }

    @Test
    fun rejectsUnstructuredModelOutput() {
        assertEquals(null, TriagePrompt.parse("Tome medicamento agora", "IA local"))
        val parsed = TriagePrompt.parse(
            "TITULO: Acompanhar\nPASSO 1: Avalie a febre.\nPASSO 2: Siga o protocolo APE.",
            "IA local", listOf("active")
        )
        assertEquals(listOf("active"), parsed?.alertIds)
        assertEquals(2, parsed?.steps?.size)
    }
}
