package com.yukuro.onpa.features.main

import android.Manifest
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yukuro.domain.models.userdata.UserData
import com.yukuro.domain.models.users.User
import com.yukuro.onpa.R
import com.yukuro.onpa.features.sounddetection.SoundDetectionServiceViewModel
import com.yukuro.onpa.features.stt.VoiceToTextViewModel
import com.yukuro.onpa.features.tts.rememberTextToSpeech
import com.yukuro.onpa.features.userdata.DataStorePrefViewModel
import com.yukuro.onpa.features.userdata.UserDataViewModel
import com.yukuro.onpa.utils.HapticUtils
import com.yukuro.onpa.utils.PermissionUtils
import com.yukuro.onpa.utils.showToast
import kotlinx.coroutines.launch


@Composable
fun MainScreen(
    navigateSettings: () -> Unit,
    navigateSoundDetect: () -> Unit,
    voiceToTextViewModel: VoiceToTextViewModel = hiltViewModel(),
    dataStorePrefViewModel: DataStorePrefViewModel = hiltViewModel(),
    soundDetectionServiceViewModel: SoundDetectionServiceViewModel = hiltViewModel(),
    userDataViewModel: UserDataViewModel,
    userData: List<UserData>,
    selectedUser: User?,
) {
    val context = LocalContext.current

    var showAddDataDialog by rememberSaveable { mutableStateOf(false) }
    val isServiceRunning by soundDetectionServiceViewModel.isServiceRunning.collectAsState()
    val sttState by voiceToTextViewModel.sttState.collectAsState()
    val dataPrefUiState by dataStorePrefViewModel.uiState.collectAsState()

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    LaunchedEffect(recordAudioPermissionLauncher) { //ask the permission on launch
        recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    //navigating to settings while tts is speaking blocks the stt
    //this launch saves it
    LaunchedEffect(Unit) {
        voiceToTextViewModel.changeCanRunAgain(true)
    }

    //pass the user language to the voiceToTextViewModel
    LaunchedEffect(selectedUser?.voiceLanguage) {
        if (selectedUser?.voiceLanguage != null) {
            Log.d("changed", selectedUser.voiceLanguage.toString())
            voiceToTextViewModel.changeLanguage(selectedUser.voiceLanguage)
        }
    }

    // Handle speech-to-text errors
    LaunchedEffect(sttState.offlineError) {
        if (sttState.offlineError) {
            showToast(context, "Speech recognition is offline. Please enable Wi-Fi.")
        }
    }

    LaunchedEffect(sttState.availableSTT) {
        if (!sttState.availableSTT) {
            showToast(
                context,
                "Speech recognition is not available. Please install or enable Google Speech Services."
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopButtons(
            navigateSettings,
            navigateSoundDetect,
            voiceToTextViewModel::stopListening,
            isListening = sttState.isSpeaking,
        )
        SpeechToTextUi(
            sttState.fullTranscripts,
            sttState.partialTranscripts,
            clearText = voiceToTextViewModel::clearTexts,
        )
        DataLazyList(
            modifier = Modifier.fillMaxSize(1f),
            userData = userData,
            isListening = sttState.isSpeaking,
            stopListening = voiceToTextViewModel::stopListening,
            startListening = voiceToTextViewModel::startListening,
            changeCanRunAgain = voiceToTextViewModel::changeCanRunAgain,
            vibrate = dataPrefUiState.vibration,
            autoMic = dataPrefUiState.autoMic,
            isLoading = dataPrefUiState.isLoading,
            onDelete = userDataViewModel::deleteOneData
        )

    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        TextFieldUpperButtons(
            onAddDataClicked = { showAddDataDialog = true },
            startListening = voiceToTextViewModel::startListening,
            stopListening = voiceToTextViewModel::stopListening,
            changeCanRunAgain = voiceToTextViewModel::changeCanRunAgain,
            isEnabled = (sttState.canRunAgain && !isServiceRunning && sttState.isSttInitialized),
            isSpeaking = sttState.isSpeaking,
            vibrate = dataPrefUiState.vibration,
            autoMic = dataPrefUiState.autoMic,
            isLoading = dataPrefUiState.isLoading,
            recordAudioPermissionLauncher = recordAudioPermissionLauncher
        )
    }
    if (showAddDataDialog) {
        AddPhraseDialog(
            onDismiss = { showAddDataDialog = false },
            onSave = { phrase ->
                selectedUser?.let { user ->
                    userDataViewModel.addOneUserData(user.userId, phrase)
                }
            }
        )
    }
}

@Composable
fun DataLazyList(
    startListening: () -> Unit,
    stopListening: () -> Unit,
    isListening: Boolean,
    changeCanRunAgain: (Boolean) -> Unit,
    vibrate: Boolean,
    autoMic: Boolean,
    isLoading: Boolean,
    userData: List<UserData>,
    modifier: Modifier,
    onDelete: (Int) -> Unit,
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val (tts, ttsReady) = if (!isLoading) {
        rememberTextToSpeech(
            onFinished = {
                coroutineScope.launch {
                    HapticUtils.triggerVibration(
                        canVibrate = vibrate,
                        context = context,
                        milliseconds = 10
                    )
                    changeCanRunAgain(true)
                    if (autoMic && PermissionUtils.checkRecordPermission(context)) {
                        startListening()
                    }
                }
            }
        )
    }else{
        null to false
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userData.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.add_data),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth()
                )
            }
        } else {
            items(
                items = userData,
                key = { it.id }
            ) { data ->
                DataCard(
                    data = data.value,
                    id = data.id,
                    tts = tts,
                    stopListening = stopListening,
                    isListening = isListening,
                    vibrate = vibrate,
                    onDelete = onDelete,
                    canRunAgainFalse = { changeCanRunAgain(false) },
                    ttsReady = ttsReady
                )
            }
        }

        // Always add the Spacer unconditionally
        item {
            Spacer(modifier = Modifier.padding(100.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DataCard(
    data: String,
    tts: MutableState<TextToSpeech?>?,
    stopListening: () -> Unit,
    isListening: Boolean,
    vibrate: Boolean,
    canRunAgainFalse: () -> Unit,
    onDelete: (Int) -> Unit,
    id: Int,
    ttsReady: Boolean
) {
    val context = LocalContext.current
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Phrase") },
            text = { Text("Are you sure you want to delete this phrase?") },
            confirmButton = {
                OutlinedButton(onClick = {
                    onDelete(id)
                    showDeleteDialog = false
                }) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(start = 18.dp, end = 18.dp, top = 10.dp)
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                color = if (ttsReady) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f) // lighter/disabled look
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            // reduce opacity if not ready
            .alpha(if (ttsReady) 1f else 0.5f)
            .combinedClickable(
                enabled = ttsReady,
                onClick = {
                    HapticUtils.triggerVibration(
                        canVibrate = vibrate,
                        context = context,
                        milliseconds = 10
                    )
                    if (isListening) stopListening()
                    canRunAgainFalse()
                    tts?.value?.speak(data, TextToSpeech.QUEUE_FLUSH, null, "")
                },
                onLongClick = {
                    HapticUtils.triggerVibration(
                        canVibrate = vibrate,
                        context = context,
                        milliseconds = 30
                    )
                    showDeleteDialog = true
                }
            ),
    ) {
        DataText(data, inputTextAlign = TextAlign.Start)
    }

}

@Composable
fun DataText(
    dataText: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    inputTextAlign: TextAlign = TextAlign.Center,
    inputStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Text(
        text = dataText.trim(),
        textAlign = inputTextAlign,
        color = textColor,
        style = inputStyle,
        modifier = modifier
            .padding(15.dp)
            .fillMaxWidth()
    )
}

@Composable
fun SpeechToTextUi(
    listOfSpokenText: List<String>,
    listOfSpokenEarlyText: List<String>,
    clearText: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val combinedText = listOfSpokenText + listOfSpokenEarlyText
    val fullText = combinedText.joinToString(" ")
        .removePrefix(" ")
        .replace(Regex(" +"), " ") // Replace multiple spaces with a single space

    LaunchedEffect(fullText.length) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(start = 10.dp, end = 10.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onBackground),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState) // apply scroll to container, not Text
                    .padding(10.dp)
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    text = fullText,
                    style = TextStyle(
                        textAlign = TextAlign.Start,
                        fontSize = 26.sp,
                    )
                )
            }
            IconButton(
                onClick = { clearText() },
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .padding(5.dp)
            ) {
                Icon(
                    modifier = Modifier.size(27.dp),
                    painter = painterResource(R.drawable.mop_24px),
                    contentDescription = stringResource(R.string.clear_text),
                )
            }
        }
    }
}

@Composable
fun TopButtons(
    navigateSettings: () -> Unit,
    navigateSoundDetect: () -> Unit,
    stopListening: () -> Unit,
    isListening: Boolean
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
    ) {
        IconButton(
            onClick = {
                if (isListening) stopListening()
                navigateSoundDetect()
            },
            modifier = Modifier
                .padding(start = 5.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(30.dp),
                painter = painterResource(R.drawable.noise_aware_24px),
                contentDescription = stringResource(R.string.audio_event_classifier),
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = {
                if (isListening) stopListening()
                navigateSettings()
            },
            modifier = Modifier
                .padding(end = 5.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(30.dp),
                painter = painterResource(R.drawable.settings_24px),
                contentDescription = stringResource(R.string.settings),
            )
        }
    }
}

@Composable
fun TextFieldUpperButtons(
    startListening: () -> Unit,
    stopListening: () -> Unit,
    isSpeaking: Boolean,
    isEnabled: Boolean,
    changeCanRunAgain: (Boolean) -> Unit,
    vibrate: Boolean,
    autoMic: Boolean,
    isLoading: Boolean,
    recordAudioPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    onAddDataClicked: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.padding(end = 16.dp, start = 5.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = { onAddDataClicked() },
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    painter = painterResource(R.drawable.add_24px),
                    contentDescription = stringResource(R.string.add_phr),
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            OutlinedCustomIconButton(
                startListening = startListening,
                stopListening = stopListening,
                isSpeaking = isSpeaking,
                isEnabled = isEnabled,
                recordAudioPermissionLauncher = recordAudioPermissionLauncher
            )
        }
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 10.dp, top = 5.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // Ensures both children match their heights
        ) {
            TextFieldWithInsideIcon(
                stopListening = stopListening,
                startListening = startListening,
                isListening = isSpeaking,
                changeCanRunAgain = changeCanRunAgain,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                autoMic = autoMic,
                vibrate = vibrate,
                isLoading = isLoading
            )
        }
    }
}

@Composable
fun AddPhraseDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var phrase by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(R.string.phrase_text_holder)) },
        text = {
            OutlinedTextField(
                value = phrase,
                onValueChange = { phrase = it },
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50.dp),
                maxLines = 3,
                )
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    if (phrase.trim().isNotEmpty()) {
                        onSave(phrase.trim())
                        onDismiss()
                    }
                }
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            OutlinedButton(onClick = { onDismiss() }) { Text(stringResource(R.string.cancel)) }
        }
    )
}


@Composable
fun OutlinedCustomIconButton(
    startListening: () -> Unit,
    stopListening: () -> Unit,
    isSpeaking: Boolean,
    isEnabled: Boolean,
    recordAudioPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
) {
    val context = LocalContext.current

    OutlinedButton(
        onClick = {
            when {
                isSpeaking -> {
                    stopListening()
                }

                else -> {
                    if (PermissionUtils.checkRecordPermission(context)) { //checks on runtime
                        startListening()
                    } else {
                        showToast(context, "Permission denied! Cannot record audio.")
                        // Request the permission
                        recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            }
        },
        enabled = isEnabled,
        border = BorderStroke(
            width = 1.5.dp,
            color = if (isEnabled) MaterialTheme.colorScheme.onBackground else Color.Gray
        ),
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape), // Make it circular
        shape = CircleShape, // Ensure the button's shape is circular
        contentPadding = PaddingValues(0.dp) //remove extra padding
    ) {
        Icon(
            modifier = Modifier
                .size(30.dp),
            imageVector = if (isSpeaking) Icons.Rounded.Mic else Icons.Rounded.MicOff,
            contentDescription = "",
        )
    }
}

@Composable
fun TextFieldWithInsideIcon(
    modifier: Modifier = Modifier,
    stopListening: () -> Unit,
    startListening: () -> Unit,
    isListening: Boolean,
    changeCanRunAgain: (Boolean) -> Unit,
    vibrate: Boolean,
    autoMic: Boolean,
    isLoading: Boolean
) {
    val context = LocalContext.current

    // State to track the focus of the TextField
    var isFocused by rememberSaveable { mutableStateOf(false) }
    var prompt by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val (tts, ttsReady) = if (!isLoading) {
        rememberTextToSpeech(
            onFinished = {
                coroutineScope.launch {
                    HapticUtils.triggerVibration(
                        canVibrate = vibrate,
                        context = context,
                        milliseconds = 10
                    )
                    changeCanRunAgain(true)
                    if (autoMic && PermissionUtils.checkRecordPermission(context)) {
                        startListening()
                    }
                }
            }
        )
    }else{
        null to false
    }

    OutlinedTextField(
        value = prompt,
        label = { Text("", style = MaterialTheme.typography.bodyMedium) },
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
        onValueChange = { prompt = it },
        modifier = modifier
            .onFocusChanged { focusState -> isFocused = focusState.isFocused },
        shape = RoundedCornerShape(50.dp),
        maxLines = 3,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
        ),
        trailingIcon = {
            Row {
                Box(
                    modifier = Modifier
                        .width(if (isFocused) 2.dp else 1.dp) // Line thickness
                        .fillMaxHeight() // Line height
                        .background(MaterialTheme.colorScheme.onBackground) // Line color
                )
                OutlinedButton(
                    onClick = {
                        if(ttsReady) {
                            if (isListening) {
                                stopListening()
                            }
                            HapticUtils.triggerVibration(
                                canVibrate = vibrate,
                                context = context,
                                milliseconds = 10
                            )
                            changeCanRunAgain(false)
                            tts?.value?.speak(
                                prompt.trim(), TextToSpeech.QUEUE_FLUSH, null, ""
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        topEnd = 50.dp,
                        bottomEnd = 50.dp
                    ),
                    enabled = prompt.trim().isNotEmpty() && ttsReady,
                    border = BorderStroke(
                        0.dp,
                        Color.Transparent
                    ), // Makes the outline transparent
                    contentPadding = PaddingValues(0.dp) //remove extra padding
                ) {
                    Icon(
                        modifier = Modifier
                            .size(30.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.graphic_eq_24px),
                        contentDescription = "",
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTopButtons() {
    Column {
        TopButtons(
            navigateSettings = {},
            navigateSoundDetect = {},
            stopListening = {},
            isListening = false
        )
    }
}
