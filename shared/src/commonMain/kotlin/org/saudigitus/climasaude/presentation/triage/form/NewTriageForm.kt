package org.saudigitus.climasaude.presentation.triage.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.domain.model.Child
import org.saudigitus.climasaude.presentation.components.AppTextField
import org.saudigitus.climasaude.presentation.components.InitialsAvatar
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.theme.Muted
import org.saudigitus.climasaude.presentation.theme.Outline
import org.saudigitus.climasaude.presentation.theme.Teal
import org.saudigitus.climasaude.presentation.theme.TealSelection
import org.saudigitus.climasaude.presentation.triage.TriageUiState

private const val NotesLimit = 500

@Composable
fun NewTriageForm(
    child: Child?,
    areaName: String?,
    state: TriageUiState,
    onSave: (Boolean, Boolean, Boolean, Boolean, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var fever by remember { mutableStateOf<Boolean?>(null) }
    var danger by remember { mutableStateOf<Boolean?>(null) }
    var test by remember { mutableStateOf<Boolean?>(null) }
    var referred by remember { mutableStateOf<Boolean?>(null) }
    var notes by remember { mutableStateOf("") }
    val answered = listOf(fever, danger, test, referred).count { it != null }
    val complete = answered == 4

    FormLayout(
        modifier,
        footer = {
            SaveButton(
                when (val missing = 4 - answered) {
                    0 -> "Guardar triagem"
                    1 -> "Falta 1 pergunta"
                    else -> "Faltam $missing perguntas"
                },
                enabled = complete,
                busy = state.busy
            ) { onSave(fever == true, danger == true, test == true, referred == true, notes) }
        }
    ) {
        ChildHeader(child, areaName)
        Progress(answered)

        YesNoQuestion(
            Icons.Filled.Warning,
            "Observou sinais de perigo?",
            "Não bebe nem mama, vomita tudo, teve convulsões ou está muito mole.",
            danger,
            { danger = it },
            alarmOnYes = true
        )
        if (danger == true) {
            AlertBanner("Referir já para a unidade sanitária, segundo o protocolo.")
        }
        YesNoQuestion(
            Icons.Filled.Favorite,
            "A criança tem febre?",
            "Agora ou nos últimos dias, segundo o cuidador.",
            fever,
            { fever = it }
        )
        YesNoQuestion(
            Icons.Filled.Search,
            "Fez teste de malária?",
            "Teste rápido (TDR) nesta visita.",
            test,
            { test = it }
        )
        YesNoQuestion(
            Icons.Filled.Place,
            "Referiu para a unidade sanitária?",
            null,
            referred,
            { referred = it }
        )

        FormSection("Observações", Icons.Filled.Edit) {
            AppTextField(
                notes,
                { notes = it.take(NotesLimit) },
                label = "Notas (opcional)",
                singleLine = false,
                minLines = 3,
                supportingText = "${notes.length}/$NotesLimit",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
        }

        FormError(state.error)
    }
}

@Composable
private fun ChildHeader(child: Child?, areaName: String?) {
    Surface(
        Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Outline)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            InitialsAvatar(child?.name, 48.dp)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    child?.name.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                val details = listOfNotNull(
                    child?.ageYears?.let { "$it anos" },
                    child?.sex,
                    areaName
                ).joinToString(" · ")
                if (details.isNotEmpty()) Text(details, color = Muted)
                Text(
                    "Triagem de hoje",
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )
            }
        }
    }
}

@Composable
private fun Progress(answered: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            "$answered de 4 respondidas",
            style = MaterialTheme.typography.labelLarge,
            color = Muted
        )
        LinearProgressIndicator(
            progress = { answered / 4f },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = Teal,
            trackColor = TealSelection,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
            drawStopIndicator = {}
        )
    }
}
