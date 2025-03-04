package com.example.ptyxiakh.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ptyxiakh.data.viewmodels.UserViewModel

enum class AppScreens {
    Loading,
    Welcome,
    SetUp,
    Main,
    Settings
}

@Composable
fun NavigationScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val userUiState by userViewModel.userUiState.collectAsState()

    Scaffold(
        modifier = Modifier.safeDrawingPadding()
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = when {
                userUiState.isLoading -> AppScreens.Loading.name
                userUiState.users.isEmpty() -> AppScreens.Welcome.name
                else -> AppScreens.Main.name
            },
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = tween(0)) },
            exitTransition = { fadeOut(animationSpec = tween(0)) },
        ) {
            composable(
                route = AppScreens.Loading.name
            ) {
                LoadingScreen()
            }
            composable(
                route = AppScreens.Welcome.name
            ) {
                Welcome(
                    navigateSetUp = { navController.navigate(AppScreens.SetUp.name) }
                )
            }
            composable(
                route = AppScreens.SetUp.name
            ) {
                SignUp()
            }
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
                    navigateMainScreen = {
                        navController.navigate(AppScreens.Main.name) {
                            popUpTo(AppScreens.Main.name) {
                                inclusive = true
                            } // Clear back stack
                        }
                    }
                )
            }
        }
    }
}