package org.saudigitus.climasaude

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import org.saudigitus.climasaude.domain.guidance.GuidanceText
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.RiskLevel
import org.saudigitus.climasaude.domain.repository.AlertRepository
import org.saudigitus.climasaude.platform.NudgeScheduler
import org.saudigitus.climasaude.presentation.ClimaSaudeApp

class MainActivity : ComponentActivity() {
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) lifecycleScope.launch {
                val koin = GlobalContext.get()
                val scheduler = koin.get<NudgeScheduler>()
                val message = GuidanceText.forLanguage(AppLanguage.PORTUGUESE).visitFamilies
                koin.get<AlertRepository>().alerts.first()
                    .filter { !it.demo && it.level != RiskLevel.GREEN }.forEach { alert ->
                        scheduler.schedule(alert.id, alert.areaName, alert.startsAt, message)
                    }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Light status-bar icons over the teal app bar and login header.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            ClimaSaudeApp(onReadyForNotifications = {
                if (Build.VERSION.SDK_INT >= 33 &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            })
        }
    }
}
