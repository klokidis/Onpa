package com.example.ptyxiakh.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ptyxiakh.R
import com.example.ptyxiakh.viewmodels.DataStorePrefViewModel

@Composable
fun SettingsScreen(
    navigateMainScreen: () -> Unit,
    dataStorePrefViewModel: DataStorePrefViewModel = hiltViewModel(),
) {
    val uiState by dataStorePrefViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier,
        contentAlignment = Alignment.TopStart
    ) {
        IconButton(
            onClick = { navigateMainScreen() },
            modifier = Modifier
                .padding(start = 5.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.back),
                modifier = Modifier.size(30.dp)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 35.sp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            SettingSwitch(
                text = stringResource(R.string.vibrate),
                onClick = dataStorePrefViewModel::toggleVibration,
                value = uiState.vibration
            )
            Spacer(modifier = Modifier.padding(5.dp))
            SettingSwitchWithExplain(
                text = stringResource(R.string.auto_mic),
                onClick = dataStorePrefViewModel::toggleAutoMic,
                value = uiState.autoMic
            )
            Spacer(modifier = Modifier.padding(5.dp))
        }
    }
}

@Composable
fun OneSettingSimple(text: String, onClick: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(true) }
            .size(60.dp)
            .padding(start = 15.dp, end = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 25.sp)
        )
    }
}

@Composable
fun SettingSwitch(text: String, onClick: (Boolean) -> Unit, value: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(!value) }
            .size(60.dp)
            .padding(start = 15.dp, end = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 25.sp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = value,
            onCheckedChange = {
                onClick(!value)
            }
        )
    }
}

@Composable
fun SettingSwitchWithExplain(text: String, onClick: (Boolean) -> Unit, value: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(!value) }
            .size(60.dp)
            .padding(start = 15.dp, end = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 25.sp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = value,
            onCheckedChange = {
                onClick(!value)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen({ })
}