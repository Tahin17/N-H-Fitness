package com.aegisfit.app.worker

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
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.aegisfit.app.MainActivity
import com.aegisfit.app.R
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class HydrationReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // 12:00 AM is the end boundary, so alerts are allowed from 06:00 through 23:59.
        if (!isReminderHour(LocalTime.now().hour)) return Result.success()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        createNotificationChannel()
        showHydrationNotification()
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Hydration reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Hourly water reminders from 6:00 AM until midnight"
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }

    private fun showHydrationNotification() {
        val openAppIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_nht_mark)
            .setContentTitle("Time to hydrate")
            .setContentText("Drink some water and keep your hydration on track.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        // Reuse one ID so reminders do not create a large stack of old notifications.
        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val START_HOUR = 6
        private const val END_HOUR_EXCLUSIVE = 24
        private const val CHANNEL_ID = "nht_hydration_reminders"
        private const val NOTIFICATION_ID = 6001
        private const val UNIQUE_WORK_NAME = "NHTFitnessHydrationReminder"

        internal fun isReminderHour(hour: Int): Boolean =
            hour in START_HOUR until END_HOUR_EXCLUSIVE

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
                1,
                TimeUnit.HOURS
            )
                .setInitialDelay(delayUntilNextReminder(), TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }

        private fun delayUntilNextReminder(now: ZonedDateTime = ZonedDateTime.now()): Long {
            val roundedHour = now.truncatedTo(ChronoUnit.HOURS)
            val nextHour = if (roundedHour.isBefore(now)) roundedHour.plusHours(1) else roundedHour
            val nextAllowed = if (isReminderHour(nextHour.hour)) {
                nextHour
            } else {
                nextHour.toLocalDate().atTime(START_HOUR, 0).atZone(now.zone).let { sixAm ->
                    if (sixAm.isBefore(nextHour)) sixAm.plusDays(1) else sixAm
                }
            }
            return Duration.between(now, nextAllowed).toMillis().coerceAtLeast(0L)
        }
    }
}
