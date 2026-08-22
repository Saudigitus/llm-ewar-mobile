package org.saudigitus.climasaude.presentation.triage.child.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.domain.model.Triage
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.theme.Muted
import org.saudigitus.climasaude.presentation.theme.Teal
import org.saudigitus.climasaude.utils.text.shortDate

@Composable
internal fun TriageHistoryCard(triage: Triage, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    shortDate(triage.recordedAt),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Spacer(Modifier.height(5.dp))
                Text(triage.recommendationTitle ?: "Ver triagem", color = Muted)
            }
            Text("›", style = MaterialTheme.typography.headlineMedium, color = Teal)
        }
    }
}