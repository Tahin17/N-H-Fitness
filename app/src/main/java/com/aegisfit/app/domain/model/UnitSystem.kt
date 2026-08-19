package com.aegisfit.app.domain.model

enum class UnitSystem {
    Metric, Imperial;
    companion object {
        fun fromString(value: String): UnitSystem = entries.find { it.name.equals(value, ignoreCase = true) } ?: Metric
    }
}
