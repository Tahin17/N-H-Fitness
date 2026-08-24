package com.aegisfit.app.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.aegisfit.app.MainActivity
import com.aegisfit.app.R

object NotificationHelper {

    const val CHANNEL_GOALS_ID = "aegisfit_daily_goals"
    const val CHANNEL_CARE_ID = "aegisfit_daily_care"
    const val CHANNEL_HYDRATION_ID = "nht_hydration_reminders"

    const val NOTIFICATION_ID_CARE_BASE = 7000
    const val NOTIFICATION_ID_GOAL_BASE = 8000

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java) ?: return

        // 1. Daily Goals & Progress Channel
        val goalsChannel = NotificationChannel(
            CHANNEL_GOALS_ID,
            "Daily Goals & Progress",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Twice-daily fitness goal check-ins and progress updates"
            enableVibration(true)
        }

        // 2. Daily Care Reminders Channel
        val careChannel = NotificationChannel(
            CHANNEL_CARE_ID,
            "Daily Care Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Time-of-day care reminders for morning, afternoon, evening, and night tasks"
            enableVibration(true)
        }

        // 3. Hydration Channel
        val hydrationChannel = NotificationChannel(
            CHANNEL_HYDRATION_ID,
            "Hydration Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Periodic water and hydration reminders"
            enableVibration(true)
        }

        manager.createNotificationChannels(listOf(goalsChannel, careChannel, hydrationChannel))
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun postNotification(
        context: Context,
        notificationId: Int,
        channelId: String,
        title: String,
        message: String,
        targetRoute: String? = null
    ) {
        if (!hasNotificationPermission(context)) return

        createNotificationChannels(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (targetRoute != null) {
                putExtra("TARGET_ROUTE", targetRoute)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_nht_mark)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.notify(notificationId, notification)
    }
}
