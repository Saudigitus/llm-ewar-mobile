package org.saudigitus.climasaude.domain.guidance

import kotlinx.coroutines.runBlocking
import org.saudigitus.climasaude.domain.ai.LocalModel
import org.saudigitus.climasaude.domain.model.Alert
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.Disease
import org.saudigitus.climasaude.domain.model.GuidanceAction
import org.saudigitus.climasaude.domain.model.RiskLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GuidanceEngineTest {
    private val alert = Alert(
        "a", "area", "Area", Disease.MALARIA, RiskLevel.RED, 0.8,
        "2026-09-24", "2026-10-05", null, "2026-09-23"
    )

    @Test
    fun redRiskAlwaysIncludesReferralEvenWhenModelOmitsIt() = runBlocking {
        val model = object : LocalModel {
            override suspend fun selectActions(alert: Alert) = listOf(GuidanceAction.VISIT_FAMILIES)
        }
        val guidance = GuidanceEngine(model).forAlert(alert, AppLanguage.PORTUGUESE)
        assertTrue(guidance.steps.any { it.contains("Encaminhe") })
        assertTrue(guidance.urgent != null)
    }

    @Test
    fun unsupportedModelActionsCannotEnterLowRiskGuidance() = runBlocking {
        val model = object : LocalModel {
            override suspend fun selectActions(alert: Alert) =
                listOf(GuidanceAction.REFER_SUSPECTED)
        }
        val guidance = GuidanceEngine(model).forAlert(
            alert.copy(level = RiskLevel.GREEN),
            AppLanguage.PORTUGUESE
        )
        assertEquals(2, guidance.steps.size)
        assertTrue(guidance.steps.none { it.contains("Encaminhe") })
    }
}
