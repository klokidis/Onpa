package com.example.onpa.features.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.Settings
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onpa.R
import com.example.onpa.utils.SttLanguagesProvider
import com.example.onpa.features.userdata.DataStorePrefViewModel
import com.example.onpa.features.userdata.UserViewModel

@Composable
fun SettingsScreen(
    navigateMainScreen: () -> Unit,
    navigateUserDetails: () -> Unit,
    dataStorePrefViewModel: DataStorePrefViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val uiState by dataStorePrefViewModel.uiState.collectAsState()
    val userUiState by userViewModel.userUiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var selectedLanguageCode by rememberSaveable {
        mutableIntStateOf(userUiState.selectedUser?.voiceLanguage ?: 0)
    }

    var selectedLanguage by rememberSaveable {
        mutableStateOf(SttLanguagesProvider.displayLanguages[userUiState.selectedUser?.voiceLanguage ?: 0])
    }

    LaunchedEffect(userUiState.selectedUser?.voiceLanguage) {
        // Update selectedLanguageCode when the voiceLanguage changes
        selectedLanguageCode = userUiState.selectedUser?.voiceLanguage ?: 0
        // Update selectedLanguage based on the new selectedLanguageCode
        selectedLanguage = SttLanguagesProvider.displayLanguages[selectedLanguageCode]
    }

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
                onClick = dataStorePrefViewModel::setVibration,
                value = uiState.vibration
            )
            Spacer(modifier = Modifier.padding(5.dp))
            SettingSwitchWithExplain(
                text = stringResource(R.string.auto_mic),
                onClick = dataStorePrefViewModel::setAutoMic,
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
                onClick = {
                    Intent().apply {
                        try {
                            // Try opening the TTS_SETTINGS Settings first
                            action = "com.android.settings.TTS_SETTINGS"
                            context.startActivity(this)
                        } catch (_: ActivityNotFoundException) {
                            // If the above fails, fallback to Accessibility Settings
                            action = Settings.ACTION_ACCESSIBILITY_SETTINGS
                            context.startActivity(this)
                        }
                    }
                },
            )
            Spacer(modifier = Modifier.padding(5.dp))
            SettingDropDownMenu(
                title = stringResource(R.string.select_Stt_Language),
                selectedLanguage = selectedLanguage,
                onClick = { index, language ->
                    selectedLanguage = language
                    selectedLanguageCode = index
                    userViewModel.changeUserLanguage(userUiState.selectedUser?.userId ?: 0, index)
                },
            )
        }
    }
}

@Composable
fun OneSettingSimpleDialog(
    text: String,
    titleText: String,
    bodyText: String,
    onClick: () -> Unit
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
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 21.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
                TextButton(onClick = {
                    onClick()
                    showDialog = false
                }) {
                    Text(
                        stringResource(R.string.go_to_Settings),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            dismissButton = {
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
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 21.sp)
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
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 21.sp)
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
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 21.sp)
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
                        stringResource(R.string.ok ),
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
fun SettingDropDownMenu(
    title: String,
    selectedLanguage: String,
    onClick: (index: Int, language: String) -> Unit
) {
    var isDropDownMenuClicked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .size(60.dp)
                .clickable { isDropDownMenuClicked = true },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 21.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = selectedLanguage,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { isDropDownMenuClicked = true }
            )
        }

        DropdownMenu(
            expanded = isDropDownMenuClicked,
            onDismissRequest = { isDropDownMenuClicked = false },
        ) {
            SttLanguagesProvider.displayLanguages.forEachIndexed { index, language ->
                DropdownMenuItem(
                    onClick = {
                        onClick(index, language)
                        isDropDownMenuClicked = false
                    },
                    text = {
                        Text(
                            text = language,
                            color = if (selectedLanguage == language) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Gray
                            }
                        )
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen({ }, navigateUserDetails = { })
}