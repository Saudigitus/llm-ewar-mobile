package org.saudigitus.climasaude.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.ui.text.style.TextOverflow
import org.saudigitus.climasaude.domain.model.RiskLevel
import org.saudigitus.climasaude.presentation.components.ListCard
import org.saudigitus.climasaude.presentation.components.Tag
import org.saudigitus.climasaude.presentation.theme.Outline
import org.saudigitus.climasaude.utils.text.dateRange
import org.saudigitus.climasaude.utils.text.riskLabel
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.domain.model.Alert
import org.saudigitus.climasaude.presentation.alert.components.AlertCard
import org.saudigitus.climasaude.presentation.alert.components.StatCard
import org.saudigitus.climasaude.presentation.app.AppUiState
import org.saudigitus.climasaude.utils.text.UiText
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.theme.Muted
import org.saudigitus.climasaude.presentation.theme.OnTealMuted
import org.saudigitus.climasaude.presentation.theme.Teal
import org.saudigitus.climasaude.presentation.theme.TealDark
import org.saudigitus.climasaude.presentation.theme.riskColor

@Composable
fun DashboardScreen(
    state: AppUiState, triageCount: Int, text: UiText, onSelect: (Alert) -> Unit,
    onDismiss: () -> Unit, onTriage: () -> Unit, modifier: Modifier = Modifier
) {
    val profile = state.profile ?: return
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column(
            Modifier.fillMaxWidth().background(
                TealDark,
                RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
            ).padding(horizontal = 22.dp, vertical = 24.dp)
        ) {
            Text(
                text.greeting + ", " + profile.name.ifBlank { profile.username },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text.area + ": " + profile.areas.joinToString { it.name },
                style = MaterialTheme.typography.bodyLarge, color = OnTealMuted
            )
        }
        Column(Modifier.padding(horizontal = 20.dp, vertical = 22.dp)) {
            Text(
                "Hoje",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(state.alerts.size, text.alerts, Color(0xFFE5F3EE), Modifier.weight(1f))
                StatCard(triageCount, text.triage, Color(0xFFFFF0DD), Modifier.weight(1f))
            }
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onTriage,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, null, Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Nova triagem",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(28.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text.alerts,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                if (state.alerts.isNotEmpty()) {
                    Spacer(Modifier.width(10.dp))
                    Tag(state.alerts.size.toString())
                }
                Spacer(Modifier.weight(1f))
                if (state.busy) CircularProgressIndicator(
                    Modifier.size(20.dp),
                    color = Teal,
                    strokeWidth = 2.dp
                )
            }
            state.message?.let { message ->
                TextButton(onClick = onDismiss) {
                    Text(
                        text.error(message),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            if (state.alerts.isEmpty()) {
                Surface(
                    Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Outline)
                ) {
                    Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, null, tint = riskColor(RiskLevel.GREEN))
                        Spacer(Modifier.width(12.dp))
                        Text(text.noAlerts, style = MaterialTheme.typography.bodyLarge, color = Ink)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.alerts.forEach { alert -> AlertCard(alert, onSelect) }
                }
            }
            Spacer(Modifier.height(14.dp))
        }
    }
}
