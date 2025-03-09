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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ptyxiakh.viewmodels.UserDataViewModel
import com.example.ptyxiakh.viewmodels.UserViewModel

enum class AppScreens {
    Loading,
    Welcome,
    SignUp,
    UserDetails,
    Main,
    Settings
}

@Composable
fun NavigationScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    userDataViewModel: UserDataViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val userUiState by userViewModel.userUiState.collectAsState()
    val userDataUiState by userDataViewModel.userDataUiState.collectAsState()

    LaunchedEffect(userUiState.selectedUser?.userId) { //load the userdata based on the selected id
        if (userUiState.selectedUser?.userId != null) {
            userDataViewModel.loadUserData(userUiState.selectedUser!!.userId)
        }else{
            userDataViewModel.loadAllUserData()
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
                Welcome(
                    navigateSetUp = { navController.navigate(AppScreens.SignUp.name) }
                )
            }
            composable(
                route = AppScreens.SignUp.name
            ) {
                SignUp(
                    navigate = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = AppScreens.UserDetails.name
            ) {
                UserDetailsScreen(
                    user = userUiState.selectedUser,
                    userData = userDataUiState.userData,
                    navigate = { navController.navigate(AppScreens.Main.name) },
                    addOneUserData = userDataViewModel::addFavorite
                )
            }
            composable(
                route = AppScreens.Main.name,
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) } // Slide out to the left
            ) {
                MainScreen(
                    selectedUser = userUiState.selectedUser,
                    navigateSettings = { navController.navigate(AppScreens.Settings.name) },
                    userData = userDataUiState.userData,
                )
            }
            composable(
                route = AppScreens.Settings.name,
                enterTransition = { slideInHorizontally(initialOffsetX = { it }) }, // Slide in from the right
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