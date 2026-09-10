package org.saudigitus.climasaude.presentation.triage.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.presentation.theme.Canvas
import org.saudigitus.climasaude.presentation.theme.Danger
import org.saudigitus.climasaude.presentation.theme.DangerContainer
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.theme.Muted
import org.saudigitus.climasaude.presentation.theme.Outline
import org.saudigitus.climasaude.presentation.theme.Teal
import org.saudigitus.climasaude.presentation.theme.TealSelection

/** Scrollable form body with the primary action pinned above the keyboard. */
@Composable
internal fun FormLayout(
    modifier: Modifier = Modifier,
    footer: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier.fillMaxSize().background(Canvas)) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content
        )
        Surface(color = Color.White, shadowElevation = 8.dp) {
            Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) { footer() }
        }
    }
}

@Composable
internal fun FormSection(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Outline)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(icon)
                Spacer(Modifier.width(10.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
            }
            content()
        }
    }
}

@Composable
internal fun IconBadge(icon: ImageVector, tint: Color = Teal, container: Color = TealSelection) {
    Box(
        Modifier.size(36.dp).background(container, CircleShape),
        contentAlignment = Alignment.Center
    ) { Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp)) }
}

@Composable
internal fun FieldLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge, color = Muted)
}

/** Single-choice chips; tapping the selected chip again clears it when [allowClear] is set. */
@Composable
internal fun <T> ChoiceChips(
    options: List<Pair<T, String>>,
    selected: T?,
    onSelect: (T?) -> Unit,
    allowClear: Boolean = false
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (value, label) ->
            val isSelected = value == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(if (isSelected && allowClear) null else value) },
                label = { Text(label) },
                leadingIcon = if (isSelected) {
                    { Icon(Icons.Filled.Check, null, Modifier.size(18.dp)) }
                } else null,
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TealSelection,
                    selectedLabelColor = Teal,
                    selectedLeadingIconColor = Teal
                )
            )
        }
    }
}

/**
 * Yes/no question with two large answer buttons. When [alarmOnYes] is set, a "Sim" answer is
 * shown in the danger colour.
 */
@Composable
internal fun YesNoQuestion(
    icon: ImageVector,
    question: String,
    hint: String?,
    value: Boolean?,
    onChange: (Boolean) -> Unit,
    alarmOnYes: Boolean = false
) {
    val alarm = alarmOnYes && value == true
    Surface(
        Modifier.fillMaxWidth(),
        color = if (alarm) DangerContainer else Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (alarm) Danger.copy(alpha = 0.4f) else Outline)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (alarm) IconBadge(icon, Danger, Color.White) else IconBadge(icon)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        question,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Ink
                    )
                    hint?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = Muted)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AnswerButton(
                    "Sim",
                    value == true,
                    if (alarmOnYes) Danger else Teal,
                    Modifier.weight(1f)
                ) {
                    onChange(true)
                }
                AnswerButton("Não", value == false, Teal, Modifier.weight(1f)) { onChange(false) }
            }
        }
    }
}

@Composable
private fun AnswerButton(
    label: String,
    selected: Boolean,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    val content: @Composable () -> Unit = {
        if (selected) {
            Icon(Icons.Filled.Check, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(label, fontWeight = FontWeight.SemiBold)
    }
    if (selected) Button(
        onClick,
        modifier.height(48.dp),
        shape = shape,
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) { content() }
    else OutlinedButton(
        onClick,
        modifier.height(48.dp),
        shape = shape,
        border = BorderStroke(1.dp, Outline),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Ink)
    ) { content() }
}

@Composable
internal fun InfoNote(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconBadge(Icons.Filled.Info)
        Spacer(Modifier.width(10.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Muted)
    }
}

@Composable
internal fun AlertBanner(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth()
            .background(DangerContainer, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Filled.Warning, null, tint = Danger, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, color = Danger, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun FormError(error: String?) {
    if (error != null) AlertBanner(error)
}

@Composable
internal fun SaveButton(label: String, enabled: Boolean, busy: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled && !busy,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Teal)
    ) {
        if (busy) CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 2.dp,
            modifier = Modifier.size(22.dp)
        )
        else Text(label, fontWeight = FontWeight.Bold)
    }
}
