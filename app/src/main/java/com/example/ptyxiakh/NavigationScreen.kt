package com.example.ptyxiakh

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ptyxiakh.ui.MainScreen
import com.example.ptyxiakh.ui.SettingsScreen

enum class AppScreens {
    Main,
    Settings
}

@Composable
fun NavigationScreen(navController: NavHostController = rememberNavController()) {
    Scaffold(
        modifier = Modifier.safeDrawingPadding()
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppScreens.Main.name,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = tween(0)) },
            exitTransition = { fadeOut(animationSpec = tween(0)) },
        ) {
            composable(
                route = AppScreens.Main.name,
                enterTransition = { slideInHorizontally(initialOffsetX = { -it }) }, // Slide in from the left
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) } // Slide out to the left
            ) {
                MainScreen(
                    navigateSettings = { navController.navigate(AppScreens.Settings.name) }
                )
            }
            composable(
                route = AppScreens.Settings.name,
                enterTransition = { slideInHorizontally(initialOffsetX = { it }) }, // Slide in from the right
                exitTransition = { slideOutHorizontally(targetOffsetX = { it }) } // Slide out to the right

            ) {
                SettingsScreen(
                    navigateMainScreen = { navController.navigate(AppScreens.Main.name) }
                )
            }
        }
    }
}