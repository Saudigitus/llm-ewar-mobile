package org.saudigitus.climasaude.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.domain.error.AppError
import org.saudigitus.climasaude.presentation.components.AppIcons
import org.saudigitus.climasaude.presentation.components.AppTextField
import org.saudigitus.climasaude.presentation.components.BrandMark
import org.saudigitus.climasaude.utils.text.UiText
import org.saudigitus.climasaude.presentation.theme.Canvas
import org.saudigitus.climasaude.presentation.theme.Danger
import org.saudigitus.climasaude.presentation.theme.Ink
import org.saudigitus.climasaude.presentation.theme.Muted
import org.saudigitus.climasaude.presentation.theme.Outline
import org.saudigitus.climasaude.presentation.theme.TealDark
import org.saudigitus.climasaude.presentation.theme.TealLight

@Composable
fun LoginScreen(
    busy: Boolean,
    message: AppError?,
    onLogin: (String, String) -> Unit,
    onDemo: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val canSubmit = !busy && username.isNotBlank() && password.isNotBlank()
    val submit = {
        focusManager.clearFocus()
        onLogin(username, password)
    }
    Column(
        Modifier.fillMaxSize().background(Color.White)
            .navigationBarsPadding().imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            Modifier.fillMaxWidth().heightIn(min = 280.dp)
                .background(Brush.verticalGradient(listOf(TealDark, TealLight)))
                .padding(28.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            BrandMark(true)
            Spacer(Modifier.height(22.dp))
            Text(
                UiText.appName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Text(
                UiText.tagline,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFE5F6EF)
            )
        }
        Column(
            Modifier.fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Text(
                "Entrar",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Use o mesmo utilizador e senha do DHIS2.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
            Spacer(Modifier.height(24.dp))
            AppTextField(
                username,
                { username = it.trim() },
                UiText.username,
                leadingIcon = Icons.Filled.Person,
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(14.dp))
            AppTextField(
                password,
                { password = it },
                UiText.password,
                leadingIcon = Icons.Filled.Lock,
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) AppIcons.VisibilityOff else AppIcons.Visibility,
                            contentDescription = if (showPassword) "Esconder senha" else "Mostrar senha"
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { if (canSubmit) submit() })
            )
            if (message != null) {
                Spacer(Modifier.height(12.dp))
                Text(UiText.error(message), color = Danger, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = submit,
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (busy) CircularProgressIndicator(
                    Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                ) else Text(
                    UiText.continueText,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(Modifier.weight(1f), color = Outline)
                Text(
                    "ou",
                    Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
                HorizontalDivider(Modifier.weight(1f), color = Outline)
            }
            Spacer(Modifier.height(14.dp))
            OutlinedButton(
                onClick = onDemo, enabled = !busy, modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text(UiText.demo, fontWeight = FontWeight.SemiBold) }
            Spacer(Modifier.height(10.dp))
            Text(
                "Usa dados de exemplo, só neste telemóvel. Nada é enviado.",
                style = MaterialTheme.typography.bodySmall,
                color = Muted
            )
        }
    }
}
