package com.aegisfit.app.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aegisfit.app.presentation.screen.body.BodyScreen
import com.aegisfit.app.presentation.screen.body.MeasurementInputScreen
import com.aegisfit.app.presentation.screen.dashboard.DashboardScreen
import com.aegisfit.app.presentation.screen.auth.LoginScreen
import com.aegisfit.app.presentation.screen.profile.ProfileScreen
import com.aegisfit.app.presentation.screen.nutrition.FoodSearchScreen
import com.aegisfit.app.presentation.screen.nutrition.NutritionScreen
import com.aegisfit.app.presentation.screen.onboarding.OnboardingScreen
import com.aegisfit.app.presentation.screen.skincare.SkinTipsScreen
import com.aegisfit.app.presentation.screen.skincare.SkincareScreen
import com.aegisfit.app.presentation.screen.workout.CardioScreen
import com.aegisfit.app.presentation.screen.workout.WorkoutDetailScreen
import com.aegisfit.app.presentation.screen.workout.WorkoutScreen

@Composable
fun AegisFitNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    isAuthenticated: Boolean
) {
    val startDestination = if (isAuthenticated) Screen.Dashboard.route else Screen.Login.route

    LaunchedEffect(isAuthenticated) {
        val currentRoute = navController.currentDestination?.route
        if (isAuthenticated && currentRoute == Screen.Login.route) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
                launchSingleTop = true
            }
        } else if (!isAuthenticated && currentRoute != null && currentRoute != Screen.Login.route) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0)
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(paddingValues),
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() }
    ) {
        // Bottom nav tabs
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(Screen.Workout.route) {
            WorkoutScreen(
                onNavigateToExercise = { exerciseId ->
                    navController.navigate(Screen.WorkoutDetail.createRoute(exerciseId, com.aegisfit.app.util.DateUtils.todayStartMillis()))
                },
                onNavigateToCardio = {
                    navController.navigate(Screen.Cardio.route)
                }
            )
        }
        composable(Screen.Nutrition.route) {
            NutritionScreen(
                onNavigateToSearch = { mealType ->
                    navController.navigate(Screen.FoodSearch.createRoute(mealType))
                }
            )
        }
        composable(Screen.Body.route) {
            BodyScreen(
                onNavigateToMeasurement = {
                    navController.navigate(Screen.MeasurementInput.route)
                }
            )
        }
        composable(Screen.Skin.route) {
            SkincareScreen(
                onNavigateToTips = {
                    navController.navigate(Screen.SkinTips.route)
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Auth
        composable(Screen.Login.route) {
            LoginScreen()
        }

        // Onboarding (full-screen slide-in)
        composable(
            Screen.Onboarding.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            OnboardingScreen(
                onComplete = {
                    navController.popBackStack(Screen.Dashboard.route, false)
                }
            )
        }

        // Measurement input (slide from right)
        composable(
            Screen.MeasurementInput.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            MeasurementInputScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Skin Tips
        composable(
            Screen.SkinTips.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            SkinTipsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Food Search
        composable(
            Screen.FoodSearch.route,
            arguments = listOf(
                navArgument("mealType") { type = NavType.StringType }
            ),
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            FoodSearchScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Workout Detail
        composable(
            Screen.WorkoutDetail.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            WorkoutDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Cardio
        composable(
            Screen.Cardio.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            CardioScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
