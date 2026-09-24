package org.saudigitus.climasaude.presentation.triage.child

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.presentation.components.SearchField
import org.saudigitus.climasaude.presentation.components.Tag
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.triage.TriageUiState
import org.saudigitus.climasaude.presentation.triage.child.components.ChildListCard
import org.saudigitus.climasaude.presentation.triage.child.components.EmptyState

@Composable
fun ChildListScreen(
    state: TriageUiState, onOpenChild: (String) -> Unit, onNewChild: () -> Unit,
    onSearch: (String) -> Unit, modifier: Modifier = Modifier
) {
    val total = state.children.size
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            if (total > 0) {
                Spacer(Modifier.height(14.dp))
                SearchField(state.search, onSearch, "Nome, cuidador ou bairro")
            }
            Spacer(Modifier.height(32.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Crianças",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )

                Spacer(Modifier.width(10.dp))
                Tag(total.toString())
            }
            Spacer(Modifier.height(4.dp))
        }
        if (state.filteredChildren.isEmpty()) item {
            EmptyState(state.search, onNewChild)
        }
        items(state.filteredChildren, key = { it.child.id }) { child ->
            ChildListCard(child) { onOpenChild(child.child.id) }
        }
        item { Spacer(Modifier.height(88.dp)) }
    }
}