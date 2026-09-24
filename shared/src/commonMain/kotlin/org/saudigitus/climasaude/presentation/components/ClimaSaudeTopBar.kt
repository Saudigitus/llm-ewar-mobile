package org.saudigitus.climasaude.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.saudigitus.climasaude.utils.text.UiText
import org.saudigitus.climasaude.presentation.theme.OnTealMuted
import org.saudigitus.climasaude.presentation.theme.TealDark

/**
 * App bar shared by every signed-in screen. Top-level screens show the brand and the sync
 * action; nested screens show a back arrow instead of the brand.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClimaSaudeTopBar(
    title: String,
    subtitle: String?,
    onBack: (() -> Unit)?,
    syncing: Boolean,
    onSync: (() -> Unit)?
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBack == null) {
                    BrandMark(inverse = true, size = 34.dp)
                    Spacer(Modifier.width(12.dp))
                }
                Column {
                    Text(
                        title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    subtitle?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnTealMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if (onBack != null) IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = UiText.close
                )
            }
        },
        actions = {
            if (onSync != null) {
                if (syncing) CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.padding(horizontal = 12.dp).size(24.dp)
                )
                else IconButton(onClick = onSync) {
                    Icon(Icons.Default.Sync, contentDescription = UiText.sync)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = TealDark,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}
