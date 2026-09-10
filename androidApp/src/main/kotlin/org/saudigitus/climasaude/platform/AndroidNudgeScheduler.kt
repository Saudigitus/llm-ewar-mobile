package org.saudigitus.climasaude.platform

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import java.time.LocalDate
import org.saudigitus.climasaude.R

class AndroidNudgeScheduler(private val context: Context) : NudgeScheduler {
    private val shown = context.getSharedPreferences("alert_notifications", Context.MODE_PRIVATE)

    override fun schedule(id: String, areaName: String, startsAt: String, message: String) {
        val starts = runCatching { LocalDate.parse(startsAt.take(10)) }.getOrNull() ?: return
        val today = LocalDate.now()
        if (starts.isBefore(today) || starts.isAfter(today.plusDays(10)) || shown.getBoolean(
                id,
                false
            )
        ) return
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(
                "climate-alerts",
                "Clima Saúde Community",
                NotificationManager.IMPORTANCE_HIGH
            )
        )
        val notification = NotificationCompat.Builder(context, "climate-alerts")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Clima Saúde Community: $areaName")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        manager.notify(id.hashCode(), notification)
        shown.edit { putBoolean(id, true) }
    }

    override fun clear() {
        shown.edit { clear() }
    }
}
