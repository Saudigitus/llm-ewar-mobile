package org.saudigitus.climasaude.presentation.triage.child.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.domain.model.ChildFollowUp
import org.saudigitus.climasaude.presentation.components.InitialsAvatar
import org.saudigitus.climasaude.presentation.components.ListCard
import org.saudigitus.climasaude.presentation.components.Tag
import org.saudigitus.climasaude.presentation.theme.Danger
import org.saudigitus.climasaude.presentation.theme.DangerContainer
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.theme.Muted
import org.saudigitus.climasaude.presentation.theme.Outline
import org.saudigitus.climasaude.presentation.theme.Teal
import org.saudigitus.climasaude.utils.text.shortDate

@Composable
internal fun ChildListCard(item: ChildFollowUp, onClick: () -> Unit) {
    val last = item.triages.firstOrNull()
    ListCard(onClick) {
        InitialsAvatar(item.child.name)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                item.child.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val details = listOfNotNull(
                item.child.ageYears?.let { if (it == 1) "1 ano" else "$it anos" },
                item.child.community?.takeIf { it.isNotBlank() }
            ).joinToString(" · ")
            if (details.isNotEmpty()) Text(
                details,
                color = Muted,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    last == null -> Tag("Sem triagem", Muted, Outline.copy(alpha = 0.5f))
                    last.dangerSigns -> Tag("Sinais de perigo", Danger, DangerContainer)
                    else -> Tag(if (item.triages.size == 1) "1 triagem" else "${item.triages.size} triagens")
                }
                last?.let {
                    Text(
                        "Última: ${shortDate(it.recordedAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted
                    )
                }
            }
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Teal)
    }
}
