package org.saudigitus.climasaude.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ClimaSaudeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Teal,
            background = Canvas,
            surface = Color.White,
            onSurface = Ink
        ),
        content = content
    )
}
