package org.saudigitus.climasaude.presentation.triage.form

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
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
import org.saudigitus.climasaude.presentation.components.AppTextField
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.triage.TriageUiState

private const val MaxChildAge = 17

@Composable
fun NewChildForm(
    state: TriageUiState, onSave: (String, String, String?, String, String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf<String?>(null) }
    var caregiver by remember { mutableStateOf("") }
    var community by remember { mutableStateOf("") }
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
            ) { onSave(name, age, sex, caregiver, community, areaId) }
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
            FieldLabel("Sexo (opcional)")
            ChoiceChips(
                listOf("Feminino" to "Feminino", "Masculino" to "Masculino"),
                sex,
                { sex = it },
                allowClear = true
            )
        }

        FormSection("Família e morada", Icons.Filled.Home) {
            AppTextField(
                caregiver,
                { caregiver = it.take(80) },
                label = "Nome do cuidador (opcional)",
                keyboardOptions = words
            )
            AppTextField(
                community,
                { community = it.take(80) },
                label = "Bairro ou localidade (opcional)",
                leadingIcon = Icons.Filled.LocationOn,
                keyboardOptions = words.copy(imeAction = ImeAction.Done)
            )
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
