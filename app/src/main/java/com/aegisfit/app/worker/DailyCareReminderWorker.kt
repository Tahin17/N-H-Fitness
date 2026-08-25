package com.aegisfit.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.aegisfit.app.domain.model.CareTimeSlot
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.DailyCareRepository
import com.aegisfit.app.notification.NotificationHelper
import com.aegisfit.app.util.DateUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@HiltWorker
class DailyCareReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val authRepository: AuthRepository,
    private val dailyCareRepository: DailyCareRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val slotName = inputData.getString(KEY_TIME_SLOT) ?: return Result.success()
        val timeSlot = try {
            CareTimeSlot.valueOf(slotName)
        } catch (e: Exception) {
            return Result.success()
        }

        val userId = authRepository.currentUserId ?: ""
        val today = DateUtils.todayStartMillis()

        val items = if (userId.isNotBlank()) {
            dailyCareRepository.getCareItemsWithStatus(userId, today).firstOrNull() ?: emptyList()
        } else {
            emptyList()
        }

        val slotItems = items.filter { it.timeSlot == timeSlot }
        val pendingItems = slotItems.filter { !it.isCompleted }

        // If user configured items for this slot and already completed all of them, don't nag
        if (slotItems.isNotEmpty() && pendingItems.isEmpty()) {
            return Result.success()
        }

        val (title, defaultMessage, notifId) = when (timeSlot) {
            CareTimeSlot.MORNING -> Triple(
                "🌅 Morning Care Routine",
                "Start your day fresh! Complete your morning daily care tasks & routine.",
                NotificationHelper.NOTIFICATION_ID_CARE_BASE + 1
            )
            CareTimeSlot.AFTERNOON -> Triple(
                "☀️ Afternoon Daily Care",
                "Time for your afternoon daily care tasks & routine.",
                NotificationHelper.NOTIFICATION_ID_CARE_BASE + 2
            )
            CareTimeSlot.EVENING -> Triple(
                "🌇 Evening Care Routine",
                "Check off your evening care tasks, hygiene & routine.",
                NotificationHelper.NOTIFICATION_ID_CARE_BASE + 3
            )
            CareTimeSlot.NIGHT -> Triple(
                "🌙 Night Time Care",
                "Time for your night care tasks & routine before sleep.",
                NotificationHelper.NOTIFICATION_ID_CARE_BASE + 4
            )
        }

        val message = if (pendingItems.isNotEmpty()) {
            val itemNames = pendingItems.take(3).joinToString(", ") { it.title }
            val moreSuffix = if (pendingItems.size > 3) " +${pendingItems.size - 3} more" else ""
            "Pending tasks: $itemNames$moreSuffix"
        } else {
            defaultMessage
        }

        NotificationHelper.postNotification(
            context = appContext,
            notificationId = notifId,
            channelId = NotificationHelper.CHANNEL_CARE_ID,
            title = title,
            message = message,
            targetRoute = "skin"
        )

        return Result.success()
    }

    companion object {
        const val KEY_TIME_SLOT = "key_time_slot"

        fun scheduleAll(context: Context) {
            scheduleSlot(context, CareTimeSlot.MORNING, LocalTime.of(6, 0))
            scheduleSlot(context, CareTimeSlot.AFTERNOON, LocalTime.of(12, 0))
            scheduleSlot(context, CareTimeSlot.EVENING, LocalTime.of(18, 0))
            scheduleSlot(context, CareTimeSlot.NIGHT, LocalTime.of(21, 30))
        }

        private fun scheduleSlot(context: Context, slot: CareTimeSlot, targetTime: LocalTime) {
            val workName = "CareReminder_${slot.name}"
            val initialDelayMs = calculateInitialDelay(targetTime)

            val inputData = Data.Builder()
                .putString(KEY_TIME_SLOT, slot.name)
                .build()

            val request = PeriodicWorkRequestBuilder<DailyCareReminderWorker>(24, TimeUnit.HOURS)
                .setInputData(inputData)
                .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                workName,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }

        private fun calculateInitialDelay(targetTime: LocalTime, now: ZonedDateTime = ZonedDateTime.now()): Long {
            val targetDateTime = now.toLocalDate().atTime(targetTime).atZone(now.zone)
            val nextOccurrence = if (targetDateTime.isBefore(now)) {
                targetDateTime.plusDays(1)
            } else {
                targetDateTime
            }
            return Duration.between(now, nextOccurrence).toMillis().coerceAtLeast(0L)
        }
    }
}
