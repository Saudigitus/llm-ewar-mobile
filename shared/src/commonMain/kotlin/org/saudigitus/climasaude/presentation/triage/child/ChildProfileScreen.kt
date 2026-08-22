package org.saudigitus.climasaude.presentation.triage.child

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import org.saudigitus.climasaude.domain.model.ChildFollowUp
import org.saudigitus.climasaude.domain.model.Triage
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.theme.Muted
import org.saudigitus.climasaude.presentation.theme.Teal
import org.saudigitus.climasaude.presentation.triage.child.components.DemographicRow
import org.saudigitus.climasaude.presentation.triage.child.components.TriageHistoryCard
import org.saudigitus.climasaude.utils.text.shortDate

@Composable
fun ChildProfileScreen(
    item: ChildFollowUp, areaName: String?, onOpenTriage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(
                        "Dados da criança",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Ink
                    )
                    Spacer(Modifier.height(12.dp))
                    DemographicRow(
                        "Idade",
                        item.child.ageYears?.let { "$it anos" } ?: "Não informada")
                    DemographicRow("Sexo", item.child.sex ?: "Não informado")
                    areaName?.let { DemographicRow("Área de cobertura", it) }
                    DemographicRow(
                        "Cuidador",
                        item.child.caregiver?.takeIf { it.isNotBlank() } ?: "Não informado")
                    DemographicRow(
                        "Localidade",
                        item.child.community?.takeIf { it.isNotBlank() } ?: "Não informada")
                    DemographicRow("Registada em", shortDate(item.child.createdAt))
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "Histórico de triagens",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                "A mais recente primeiro",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }
        if (item.triages.isEmpty()) item {
            Text(
                "Ainda sem triagens. Toque em Nova triagem para começar.",
                color = Muted
            )
        }
        items(item.triages, key = { it.id }) { triage ->
            TriageHistoryCard(triage) { onOpenTriage(triage.id) }
        }
        item { Spacer(Modifier.height(88.dp)) }
    }
}
