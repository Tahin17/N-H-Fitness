package com.aegisfit.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.aegisfit.app.domain.model.MealType

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Home", Icons.Filled.Dashboard)
    object Workout : Screen("workout", "Train", Icons.Filled.FitnessCenter)
    object Nutrition : Screen("nutrition", "Food", Icons.Filled.Restaurant)
    object Body : Screen("body", "Body", Icons.Filled.AccessibilityNew)
    object Skin : Screen("skin", "Care", Icons.Filled.Face)

    // Non-bottom-nav screens
    object Onboarding : Screen("onboarding", "Onboarding", Icons.Filled.Dashboard)
    object MeasurementInput : Screen("measurement_input", "Add Measurements", Icons.Filled.AccessibilityNew)
    object SkinTips : Screen("skin_tips", "Skin Tips", Icons.Filled.Face)
    object FoodSearch : Screen("food_search/{mealType}", "Food Search", Icons.Filled.Search) {
        fun createRoute(mealType: MealType) = "food_search/${mealType.name}"
    }
    object Cardio : Screen("cardio", "Cardio", Icons.Filled.FitnessCenter)
    object WorkoutDetail : Screen("workout_detail/{exerciseId}/{date}", "Workout Detail", Icons.Filled.FitnessCenter) {
        fun createRoute(exerciseId: Long, date: Long) = "workout_detail/$exerciseId/$date"
    }
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object Login : Screen("login", "Login", Icons.Filled.Person)

    companion object {
        val bottomNavItems = listOf(Dashboard, Workout, Nutrition, Body, Skin)
    }
}
