package org.saudigitus.climasaude.data.demo

import org.saudigitus.climasaude.domain.model.CatchmentArea
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DemoAlertsTest {
    @Test
    fun sampleAlertsAreMarkedAndScopedToTheDemoArea() {
        val alerts = DemoAlerts.forArea(CatchmentArea("demo-area", "Área de demonstração"))
        assertEquals(3, alerts.size)
        assertEquals(setOf("RED", "YELLOW", "GREEN"), alerts.map { it.level }.toSet())
        assertTrue(alerts.all { it.demo && it.areaId == "demo-area" && it.id.startsWith("demo-") })
    }
}
