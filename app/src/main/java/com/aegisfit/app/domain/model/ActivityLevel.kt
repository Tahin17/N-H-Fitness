package com.aegisfit.app.domain.model

enum class ActivityLevel(val displayName: String, val multiplier: Double) {
    Sedentary("Sedentary", 1.2),
    Light("Lightly Active", 1.375),
    Moderate("Moderately Active", 1.55),
    Active("Active", 1.725),
    VeryActive("Very Active", 1.9);

    companion object {
        fun fromString(value: String): ActivityLevel = entries.find { it.name.equals(value, ignoreCase = true) } ?: Moderate
    }
}
