package com.aegisfit.app.domain.model

enum class Gender(val displayName: String) {
    Male("Male"),
    Female("Female"),
    Other("Other");

    companion object {
        fun fromString(value: String): Gender = entries.find { it.name.equals(value, ignoreCase = true) } ?: Male
    }
}
