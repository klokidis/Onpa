package com.example.ptyxiakh.ui.setUp

import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ptyxiakh.data.viewmodels.UserViewModel

@Composable
fun SetUp(
    userViewModel: UserViewModel = hiltViewModel(),
) {

    val scrollState = rememberScrollState()


}