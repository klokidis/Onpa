package com.example.ptyxiakh.ui

import android.Manifest
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ptyxiakh.viewmodels.GeminiViewModel
import com.example.ptyxiakh.R
import com.example.ptyxiakh.model.ResponseState
import com.example.ptyxiakh.model.User
import com.example.ptyxiakh.model.UserData
import com.example.ptyxiakh.viewmodels.VoiceToTextViewModel
import com.example.ptyxiakh.ui.tts.rememberTextToSpeech
import com.example.ptyxiakh.utils.HapticUtils
import com.example.ptyxiakh.utils.PermissionUtils
import com.example.ptyxiakh.utils.showToast
import com.example.ptyxiakh.viewmodels.DataStorePrefViewModel
import com.example.ptyxiakh.viewmodels.SoundDetectionServiceViewModel
import kotlinx.coroutines.launch


@Composable
fun MainScreen(
    navigateSettings: () -> Unit,
    navigateSoundDetect: () -> Unit,
    geminiViewModel: GeminiViewModel = hiltViewModel(),
    voiceToTextViewModel: VoiceToTextViewModel = hiltViewModel(),
    dataStorePrefViewModel: DataStorePrefViewModel = hiltViewModel(),
    soundDetectionServiceViewModel: SoundDetectionServiceViewModel = hiltViewModel(),
    userData: List<UserData>,
    selectedUser: User?,
) {
    val context = LocalContext.current

    val isServiceRunning by soundDetectionServiceViewModel.isServiceRunning.collectAsState()
    val responseUiState by geminiViewModel.responseState.collectAsState()
    val resultUiState by geminiViewModel.resultUiState.collectAsState()
    val sttState by voiceToTextViewModel.sttState.collectAsState()
    val dataPrefUiState by dataStorePrefViewModel.uiState.collectAsState()

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    LaunchedEffect(recordAudioPermissionLauncher) { //ask the permission on launch
        recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
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
            sttState.spokenPromptText.length,
            clearText = voiceToTextViewModel::clearTexts,
        )
        ResultsLazyList(
            uiState = responseUiState,
            isListening = sttState.isSpeaking,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            answersList = resultUiState.answersList,
            stopListening = voiceToTextViewModel::stopListening,
            startListening = voiceToTextViewModel::startListening,
            changeCanRunAgain = voiceToTextViewModel::changeCanRunAgain,
            weightModifier = Modifier.weight(1f),
            vibrate = dataPrefUiState.vibration,
            autoMic = dataPrefUiState.autoMic,
            isLoading = dataPrefUiState.isLoading,
        )

    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        TextFieldUpperButtons(
            geminiViewModel::sendPrompt,
            userData = userData,
            startListening = voiceToTextViewModel::startListening,
            stopListening = voiceToTextViewModel::stopListening,
            changeCanRunAgain = voiceToTextViewModel::changeCanRunAgain,
            isEnabled = (sttState.canRunAgain && !isServiceRunning),
            isListening = sttState.isSpeaking,
            vibrate = dataPrefUiState.vibration,
            autoMic = dataPrefUiState.autoMic,
            isLoading = dataPrefUiState.isLoading,
            prompt = {
                (sttState.fullTranscripts + sttState.partialTranscripts)
                    .joinToString().drop(sttState.spokenPromptText.length)
            },
            changeSpokenPromptText = voiceToTextViewModel::changeSpokenPromptText,
            recordAudioPermissionLauncher = recordAudioPermissionLauncher
        )
    }
}

@Composable
fun ResultsLazyList(
    uiState: ResponseState,
    modifier: Modifier,
    answersList: List<String>,
    startListening: () -> Unit,
    stopListening: () -> Unit,
    weightModifier: Modifier,
    isListening: Boolean,
    changeCanRunAgain: (Boolean) -> Unit,
    vibrate: Boolean,
    autoMic: Boolean,
    isLoading: Boolean,
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val tts = if (!isLoading) { //initialize after loaded so the onFinished is with the right values
        rememberTextToSpeech(
            onFinished = {
                coroutineScope.launch {
                    HapticUtils.triggerVibration(
                        canVibrate = vibrate,
                        context = context,
                        milliseconds = 10
                    )
                    changeCanRunAgain(true)
                    if (autoMic && PermissionUtils.checkRecordPermission(context)) startListening()
                }
            }
        )
    } else {
        null
    }

    // Automatically scroll when the list updates
    LaunchedEffect(answersList) {
        if (answersList.isNotEmpty()) {
            listState.animateScrollToItem(answersList.size - 3)
        }
    }

    //here add the list of the results
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState != ResponseState.Initial) {
            items(answersList) { answer ->
                ResultCard(
                    result = answer,
                    tts = tts,
                    stopListening = stopListening,
                    isListening = isListening,
                    vibrate = vibrate
                ) { changeCanRunAgain(false) }
            }
        }
        item {
            when (uiState) {
                is ResponseState.Error -> {
                    ResultText(
                        uiState.errorMessage,
                        textColor = MaterialTheme.colorScheme.error,
                        inputTextAlign = TextAlign.Center
                    )
                }

                is ResponseState.Initial -> {
                    ResultText(
                        stringResource(R.string.results_placeholder),
                        inputTextAlign = TextAlign.Center,
                        textColor = Color.Gray
                    )
                }

                is ResponseState.Loading -> {
                    Spacer(modifier = Modifier.padding(15.dp))
                    CircularProgressIndicator(modifier = modifier)
                    Spacer(modifier = weightModifier)
                }

                is ResponseState.Success -> {} // no need
            }
        }
        item {
            Spacer(modifier = Modifier.size(200.dp))
        }
    }
}

@Composable
fun ResultCard(
    result: String,
    tts: MutableState<TextToSpeech?>?,
    stopListening: () -> Unit,
    isListening: Boolean,
    vibrate: Boolean,
    canRecordFun: () -> Unit
) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        shape = RoundedCornerShape(16.dp), // Ensure the shape is consistent
        modifier = Modifier
            .padding(start = 18.dp, end = 18.dp, top = 10.dp)
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(16.dp)
            ),
        onClick = {
            HapticUtils.triggerVibration(canVibrate = vibrate, context = context, milliseconds = 10)
            if (isListening) {
                stopListening()
            }
            canRecordFun()
            tts?.value?.speak(
                result, TextToSpeech.QUEUE_FLUSH, null, ""
            )
        }
    ) {
        ResultText(result, inputTextAlign = TextAlign.Start)
    }
}

@Composable
fun ResultText(
    resultText: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    inputTextAlign: TextAlign = TextAlign.Center,
    inputStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Text(
        text = resultText.trim(),
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
    spokenTextUsed: Int,
    clearText: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val combinedText = listOfSpokenText + listOfSpokenEarlyText
    val fullText = combinedText.joinToString(" ")
        .removePrefix(" ")
        .replace(Regex(" +"), " ") // Replace multiple spaces with a single space

    LaunchedEffect(combinedText.size) {
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
                    .padding(10.dp)
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    text = buildAnnotatedString {
                        val grayText = fullText.take(spokenTextUsed)
                        val defaultText = fullText.drop(spokenTextUsed)

                        append(grayText)
                        addStyle(
                            style = SpanStyle(color = Color.Gray),
                            start = 0,
                            end = grayText.length
                        )

                        append(defaultText)
                        addStyle(
                            style = SpanStyle(color = MaterialTheme.colorScheme.onBackground),
                            start = grayText.length,
                            end = grayText.length + defaultText.length
                        )
                    },
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
                stopListening()
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
    sendPrompt: (String, List<UserData>) -> Unit,
    startListening: () -> Unit,
    stopListening: () -> Unit,
    isListening: Boolean,
    isEnabled: Boolean,
    prompt: () -> String,
    changeSpokenPromptText: () -> Unit,
    userData: List<UserData>,
    changeCanRunAgain: (Boolean) -> Unit,
    vibrate: Boolean,
    autoMic: Boolean,
    isLoading: Boolean,
    recordAudioPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
) {
    Column {
        Row(
            modifier = Modifier.padding(end = 16.dp, start = 5.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            OutlinedCustomIconButton(
                startListening = startListening,
                stopListening = stopListening,
                isListening = isListening,
                isEnabled = isEnabled,
                recordAudioPermissionLauncher = recordAudioPermissionLauncher
            )
            Spacer(modifier = Modifier.padding(10.dp))
            OutlinedCustomButton(
                sendPrompt = sendPrompt,
                userData = userData,
                prompt = prompt,
                changeSpokenPromptText = changeSpokenPromptText
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
                isListening = isListening,
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
fun OutlinedCustomButton(
    sendPrompt: (String, List<UserData>) -> Unit,
    userData: List<UserData>,
    prompt: () -> String,
    changeSpokenPromptText: () -> Unit
) {
    val isEnabled = prompt().trim().isNotEmpty()

    OutlinedButton(
        onClick = {
            sendPrompt(prompt(), userData)
            changeSpokenPromptText()
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
        Text(
            text = stringResource(R.string.ai),
            color = if (isEnabled) MaterialTheme.colorScheme.onBackground else Color.Gray
        )
    }
}

@Composable
fun OutlinedCustomIconButton(
    startListening: () -> Unit,
    stopListening: () -> Unit,
    isListening: Boolean,
    isEnabled: Boolean,
    recordAudioPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
) {
    val context = LocalContext.current

    OutlinedButton(
        onClick = {
            when {
                isListening -> {
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
            imageVector = if (isListening) Icons.Rounded.Mic else Icons.Rounded.MicOff,
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

    val tts = if (!isLoading) { //initialize after loaded so the onFinished is with the right values
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
    } else {
        null
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
                    },
                    modifier = Modifier
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        topEnd = 50.dp,
                        bottomEnd = 50.dp
                    ),
                    enabled = prompt.trim().isNotEmpty(),
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
