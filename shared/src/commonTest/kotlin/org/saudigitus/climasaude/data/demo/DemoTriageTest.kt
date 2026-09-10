package org.saudigitus.climasaude.data.demo

import org.saudigitus.climasaude.domain.model.CatchmentArea
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DemoTriageTest {
    @Test
    fun visitsHaveSavedGuidanceAndAreaMatchedAlertContext() {
        val alerts = DemoAlerts.forArea(CatchmentArea("pilot", "Distrito Piloto"))
        val (children, visits) = DemoTriage.records(alerts)

        assertEquals(2, children.size)
        assertTrue(children.all { it.areaId == "pilot" })
        assertEquals(3, visits.size)
        assertTrue(visits.all { !it.recommendationTitle.isNullOrBlank() && !it.recommendationSteps.isNullOrBlank() })
        assertTrue(visits.any { it.recommendationAlertIds?.contains("demo-red-pilot") == true })
    }
}
