package org.saudigitus.climasaude.domain.guidance

import org.saudigitus.climasaude.domain.ai.LocalModel
import org.saudigitus.climasaude.domain.model.Alert
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.Guidance
import org.saudigitus.climasaude.domain.model.GuidanceAction
import org.saudigitus.climasaude.domain.model.RiskLevel

class GuidanceEngine(private val model: LocalModel) {
    suspend fun forAlert(alert: Alert, language: AppLanguage): Guidance {
        val text = GuidanceText.forLanguage(language)
        val defaults = when (alert.level) {
            RiskLevel.GREEN -> listOf(GuidanceAction.KEEP_WATCH, GuidanceAction.CHECK_SUPPLIES)
            RiskLevel.YELLOW -> listOf(
                GuidanceAction.VISIT_FAMILIES,
                GuidanceAction.CHECK_FEVER,
                GuidanceAction.CHECK_SUPPLIES
            )

            RiskLevel.RED -> listOf(
                GuidanceAction.VISIT_FAMILIES,
                GuidanceAction.CHECK_FEVER,
                GuidanceAction.REFER_SUSPECTED
            )
        }
        val suggested = runCatching { model.selectActions(alert) }.getOrNull()
        val selected =
            suggested?.filter { it in defaults }?.distinct()?.take(3)?.takeIf { it.isNotEmpty() }
                ?: defaults
        val actions =
            if (alert.level == RiskLevel.RED && GuidanceAction.REFER_SUSPECTED !in selected) {
                (selected + GuidanceAction.REFER_SUSPECTED).takeLast(3)
            } else selected
        val steps = actions.map { action ->
            when (action) {
                GuidanceAction.KEEP_WATCH -> text.keepWatch
                GuidanceAction.CHECK_SUPPLIES -> text.checkSupplies
                GuidanceAction.VISIT_FAMILIES -> text.visitFamilies
                GuidanceAction.CHECK_FEVER -> text.checkFever
                GuidanceAction.REFER_SUSPECTED -> text.referSuspected
            }
        }
        val headline = when (alert.level) {
            RiskLevel.GREEN -> text.lowRisk
            RiskLevel.YELLOW -> text.risingRisk
            RiskLevel.RED -> text.highRisk
        }
        return Guidance(
            headline, steps, if (alert.level == RiskLevel.RED) text.dangerSigns else null,
            if (suggested == null) "draft-protocol-rules" else "model-ranked-draft-rules"
        )
    }
}
