package com.example.ptyxiakh.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ptyxiakh.R
import com.example.ptyxiakh.viewmodels.UserDataViewModel
import com.example.ptyxiakh.viewmodels.UserViewModel

@Composable
fun UserDetailsScreen(
    navigate: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
    userDataViewModel: UserDataViewModel = hiltViewModel(),
) {
    val userUiState by userViewModel.userUiState.collectAsState()
    val userDataUiState by userDataViewModel.userDataUiState.collectAsState()

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(20.dp))
        Icon(
            modifier = Modifier
                .size(170.dp),
            imageVector = Icons.Default.DataUsage,
            contentDescription = stringResource(R.string.data_icon),
        )

        Text(
            text = stringResource(R.string.user_details),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 30.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 10.dp,
                ),
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.user_details_meaning),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 40.dp
                ),
            textAlign = TextAlign.Center
        )

    }
}