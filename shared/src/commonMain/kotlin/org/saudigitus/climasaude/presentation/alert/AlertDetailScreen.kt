package org.saudigitus.climasaude.presentation.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.domain.model.Alert
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.Guidance
import org.saudigitus.climasaude.presentation.components.RiskPill
import org.saudigitus.climasaude.utils.text.UiText
import org.saudigitus.climasaude.presentation.theme.Canvas
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.theme.Muted
import org.saudigitus.climasaude.presentation.theme.Teal
import org.saudigitus.climasaude.presentation.theme.riskColor

@Composable
fun AlertDetailScreen(
    alert: Alert,
    guidance: Guidance?,
    language: AppLanguage,
    text: UiText,
    onTranslate: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().background(Canvas).verticalScroll(rememberScrollState())) {
        Column(
            Modifier.fillMaxWidth()
                .background(Color.White, RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            RiskPill(alert.level)
            Spacer(Modifier.height(14.dp))
            Text(
                text.malaria + " · " + alert.areaName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text.period + ": " + alert.startsAt.take(10) + " – " + alert.endsAt.take(10),
                style = MaterialTheme.typography.bodyMedium, color = Muted
            )
            alert.probability?.let { probability ->
                Spacer(Modifier.height(6.dp))
                Text(
                    "Probabilidade estimada: ${(probability * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium, color = Muted
                )
            }
            alert.rainfallNote?.takeIf { it.isNotBlank() }?.let { note ->
                Spacer(Modifier.height(8.dp))
                Text(note, style = MaterialTheme.typography.bodyMedium, color = Ink)
            }
            Spacer(Modifier.height(14.dp))
        }
        Column(Modifier.padding(20.dp)) {
            Text(
                text.translate,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                TranslationOption(
                    text.portuguese,
                    language == AppLanguage.PORTUGUESE
                ) { onTranslate(AppLanguage.PORTUGUESE) }
                Spacer(Modifier.width(8.dp))
                TranslationOption(
                    text.xichangana,
                    language == AppLanguage.XICHANGANA
                ) { onTranslate(AppLanguage.XICHANGANA) }
                Spacer(Modifier.width(8.dp))
                TranslationOption(text.emakhuwa, language == AppLanguage.EMAKHUWA) {
                    onTranslate(
                        AppLanguage.EMAKHUWA
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text.steps,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Spacer(Modifier.height(12.dp))
            if (guidance == null) {
                CircularProgressIndicator(Modifier.size(28.dp))
            } else {
                Text(
                    guidance.headline,
                    style = MaterialTheme.typography.titleMedium,
                    color = riskColor(alert.level),
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
                guidance.steps.forEachIndexed { index, step ->
                    StepCard(index + 1, step)
                    Spacer(Modifier.height(10.dp))
                }
                guidance.urgent?.let { urgent ->
                    Spacer(Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE9E3)),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(18.dp)) {
                            Text(
                                text.urgent, style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF9E3429), fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(urgent, style = MaterialTheme.typography.bodyLarge, color = Ink)
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun TranslationOption(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(label) })
}

@Composable
private fun StepCard(number: Int, instruction: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(38.dp).background(Color(0xFFE0F3EE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(number.toString(), color = Teal, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(14.dp))
            Text(
                instruction,
                Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = Ink
            )
        }
    }
}
