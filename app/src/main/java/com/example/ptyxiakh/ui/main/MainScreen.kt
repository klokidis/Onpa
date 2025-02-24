package com.example.ptyxiakh.ui.main

import android.Manifest
import android.speech.tts.TextToSpeech
import android.widget.Toast
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
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ptyxiakh.ai.GeminiViewModel
import com.example.ptyxiakh.R
import com.example.ptyxiakh.ai.ResponseState
import com.example.ptyxiakh.data.viewmodels.UserViewModel
import com.example.ptyxiakh.stt.VoiceToTextViewModel
import com.example.ptyxiakh.tts.rememberTextToSpeech
import kotlinx.coroutines.launch


@Composable
fun MainScreen(
    navigateSettings: () -> Unit,
    geminiViewModel: GeminiViewModel = viewModel(),
    voiceToTextViewModel: VoiceToTextViewModel = viewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val responseUiState by geminiViewModel.responseState.collectAsState()
    val resultUiState by geminiViewModel.resultUiState.collectAsState()
    val sttState by voiceToTextViewModel.sttState.collectAsState()
    val context = LocalContext.current
    var canRecord by remember { mutableStateOf(false) }

    val recordAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            canRecord = isGranted
        }
    )

    LaunchedEffect(recordAudioLauncher) {
        recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    LaunchedEffect(sttState.offlineError) {
        // Inform the user about the missing service
        if (sttState.offlineError) {
            Toast.makeText(
                context,
                "Speech recognition is offline. Please enable Wi-Fi.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(!sttState.availableSTT) {
        if (!sttState.availableSTT) {
            Toast.makeText(
                context,
                "Speech recognition is not available. Please install or enable Google Speech Services.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopButtons(navigateSettings)
        SpeechToTextUi(
            sttState.fullTranscripts,
            sttState.partialTranscripts,
            sttState.spokenPromptText.length,
            clearText = voiceToTextViewModel::clearTexts,
        )
        ResultsLazyList(
            uiState = responseUiState,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            answersList = resultUiState.answersList,
            stopListening = voiceToTextViewModel::stopListening,
            startListening = voiceToTextViewModel::startListening,
            weightModifier = Modifier.weight(1f)
        )

    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        TextFieldUpperButtons(
            geminiViewModel::sendPrompt,
            changeLanguage = voiceToTextViewModel::changeLanguage,
            startListening = voiceToTextViewModel::startListening,
            stopListening = voiceToTextViewModel::stopListening,
            isEnabled = true,
            isListening = sttState.isSpeaking,
            prompt = {
                (sttState.fullTranscripts + sttState.partialTranscripts)
                    .joinToString().drop(sttState.spokenPromptText.length)
            },
            changeSpokenPromptText = voiceToTextViewModel::changeSpokenPromptText,
        )
    }
}

@Composable
fun ResultsLazyList(
    uiState: ResponseState,
    modifier: Modifier,
    answersList: List<String>,
    startListening: (String) -> Unit,
    stopListening: () -> Unit,
    weightModifier: Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val tts = rememberTextToSpeech {
        coroutineScope.launch {
            startListening("el-GR")
        }
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
                ResultCard(answer, tts, stopListening)
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
    tts: MutableState<TextToSpeech?>,
    stopListening: () -> Unit,
) {
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
            stopListening()
            tts.value?.speak(
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
    inputStyle: TextStyle = MaterialTheme.typography.bodyLarge,
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
        .removePrefix(" ") //removes the first " "
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
        Text(
            modifier = Modifier
                .padding(10.dp)
                .verticalScroll(scrollState),
            text = buildAnnotatedString {
                // Ensure the indices are correct for take and drop operations
                val grayText = fullText.take(spokenTextUsed) // Gray-colored text
                val defaultText = fullText.drop(spokenTextUsed) // Default-colored text

                // Add the gray text
                append(grayText)
                addStyle(
                    style = SpanStyle(color = Color.Gray),
                    start = 0,
                    end = grayText.length
                )

                // Add the default text
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
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            IconButton(
                onClick = {
                    clearText()
                },
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .padding(5.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .size(27.dp),
                    painter = painterResource(R.drawable.mop_24px),
                    contentDescription = stringResource(R.string.clear_text),
                )
            }
        }
    }
}

@Composable
fun TopButtons(navigateSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .wrapContentSize()
    ) {
        IconButton(
            onClick = { },
            modifier = Modifier
                .padding(start = 5.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(30.dp),
                painter = painterResource(R.drawable.menu_24px),
                contentDescription = "",
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { navigateSettings() },
            modifier = Modifier
                .padding(end = 5.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(30.dp),
                painter = painterResource(R.drawable.settings_24px),
                contentDescription = "",
            )
        }
    }
}

@Composable
fun TextFieldUpperButtons(
    sendPrompt: (String) -> Unit,
    startListening: (String) -> Unit,
    changeLanguage: (String) -> Unit,
    stopListening: () -> Unit,
    isListening: Boolean,
    isEnabled: Boolean,
    prompt: () -> String,
    changeSpokenPromptText: () -> Unit,
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
                changeLanguage = changeLanguage,
            )
            Spacer(modifier = Modifier.padding(10.dp))
            OutlinedCustomButton(
                sendPrompt = sendPrompt,
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
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
fun OutlinedCustomButton(
    sendPrompt: (String) -> Unit,
    prompt: () -> String,
    changeSpokenPromptText: () -> Unit
) {
    val isEnabled = prompt().trim().isNotEmpty()

    OutlinedButton(
        onClick = {
            sendPrompt(prompt())
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
    startListening: (String) -> Unit,
    stopListening: () -> Unit,
    changeLanguage: (String) -> Unit,
    isListening: Boolean,
    isEnabled: Boolean,
) {
    OutlinedButton(
        onClick = {
            when {
                isListening -> {
                    stopListening()
                }

                else -> {
                    changeLanguage("el-GR")
                    startListening("el-GR")
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
    startListening: (String) -> Unit,
) {
    // State to track the focus of the TextField
    var isFocused by rememberSaveable { mutableStateOf(false) }
    var prompt by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val tts = rememberTextToSpeech {
        coroutineScope.launch {
            startListening("el-GR")
        }
    }

    OutlinedTextField(
        value = prompt,
        label = { Text("") },
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
                        stopListening()
                        tts.value?.speak(
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
        TopButtons(navigateSettings = {})
    }
}
