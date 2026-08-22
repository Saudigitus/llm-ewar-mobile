package org.saudigitus.climasaude.presentation.triage.child.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.theme.Muted
import org.saudigitus.climasaude.presentation.theme.Outline

@Composable
internal fun EmptyState(search: String, onNewChild: () -> Unit) {
    Surface(
        Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Outline)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                if (search.isBlank()) "Ainda sem crianças" else "Nada encontrado para “${search.trim()}”",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Spacer(Modifier.height(4.dp))
            Text(
                if (search.isBlank()) "Registe uma criança para fazer a primeira triagem."
                else "Confirme a escrita ou procure pelo cuidador.",
                color = Muted
            )
            if (search.isBlank()) {
                Spacer(Modifier.height(14.dp))
                OutlinedButton(onClick = onNewChild, shape = RoundedCornerShape(14.dp)) {
                    Icon(Icons.Filled.Add, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Registar criança")
                }
            }
        }
    }
}
