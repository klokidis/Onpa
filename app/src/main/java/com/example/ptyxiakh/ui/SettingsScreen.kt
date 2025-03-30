package com.example.ptyxiakh.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    navigateUserDetails: () -> Unit,
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
            Spacer(modifier = Modifier.padding(5.dp))
            SettingSwitch(
                text = stringResource(R.string.vibrate),
                onClick = dataStorePrefViewModel::toggleVibration,
                value = uiState.vibration
            )
            Spacer(modifier = Modifier.padding(5.dp))
            SettingSwitchWithExplain(
                text = stringResource(R.string.auto_mic),
                onClick = dataStorePrefViewModel::toggleAutoMic,
                value = uiState.autoMic,
                titleText = stringResource(R.string.auto_mic),
                bodyText = stringResource(R.string.auto_mic_meaning)
            )
            Spacer(modifier = Modifier.padding(5.dp))
            OneSettingSimple(stringResource(R.string.edit_user_data), navigateUserDetails)
            Spacer(modifier = Modifier.padding(5.dp))
            OneSettingSimpleDialog(
                text = stringResource(R.string.tts_language_title),
                titleText = stringResource(R.string.tts_language_title),
                bodyText = stringResource(R.string.tts_language_description),
            )
        }
    }
}

@Composable
fun OneSettingSimpleDialog(
    text: String,
    titleText: String,
    bodyText: String
) {
    var showDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
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
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(titleText) },
            text = {
                Box(
                    modifier = Modifier
                        .heightIn(max = 300.dp) // Limits height to enable scrolling
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(bodyText)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        stringResource(R.string.ok),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
        )
    }
}

@Composable
fun OneSettingSimple(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
fun SettingSwitchWithExplain(
    text: String,
    onClick: (Boolean) -> Unit,
    value: Boolean,
    titleText: String,
    bodyText: String
) {
    var showDialog by remember { mutableStateOf(false) }

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
        Spacer(modifier = Modifier.padding(5.dp))
        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier.size(20.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Help,
                contentDescription = stringResource(R.string.explain),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.padding(5.dp))
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = value,
            onCheckedChange = {
                onClick(!value)
            }
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(titleText) },
            text = {
                Text(bodyText)
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        stringResource(R.string.ok),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        )
    }
}

@Composable
fun SettingDropDownMenu(text: String, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .size(60.dp)
            .padding(start = 15.dp, end = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        var expanded by remember { mutableStateOf(false) }
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 25.sp)
        )
        Spacer(modifier = Modifier.weight(1f))
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Option 2") },
                onClick = { /* Do something... */ }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen({ }, navigateUserDetails = { })
}