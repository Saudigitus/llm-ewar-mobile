package org.saudigitus.climasaude.presentation.triage.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.presentation.components.AppDropdownField
import org.saudigitus.climasaude.presentation.components.AppTextField
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.theme.Outline
import org.saudigitus.climasaude.presentation.theme.Teal
import org.saudigitus.climasaude.presentation.triage.TriageUiState
import org.saudigitus.climasaude.utils.text.coordinates
import org.saudigitus.climasaude.utils.text.precision

private const val MaxChildAge = 17

@Composable
fun NewChildForm(
    state: TriageUiState,
    onLocate: () -> Unit,
    onSave: (String, String, String?, String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf<String?>(null) }
    var caregiver by remember { mutableStateOf("") }
    var areaId by remember(state.areas) { mutableStateOf(state.areas.singleOrNull()?.id) }
    val ageInvalid = age.toIntOrNull()?.let { it > MaxChildAge } == true
    val words = KeyboardOptions(
        capitalization = KeyboardCapitalization.Words,
        imeAction = ImeAction.Next
    )

    FormLayout(
        modifier,
        footer = {
            SaveButton(
                "Guardar e fazer triagem",
                enabled = name.isNotBlank() && !ageInvalid && areaId != null,
                busy = state.busy
            ) { onSave(name, age, sex, caregiver, areaId) }
        }
    ) {
        InfoNote("Depois de guardar, passa logo para a primeira triagem.")

        FormSection("Criança", Icons.Filled.Face) {
            AppTextField(
                name,
                { name = it.take(80) },
                label = "Nome da criança *",
                leadingIcon = Icons.Filled.Person,
                keyboardOptions = words
            )
            AppTextField(
                age,
                { age = it.filter(Char::isDigit).take(2) },
                label = "Idade (opcional)",
                suffix = "anos",
                isError = ageInvalid,
                supportingText = if (ageInvalid) "Só crianças até $MaxChildAge anos."
                else "Até $MaxChildAge anos",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
            AppDropdownField(
                listOf("Feminino" to "Feminino", "Masculino" to "Masculino"),
                sex,
                { sex = it },
                label = "Sexo (opcional)",
                placeholder = "Selecionar",
                allowClear = true
            )
        }

        FormSection("Família e morada", Icons.Filled.Home) {
            AppTextField(
                caregiver,
                { caregiver = it.take(80) },
                label = "Nome do cuidador (opcional)",
                keyboardOptions = words.copy(imeAction = ImeAction.Done)
            )
            LocationField(state, onLocate)
            when {
                state.areas.size > 1 -> {
                    FieldLabel("Área de cobertura *")
                    ChoiceChips(state.areas.map { it.id to it.name }, areaId, { areaId = it })
                }

                state.areas.size == 1 -> {
                    FieldLabel("Área de cobertura")
                    Text(state.areas.single().name, color = Ink, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        FormError(state.error)
    }
}

@Composable
private fun LocationField(state: TriageUiState, onLocate: () -> Unit) {
    val point = state.childLocation
    val precise = point?.accuracyMeters?.let { it <= 7.0 } == true
    AppTextField(
        point?.let { coordinates(it.latitude, it.longitude) }.orEmpty(),
        {},
        label = "Coordenadas da casa (opcional)",
        leadingIcon = Icons.Filled.LocationOn,
        readOnly = true,
        placeholder = when {
            state.locating -> "A obter coordenadas…"
            state.locationPermissionNeeded -> "Sem acesso à localização"
            else -> null
        },
        isError = state.locationError != null,
        supportingText = when {
            state.locationError != null -> state.locationError
            point != null && state.locating -> "${precision(point.accuracyMeters)} · a melhorar…"
            point != null && !precise -> "${precision(point.accuracyMeters)} · acima de 7 m"
            point != null -> precision(point.accuracyMeters)
            state.locating -> "À espera do sinal GPS…"
            else -> null
        },
        trailingIcon = if (state.locating) {
            {
                CircularProgressIndicator(
                    color = Teal,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else null
    )
    if (!state.locating && (state.locationPermissionNeeded || state.locationError != null || (point != null && !precise))) {
        OutlinedButton(
            onLocate,
            Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Outline),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Teal)
        ) {
            Icon(Icons.Filled.LocationOn, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                if (state.locationPermissionNeeded) "Permitir localização" else "Tentar de novo",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
