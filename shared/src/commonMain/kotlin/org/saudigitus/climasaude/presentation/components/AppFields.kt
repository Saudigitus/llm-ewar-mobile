package org.saudigitus.climasaude.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.presentation.theme.Muted
import org.saudigitus.climasaude.presentation.theme.Outline
import org.saudigitus.climasaude.presentation.theme.Teal
import org.saudigitus.climasaude.presentation.theme.TealSelection

@Composable
internal fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    isError: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    suffix: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean = false,
    placeholder: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value,
        onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it, color = Muted) } },
        readOnly = readOnly,
        supportingText = supportingText?.let { { Text(it) } },
        isError = isError,
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
        trailingIcon = trailingIcon,
        suffix = suffix?.let { { Text(it) } },
        singleLine = singleLine,
        minLines = minLines,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Teal,
            focusedLabelColor = Teal,
            focusedLeadingIconColor = Teal,
            cursorColor = Teal,
            unfocusedBorderColor = Outline,
            unfocusedLeadingIconColor = Muted,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorContainerColor = Color.White
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun <T> AppDropdownField(
    options: List<Pair<T, String>>,
    selected: T?,
    onSelect: (T?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    placeholder: String? = null,
    allowClear: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded, { expanded = it }, modifier) {
        AppTextField(
            options.firstOrNull { it.first == selected }?.second.orEmpty(),
            {},
            label = label,
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            leadingIcon = leadingIcon,
            placeholder = placeholder,
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(
            expanded,
            { expanded = false },
            containerColor = Color.White
        ) {
            if (allowClear && selected != null) DropdownMenuItem(
                text = { Text("Limpar seleção", color = Muted) },
                onClick = { onSelect(null); expanded = false }
            )
            options.forEach { (value, text) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text,
                            fontWeight = if (value == selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (value == selected) Teal else Color.Unspecified
                        )
                    },
                    onClick = { onSelect(value); expanded = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

/** Pill-shaped search box with a clear button; the keyboard's search key just hides the keyboard. */
@Composable
internal fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value,
        onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = Muted) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = if (value.isNotEmpty()) {
            {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Limpar pesquisa")
                }
            }
        } else null,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Teal,
            unfocusedBorderColor = Outline,
            focusedLeadingIconColor = Teal,
            unfocusedLeadingIconColor = Muted,
            focusedTrailingIconColor = Muted,
            unfocusedTrailingIconColor = Muted,
            cursorColor = Teal,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

/** White bordered card used for rows in lists. */
@Composable
internal fun ListCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Outline)
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

/** Small rounded label, e.g. "3 triagens". */
@Composable
internal fun Tag(text: String, color: Color = Teal, container: Color = TealSelection) {
    Text(
        text,
        Modifier.background(container, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        color = color,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
internal fun InitialsAvatar(name: String?, size: Dp = 44.dp) {
    Box(
        Modifier.size(size).background(TealSelection, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            initials(name),
            color = Teal,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

internal fun initials(name: String?): String =
    name.orEmpty().split(' ').filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
