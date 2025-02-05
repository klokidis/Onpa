package com.example.ptyxiakh.ui

import android.Manifest
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ptyxiakh.ai.GeminiViewModel
import com.example.ptyxiakh.R
import com.example.ptyxiakh.ai.ResponseState
import com.example.ptyxiakh.stt.VoiceToTextViewModel
import com.example.ptyxiakh.tts.rememberTextToSpeech


@Composable
fun MainScreen(
    navigateSettings: () -> Unit,
    geminiViewModel: GeminiViewModel = viewModel(),
    voiceToTextViewModel: VoiceToTextViewModel = viewModel()
) {
    val placeholderResult = stringResource(R.string.results_placeholder)
    val result by rememberSaveable { mutableStateOf(placeholderResult) }

    val responseUiState by geminiViewModel.responseState.collectAsState()
    val resultUiState by geminiViewModel.resultUiState.collectAsState()
    val sttState by voiceToTextViewModel.sttState.collectAsState()

    var canRecord by remember { mutableStateOf(false) }

    val recordAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            canRecord = isGranted
        }
    )

    LaunchedEffect(key1 = recordAudioLauncher) {
        recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopButtons(navigateSettings)
        SpeechToTextUi(sttState.fullTranscripts, sttState.partialTranscripts)
        ResultsLazyList(
            responseUiState,
            result,
            Modifier.align(Alignment.CenterHorizontally),
            resultUiState.answersList,
            Modifier.weight(1f)
        )

    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        TextFieldUpperButtons(
            geminiViewModel::sendPrompt,
            startListening = voiceToTextViewModel::startListening,
            stopListening = voiceToTextViewModel::stopListening,
            isEnabled = !sttState.hasError,
            isListening = sttState.isSpeaking,
        )
    }
}

@Composable
private fun ResultsLazyList(
    uiState: ResponseState,
    result: String,
    modifier: Modifier,
    answersList: List<String>,
    weightModifier: Modifier
) {
    val listState = rememberLazyListState()
    var result1 = result
    val tts = rememberTextToSpeech()

    // Automatically scroll when the list updates
    LaunchedEffect(answersList) {
        if (answersList.isNotEmpty()) {
            listState.animateScrollToItem(answersList.size - 2)
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
                ResultCard(answer,tts)
            }
        }
        item {
            when (uiState) {
                is ResponseState.Error -> {
                    result1 = uiState.errorMessage
                    ResultText(
                        result1,
                        textColor = MaterialTheme.colorScheme.error,
                        inputTextAlign = TextAlign.Center
                    )
                }

                is ResponseState.Initial -> {
                    ResultText(result1, inputTextAlign = TextAlign.Center)
                }

                ResponseState.Loading -> {
                    Spacer(modifier = Modifier.padding(15.dp))
                    CircularProgressIndicator(modifier = modifier)
                    Spacer(modifier = weightModifier)
                }

                is ResponseState.Success -> {} // no need
            }
        }
        item {
            ResultCard("hello there what is up", tts)
        }
        item {
            Spacer(modifier = Modifier.size(200.dp))
        }
    }
}

@Composable
private fun ResultCard(
    result: String,
    tts: MutableState<TextToSpeech?>,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        modifier = Modifier
            .padding(start = 18.dp, end = 18.dp, top = 10.dp)
            .fillMaxWidth()
            .clickable {
                tts.value?.speak(
                    result, TextToSpeech.QUEUE_FLUSH, null, ""
                )
            }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(16.dp)
            )
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
private fun SpeechToTextUi(listOfSpokenText: List<String>, listOfSpokenEarlyText: List<String>) {
    OutlinedTextField(
        value = listOfSpokenText.joinToString() + listOfSpokenEarlyText.joinToString(),
        singleLine = false,
        enabled = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(start = 10.dp, end = 10.dp),
        onValueChange = {},
        label = { Text("") },
        textStyle = TextStyle(
            textAlign = TextAlign.Start,
            //fontFamily = FontFamily(Font(R.font.radiocanadabigregular)),
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.onBackground
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedBorderColor = MaterialTheme.colorScheme.onBackground, // Set the focused outline to black
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
        ),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun TopButtons(navigateSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(end = 5.dp, start = 5.dp, top = 5.dp)
    ) {
        Icon(
            modifier = Modifier
                .size(30.dp)
                .clickable { },
            imageVector = ImageVector.vectorResource(R.drawable.menu_24px),
            contentDescription = "",
        )

        Spacer(modifier = Modifier.weight(1f))
        Row {
            Icon(
                modifier = Modifier
                    .size(30.dp)
                    .clickable { navigateSettings() },
                imageVector = ImageVector.vectorResource(R.drawable.settings_24px),
                contentDescription = "",
            )
        }
    }
}

@Composable
private fun TextFieldUpperButtons(
    sendPrompt: (String) -> Unit,
    startListening: () -> Unit,
    stopListening: () -> Unit,
    isListening: Boolean,
    isEnabled: Boolean
) {
    var prompt by rememberSaveable { mutableStateOf("") }
    Column {
        Row(
            modifier = Modifier.padding(end = 16.dp, start = 5.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            OutlinedCustomIconButton(
                startListening = startListening,
                stopListening = stopListening,
                isListening = isListening,
                isEnabled = isEnabled
            )
            Spacer(modifier = Modifier.padding(10.dp))
            OutlinedCustomButton(sendPrompt) { prompt }
        }
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 10.dp, top = 5.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // Ensures both children match their heights
        ) {
            TextFieldWithInsideIcon(
                { prompt },
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) { prompt = it }
        }
    }
}

@Composable
private fun OutlinedCustomButton(sendPrompt: (String) -> Unit, prompt: () -> String) {
    val isEnabled = prompt().trim().isNotEmpty()

    OutlinedButton(
        onClick = {
            sendPrompt(prompt())
        },
        enabled = isEnabled,
        border = BorderStroke(
            width = 1.dp,
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
private fun OutlinedCustomIconButton(
    startListening: () -> Unit,
    stopListening: () -> Unit,
    isListening: Boolean,
    isEnabled: Boolean,
) {
    OutlinedButton(
        onClick = {
            if (isListening) {
                stopListening()
            } else {
                startListening()
            }
        },
        enabled = true,
        border = BorderStroke(
            width = 1.dp,
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
private fun TextFieldWithInsideIcon(
    prompt: () -> String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    // State to track the focus of the TextField
    var isFocused by rememberSaveable { mutableStateOf(false) }

    val tts = rememberTextToSpeech()

    OutlinedTextField(
        value = prompt(),
        label = { Text("") },
        onValueChange = { onValueChange(it) },
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
                        tts.value?.speak(
                            prompt().trim(), TextToSpeech.QUEUE_FLUSH, null, ""
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
                    enabled = prompt().trim().isNotEmpty(),
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
fun MainScreenPreview() {
    MainScreen(navigateSettings = { })
}