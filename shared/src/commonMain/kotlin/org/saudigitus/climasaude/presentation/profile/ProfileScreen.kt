package org.saudigitus.climasaude.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.UserProfile
import org.saudigitus.climasaude.presentation.components.AppIcons
import org.saudigitus.climasaude.presentation.components.InitialsAvatar
import org.saudigitus.climasaude.presentation.components.Tag
import org.saudigitus.climasaude.presentation.profile.components.AccountCard
import org.saudigitus.climasaude.presentation.profile.components.LanguageOption
import org.saudigitus.climasaude.presentation.profile.components.Section
import org.saudigitus.climasaude.presentation.theme.Canvas
import org.saudigitus.climasaude.presentation.theme.Danger
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.theme.Muted
import org.saudigitus.climasaude.presentation.theme.Outline
import org.saudigitus.climasaude.presentation.theme.Sun
import org.saudigitus.climasaude.presentation.theme.Teal
import org.saudigitus.climasaude.presentation.theme.TealSelection
import org.saudigitus.climasaude.utils.text.UiText
import org.saudigitus.climasaude.utils.text.languageLabel

@Composable
fun ProfileScreen(
    profile: UserProfile,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var confirmLogout by remember { mutableStateOf(false) }
    Column(
        modifier.fillMaxSize().background(Canvas).verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AccountCard(profile)

        Section("Área de trabalho", Icons.Filled.LocationOn) {
            profile.areas.forEach { area ->
                Text(area.name, style = MaterialTheme.typography.bodyLarge, color = Ink)
            }
            if (profile.areas.isEmpty()) Text("Sem área atribuída", color = Muted)
        }

        Section("Língua local", AppIcons.Translate) {
            Text(
                "A língua que mais usa com as famílias.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
            Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppLanguage.entries.forEach { language ->
                    LanguageOption(language, language == profile.language) {
                        onLanguageChange(language)
                    }
                }
            }
        }

        OutlinedButton(
            onClick = { confirmLogout = true },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Danger.copy(alpha = 0.4f))
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Danger, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(UiText.logout, color = Danger, fontWeight = FontWeight.SemiBold)
        }
    }

    if (confirmLogout) AlertDialog(
        onDismissRequest = { confirmLogout = false },
        title = { Text("Sair da conta?") },
        text = {
            Text(
                if (profile.demo) "Os dados de exemplo são apagados deste telemóvel."
                else "As crianças e triagens guardadas neste telemóvel são apagadas. " +
                    "Envie os dados antes de sair."
            )
        },
        confirmButton = {
            TextButton(onClick = { confirmLogout = false; onLogout() }) {
                Text(UiText.logout, color = Danger, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = { confirmLogout = false }) { Text("Cancelar", color = Teal) }
        }
    )
}





