package com.aegisfit.app.notification

import android.content.Context
import com.aegisfit.app.worker.DailyCareReminderWorker
import com.aegisfit.app.worker.DailyGoalReminderWorker
import com.aegisfit.app.worker.HydrationReminderWorker

object AppNotificationScheduler {

    fun scheduleAll(context: Context) {
        // 1. Create channels
        NotificationHelper.createNotificationChannels(context)

        // 2. Schedule Daily Goals (Midday 1pm & Evening 8:30pm)
        DailyGoalReminderWorker.scheduleAll(context)

        // 3. Schedule Daily Care (Morning 6am, Afternoon 12pm, Evening 6pm, Night 9:30pm)
        DailyCareReminderWorker.scheduleAll(context)

        // 4. Schedule Hydration Reminders (Hourly between 6am and midnight)
        HydrationReminderWorker.schedule(context)
    }
}
