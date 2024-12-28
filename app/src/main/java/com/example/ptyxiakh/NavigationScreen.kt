package com.example.ptyxiakh

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
            composable(route = AppScreens.Main.name) {
                MainScreen()
            }
            composable(route = AppScreens.Settings.name) {
                SettingsScreen()
            }
        }
    }
}