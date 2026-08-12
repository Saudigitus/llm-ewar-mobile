package org.saudigitus.climasaude.presentation.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.theme.Muted
import org.saudigitus.climasaude.presentation.theme.Outline
import org.saudigitus.climasaude.presentation.theme.Teal
import org.saudigitus.climasaude.presentation.theme.TealSelection
import org.saudigitus.climasaude.utils.text.languageLabel

@Composable
internal fun LanguageOption(language: AppLanguage, selected: Boolean, onClick: () -> Unit) {
    Surface(
        Modifier.fillMaxWidth()
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) TealSelection else Color.White,
        border = BorderStroke(1.dp, if (selected) Teal else Outline)
    ) {
        Row(
            Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
                modifier = Modifier.padding(10.dp),
                colors = RadioButtonDefaults.colors(selectedColor = Teal)
            )
            Column {
                Text(languageLabel(language), fontWeight = FontWeight.SemiBold, color = Ink)
                Text(
                    when (language) {
                        AppLanguage.PORTUGUESE -> "Língua oficial"
                        AppLanguage.XICHANGANA -> "Sul: Maputo, Gaza"
                        AppLanguage.EMAKHUWA -> "Norte: Nampula, Niassa, Cabo Delgado"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )
            }
        }
    }
}