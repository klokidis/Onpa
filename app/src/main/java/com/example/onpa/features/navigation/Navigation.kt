package com.example.onpa.features.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onpa.features.licenses.LicensesScreen
import com.example.onpa.features.loading.LoadingScreen
import com.example.onpa.features.main.MainScreen
import com.example.onpa.features.settings.SettingsScreen
import com.example.onpa.features.signup.SignUpScreen
import com.example.onpa.features.sounddetection.SoundDetectionScreen
import com.example.onpa.features.userdata.UserDetailsScreen
import com.example.onpa.features.signup.WelcomeScreen
import com.example.onpa.features.userdata.UserDataViewModel
import com.example.onpa.features.userdata.UserViewModel

enum class AppScreens {
    Loading,
    Welcome,
    SignUp,
    UserDetails,
    Main,
    SoundDetect,
    Settings,
    Licenses
}

@Composable
fun Navigation(
    userViewModel: UserViewModel = hiltViewModel(),
    userDataViewModel: UserDataViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val userUiState by userViewModel.userUiState.collectAsState()
    val userDataUiState by userDataViewModel.userDataUiState.collectAsState()

    LaunchedEffect(userUiState.selectedUser?.userId) { //load the userdata based on the selected id
        if (userUiState.selectedUser?.userId != null) {
            userDataViewModel.getOneUserData(userUiState.selectedUser!!.userId)
        } else {
            userDataViewModel.getAllUserData()
        }
    }

    Scaffold(
        modifier = Modifier.safeDrawingPadding()
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = when {
                userUiState.isLoading || userDataUiState.isLoading -> AppScreens.Loading.name
                userUiState.users.isEmpty() -> AppScreens.Welcome.name
                userDataUiState.userData.isEmpty() -> AppScreens.UserDetails.name
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
                WelcomeScreen(
                    navigateSetUp = { navController.navigate(AppScreens.SignUp.name) }
                )
            }
            composable(
                route = AppScreens.SignUp.name
            ) {
                SignUpScreen()
            }
            composable(
                route = AppScreens.UserDetails.name
            ) {
                UserDetailsScreen(
                    user = userUiState.selectedUser,
                    userData = userDataUiState.userData,
                    navigate = {
                        navController.navigate(AppScreens.Main.name) {
                            popUpTo(AppScreens.Main.name) {
                                inclusive = true
                            } // Clear back stack
                        }
                    },
                    addOneUserData = userDataViewModel::addOneUserData,
                    deleteOneData = userDataViewModel::deleteOneData
                )
            }
            composable(
                route = AppScreens.Main.name,
                enterTransition = {
                    when (initialState.destination.route) {
                        AppScreens.SoundDetect.name -> slideInHorizontally(initialOffsetX = { it }) // Slide right
                        AppScreens.Settings.name -> slideInHorizontally(initialOffsetX = { -it }) // Slide left
                        else -> {
                            fadeIn(animationSpec = tween(0))
                        }
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        AppScreens.SoundDetect.name -> slideOutHorizontally(targetOffsetX = { it }) // Slide right
                        AppScreens.Settings.name -> slideOutHorizontally(targetOffsetX = { -it }) // Slide left
                        else -> null
                    }
                }
            ) {
                MainScreen(
                    selectedUser = userUiState.selectedUser,
                    navigateSettings = { navController.navigate(AppScreens.Settings.name) },
                    navigateSoundDetect = { navController.navigate(AppScreens.SoundDetect.name) },
                    userData = userDataUiState.userData,
                )
            }
            composable(
                route = AppScreens.SoundDetect.name,
                enterTransition = { slideInHorizontally(initialOffsetX = { -it }) }, // Slide in from the left
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            ) {
                SoundDetectionScreen(
                    navigate = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = AppScreens.Settings.name,
                enterTransition = { slideInHorizontally(initialOffsetX = { it }) }, // Slide in from the right
                exitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
            ) {
                SettingsScreen(
                    navigateMainScreen = {
                        navController.navigate(AppScreens.Main.name) {
                            popUpTo(AppScreens.Main.name) {
                                inclusive = true
                            } // Clear back stack
                        }
                    },
                    navigateUserDetails = {
                        navController.navigate(AppScreens.UserDetails.name)
                    },
                    navigateLicensesScreen = {
                        navController.navigate(AppScreens.Licenses.name)
                    }
                )
            }
            composable(
                route = AppScreens.Licenses.name,
            ) {
                LicensesScreen(
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}