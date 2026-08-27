package org.saudigitus.climasaude.domain.guidance

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

class TriageRecommendationEngineTest {
    private val engine = TriageRecommendationEngine()

    @Test
    fun dangerSignsTakePriorityOverFeverAndTestStatus() {
        val recommendation =
            engine.guidance(fever = true, dangerSigns = true, malariaTest = false, referred = false,
                alerts = emptyList(), alertIds = emptyList())

        assertContains(recommendation.title, "urgente")
        assertTrue(recommendation.steps.any { it.contains("Encaminhe") })
        assertTrue(recommendation.source.contains("protocolo APE"))
    }

    @Test
    fun feverWithoutTestPromptsProtocolBasedEvaluation() {
        val recommendation = engine.guidance(
            fever = true,
            dangerSigns = false,
            malariaTest = false,
            referred = false,
            alerts = listOf("RED"),
            alertIds = listOf("alert-1")
        )

        assertContains(recommendation.body, "protocolo APE")
        assertContains(recommendation.body, "risco de malária")
        assertTrue(!recommendation.body.contains("mg"))
    }
}
