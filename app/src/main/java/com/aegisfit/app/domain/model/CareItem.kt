package com.aegisfit.app.domain.model

enum class CareTimeSlot(val displayName: String, val order: Int) {
    MORNING("Morning", 1),
    AFTERNOON("Afternoon", 2),
    EVENING("Evening", 3),
    NIGHT("Night", 4);

    companion object {
        fun fromString(value: String): CareTimeSlot {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: MORNING
        }
    }
}

enum class CareCategory(val displayName: String) {
    MEDICINE("Medicine"),
    SKINCARE("Skincare"),
    HYGIENE("Hygiene"),
    HABIT("Habit"),
    TASK("Task"),
    OTHER("Other");

    companion object {
        fun fromString(value: String): CareCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: OTHER
        }
    }
}

data class CareItem(
    val id: Long = 0,
    val userId: String = "",
    val title: String,
    val timeSlot: CareTimeSlot = CareTimeSlot.MORNING,
    val category: CareCategory = CareCategory.OTHER,
    val notes: String? = null,
    val timeHint: String? = null,
    val isCompleted: Boolean = false,
    val createdAtMs: Long = System.currentTimeMillis()
)

data class DailyCareSummary(
    val completedCount: Int = 0,
    val totalCount: Int = 0
) {
    val progress: Float
        get() = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val isAllCompleted: Boolean
        get() = totalCount > 0 && completedCount == totalCount
}
