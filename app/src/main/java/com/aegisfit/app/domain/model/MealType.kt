package com.aegisfit.app.domain.model

enum class MealType(val displayName: String) {
    Breakfast("Breakfast"),
    Lunch("Lunch"),
    Dinner("Dinner"),
    Snack("Snack");

    companion object {
        fun fromString(value: String): MealType = entries.find { it.name.equals(value, ignoreCase = true) } ?: Breakfast
    }
}
