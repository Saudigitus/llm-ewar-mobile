package org.saudigitus.climasaude.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.presentation.components.BrandMark
import org.saudigitus.climasaude.presentation.theme.TealDark
import org.saudigitus.climasaude.presentation.theme.TealLight
import org.saudigitus.climasaude.utils.text.UiText

@Composable
fun SplashScreen() {
    Box(
        Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(TealDark, TealLight)))
    ) {
        Column(
            Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BrandMark(inverse = true, size = 96.dp)
            Spacer(Modifier.height(24.dp))
            Text(
                UiText.appName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                UiText.tagline,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFE5F6EF),
                textAlign = TextAlign.Center
            )
        }
        CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 2.dp,
            modifier = Modifier.align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 48.dp)
                .size(28.dp)
        )
    }
}
