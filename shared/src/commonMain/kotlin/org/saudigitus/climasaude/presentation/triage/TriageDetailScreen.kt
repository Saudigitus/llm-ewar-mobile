package org.saudigitus.climasaude.presentation.triage

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.Child
import org.saudigitus.climasaude.domain.model.Triage
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.theme.Muted
import org.saudigitus.climasaude.presentation.theme.Teal
import org.saudigitus.climasaude.utils.text.shortDate
import org.saudigitus.climasaude.utils.text.yesNo

@Composable
fun TriageDetailScreen(
    triage: Triage, child: Child?, justSaved: Boolean, onViewHistory: () -> Unit,
    language: AppLanguage, translating: Boolean, translationError: String?,
    onTranslate: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text(
            listOfNotNull(child?.name, shortDate(triage.recordedAt)).joinToString(" · "),
            color = Muted
        )
        Spacer(Modifier.height(20.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                Text(
                    "Registo da visita",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Spacer(Modifier.height(12.dp))
                DetailRow("Febre", yesNo(triage.fever))
                DetailRow("Sinais de perigo", yesNo(triage.dangerSigns))
                DetailRow("Teste de malária realizado", yesNo(triage.malariaTest))
                DetailRow("Encaminhamento", yesNo(triage.referred))
                Spacer(Modifier.height(8.dp))
                Text("Observações", fontWeight = FontWeight.SemiBold, color = Ink)
                Text(triage.notes.ifBlank { "Sem notas." }, color = Muted)
            }
        }
        Spacer(Modifier.height(18.dp))
        RecommendationCard(triage, language, translating, translationError, onTranslate)
        Spacer(Modifier.height(20.dp))
        if (justSaved) Button(
            onClick = onViewHistory, modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) { Text("Ver histórico") }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Text("$label: $value", color = Ink, modifier = Modifier.padding(vertical = 4.dp))
}

@Composable
private fun RecommendationCard(
    triage: Triage, language: AppLanguage, translating: Boolean,
    translationError: String?, onTranslate: (AppLanguage) -> Unit
) {
    val translation = triage.translations[language]
    val steps = translation?.steps ?: triage.recommendationSteps.ifEmpty {
        triage.recommendationBody?.lines()?.map { it.trim().removePrefix("• ") }.orEmpty()
    }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (triage.dangerSigns) Color(0xFFFFE9E3) else Color(
                0xFFE5F3EE
            )
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp)) {
            Text(
                "Próximos passos",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (triage.dangerSigns) Color(0xFF9E3429) else Teal
            )
            Spacer(Modifier.height(9.dp))
            Text(
                translation?.title ?: triage.recommendationTitle ?: "Orientação pendente",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Spacer(Modifier.height(8.dp))
            if (steps.isEmpty()) Text("Consulte o protocolo APE local.", color = Ink)
            steps.forEachIndexed { index, step ->
                Text(
                    "${index + 1}. $step", color = Ink, style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
            }
            if (triage.recommendationAlertIds.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                val count = triage.recommendationAlertIds.size
                Text(
                    if (count == 1) "Inclui 1 alerta ativo na data da visita."
                    else "Inclui $count alertas ativos na data da visita.",
                    color = Muted, style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(16.dp))
            Text("Idioma da orientação", color = Ink, fontWeight = FontWeight.SemiBold)
            Row(Modifier.horizontalScroll(rememberScrollState())) {
                AppLanguage.entries.forEach { option ->
                    val label = when (option) {
                        AppLanguage.PORTUGUESE -> "Português"
                        AppLanguage.XICHANGANA -> "Xichangana"
                        AppLanguage.EMAKHUWA -> "Emakhuwa"
                    }
                    FilterChip(
                        selected = language == option,
                        onClick = { onTranslate(option) },
                        label = { Text(label) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            if (translating) Text("A traduzir…", color = Muted)
            if (translationError != null) Text(translationError, color = Color(0xFF9E3429))
            if (language != AppLanguage.PORTUGUESE && translation == null && translationError == null && !translating) {
                Text("Em português.", color = Muted)
            }
        }
    }
}
