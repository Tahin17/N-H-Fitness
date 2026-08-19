package com.aegisfit.app.util

import java.util.Calendar

object DateUtils {

    /** Returns the epoch millis for midnight (00:00:00.000) of the current day in the device's default timezone. */
    fun todayStartMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /** Normalizes any epoch millis timestamp to midnight of that day. */
    fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /** Returns the final millisecond of the local calendar day, including DST transitions. */
    fun endOfDay(millis: Long): Long = addDays(startOfDay(millis), 1) - 1

    /** Adds local calendar days without assuming every day is exactly 24 hours. */
    fun addDays(millis: Long, days: Int): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.add(Calendar.DAY_OF_YEAR, days)
        return cal.timeInMillis
    }

    /** Returns the midnight of Monday of the current week. */
    fun weekStartMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        // Shift so Monday = first day
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
        cal.add(Calendar.DAY_OF_YEAR, -daysFromMonday)
        return cal.timeInMillis
    }

    /** Returns the midnight of the 1st of the current month. */
    fun monthStartMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /** Returns the number of days since epoch for a given millis. Useful for Night A/B rotation. */
    fun daysSinceEpoch(millis: Long = System.currentTimeMillis()): Long {
        return startOfDay(millis) / (24 * 60 * 60 * 1000L)
    }

    /** Returns a list of midnight timestamps for each day in the range [start, end]. */
    fun daysInRange(start: Long, end: Long): List<Long> {
        val days = mutableListOf<Long>()
        val cal = Calendar.getInstance()
        cal.timeInMillis = startOfDay(start)
        val endNormalized = startOfDay(end)
        while (cal.timeInMillis <= endNormalized) {
            days.add(cal.timeInMillis)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return days
    }

    /** Returns the number of days in the current month. */
    fun daysInCurrentMonth(): Int {
        val cal = Calendar.getInstance()
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /** Returns the day-of-week (1=Mon, 7=Sun) for a given millis. */
    fun dayOfWeek(millis: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        return if (dow == Calendar.SUNDAY) 7 else dow - 1
    }

    /** Returns the day-of-month (1-31) for a given millis. */
    fun dayOfMonth(millis: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        return cal.get(Calendar.DAY_OF_MONTH)
    }
}
