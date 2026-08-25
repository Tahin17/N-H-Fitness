package com.aegisfit.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.aegisfit.app.domain.repository.AuthRepository
import com.aegisfit.app.domain.repository.HydrationRepository
import com.aegisfit.app.domain.repository.NutritionRepository
import com.aegisfit.app.domain.repository.UserRepository
import com.aegisfit.app.domain.repository.WorkoutRepository
import com.aegisfit.app.domain.usecase.biometrics.CalculateCalorieTargetUseCase
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
class DailyGoalReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val nutritionRepository: NutritionRepository,
    private val hydrationRepository: HydrationRepository,
    private val workoutRepository: WorkoutRepository,
    private val calculateCalorieTargetUseCase: CalculateCalorieTargetUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val checkinType = inputData.getString(KEY_CHECKIN_TYPE) ?: TYPE_MIDDAY
        val userId = authRepository.currentUserId ?: ""
        val today = DateUtils.todayStartMillis()

        var completedGoals = 0
        val totalGoals = 4

        if (userId.isNotBlank()) {
            val profile = userRepository.getUserProfileOnce(userId)
            val calories = nutritionRepository.getTotalCaloriesForDate(userId, today).firstOrNull() ?: 0.0
            val targetCalories = profile?.let {
                calculateCalorieTargetUseCase(
                    currentWeightKg = it.weightKg,
                    goalWeightKg = it.goalWeightKg,
                    heightCm = it.heightCm,
                    age = it.age,
                    gender = it.gender,
                    activityLevel = it.activityLevel
                )
            } ?: 2000

            val water = hydrationRepository.getTotalForDate(userId, today).firstOrNull() ?: 0L
            val sets = workoutRepository.getCompletedSetsCountForDate(userId, today).firstOrNull() ?: 0
            val cardioCals = workoutRepository.getCardioCaloriesForDate(userId, today).firstOrNull() ?: 0.0
            val weightLog = userRepository.getWeightLogForDate(userId, today).firstOrNull()

            if (targetCalories > 0 && calories >= (targetCalories * 0.85)) completedGoals++
            if (water >= 3000L) completedGoals++
            if (sets > 0 || cardioCals > 0.0) completedGoals++
            if (weightLog != null) completedGoals++
        }

        val (title, message, notifId) = if (checkinType == TYPE_MIDDAY) {
            val msg = when {
                completedGoals == totalGoals -> "4 of 4 goals already completed today! Outstanding work 🎉"
                completedGoals > 0 -> "You've completed $completedGoals of $totalGoals daily goals so far. Keep up the momentum!"
                else -> "Halfway through the day! Log your lunch, drink water, and check off your workout."
            }
            Triple("Midday Fitness Check-in 🎯", msg, NotificationHelper.NOTIFICATION_ID_GOAL_BASE + 1)
        } else {
            val msg = when {
                completedGoals == totalGoals -> "🎉 All $totalGoals daily fitness goals achieved for today! Great job."
                completedGoals > 0 -> "You've met $completedGoals of $totalGoals goals today. Remember to log your remaining meals, water, or workout before bed!"
                else -> "Don't forget to log your food, water, or workout today to keep your streak alive."
            }
            Triple("Evening Goals Update 🌙", msg, NotificationHelper.NOTIFICATION_ID_GOAL_BASE + 2)
        }

        NotificationHelper.postNotification(
            context = appContext,
            notificationId = notifId,
            channelId = NotificationHelper.CHANNEL_GOALS_ID,
            title = title,
            message = message,
            targetRoute = "dashboard"
        )

        return Result.success()
    }

    companion object {
        const val KEY_CHECKIN_TYPE = "key_checkin_type"
        const val TYPE_MIDDAY = "MIDDAY"
        const val TYPE_EVENING = "EVENING"

        fun scheduleAll(context: Context) {
            scheduleCheckin(context, TYPE_MIDDAY, LocalTime.of(13, 0)) // 1:00 PM Midday
            scheduleCheckin(context, TYPE_EVENING, LocalTime.of(20, 30)) // 8:30 PM Evening
        }

        private fun scheduleCheckin(context: Context, checkinType: String, targetTime: LocalTime) {
            val workName = "DailyGoalReminder_$checkinType"
            val initialDelayMs = calculateInitialDelay(targetTime)

            val inputData = Data.Builder()
                .putString(KEY_CHECKIN_TYPE, checkinType)
                .build()

            val request = PeriodicWorkRequestBuilder<DailyGoalReminderWorker>(24, TimeUnit.HOURS)
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
