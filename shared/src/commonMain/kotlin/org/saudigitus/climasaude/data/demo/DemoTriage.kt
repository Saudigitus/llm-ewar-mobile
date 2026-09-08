package org.saudigitus.climasaude.data.demo

import kotlinx.serialization.json.Json
import org.saudigitus.climasaude.data.local.entity.AlertEntity
import org.saudigitus.climasaude.data.local.entity.ChildEntity
import org.saudigitus.climasaude.data.local.entity.TriageEntity
import org.saudigitus.climasaude.data.prompt.TriagePrompt
import org.saudigitus.climasaude.domain.guidance.TriageRecommendationEngine
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

object DemoTriage {
    @OptIn(ExperimentalTime::class)
    fun records(alerts: List<AlertEntity>): Pair<List<ChildEntity>, List<TriageEntity>> {
        val now = Clock.System.now()
        val children = listOf(
            ChildEntity(
                "demo-child-amina",
                "demo-ape",
                "Amina Macuácua",
                2,
                (now - 8.days).toString(),
                true,
                "Feminino",
                "Sara Macuácua",
                "Comunidade de Mavalo",
                areaId = alerts.firstOrNull()?.areaId
            ),
            ChildEntity(
                "demo-child-paulo", "demo-ape", "Paulo Sitoe", 4, (now - 12.days).toString(), true,
                "Masculino", "João Sitoe", "Comunidade de Nhacutse",
                areaId = alerts.firstOrNull()?.areaId
            )
        )
        val triages = listOf(
            TriageEntity(
                "demo-triage-amina-1",
                "demo-ape",
                "demo-child-amina",
                (now - 7.days).toString(),
                true,
                false,
                true,
                false,
                "Febre desde a noite anterior. Cuidadora relata apetite reduzido; teste de malária realizado. Sem sinais de perigo observados.",
                true
            ),
            TriageEntity(
                "demo-triage-amina-2",
                "demo-ape",
                "demo-child-amina",
                (now - 1.days).toString(),
                false,
                false,
                false,
                false,
                "Cuidadora relata melhoria. Sem febre hoje. Reforçadas orientações para observar novos sintomas e voltar se houver agravamento.",
                true
            ),
            TriageEntity(
                "demo-triage-paulo-1",
                "demo-ape",
                "demo-child-paulo",
                (now - 3.days).toString(),
                false,
                false,
                false,
                false,
                "Visita de rotina após chuva intensa. Criança ativa, sem febre ou sinais de perigo. Próxima visita combinada com o cuidador.",
                true
            )
        )
        val engine = TriageRecommendationEngine()
        val withGuidance = triages.map { triage ->
            val matching = TriagePrompt.matchingAlerts(triage, alerts)
            val guidance = engine.guidance(
                triage.fever, triage.dangerSigns, triage.malariaTest, triage.referred,
                matching.map { it.level }, matching.map { it.id }
            )
            triage.copy(
                recommendationTitle = guidance.title,
                recommendationBody = guidance.body,
                recommendationSource = guidance.source,
                recommendationSteps = Json.encodeToString(guidance.steps),
                recommendationAlertIds = Json.encodeToString(guidance.alertIds)
            )
        }
        return children to withGuidance
    }
}
