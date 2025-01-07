package com.example.ptyxiakh.ui

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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


@Composable
fun MainScreen(
    geminiViewModel: GeminiViewModel = viewModel()
) {
    val placeholderResult = stringResource(R.string.results_placeholder)
    val result by rememberSaveable { mutableStateOf(placeholderResult) }
    val responseUiState by geminiViewModel.responseState.collectAsState()
    val resultUiState by geminiViewModel.resultUiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopButtons()
        SpeechToTextUi()
        ResultsUi(
            responseUiState,
            result,
            Modifier.Companion.align(Alignment.CenterHorizontally),
            resultUiState.answersList,
            Modifier.Companion.weight(1f)
        )

    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        BottomUi(geminiViewModel::sendPrompt)
    }
}

@Composable
private fun ResultsUi(
    uiState: ResponseState,
    result: String,
    modifier: Modifier,
    answersList: List<String>,
    weightModifier: Modifier
) {
    val listState = rememberLazyListState()
    var result1 = result
    var textColor = MaterialTheme.colorScheme.onSurface

    //here add the list of the results
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState != ResponseState.Initial) {
            items(answersList) { answer ->
                ResultCard(answer)
            }
        }
        item {
            when (uiState) {
                is ResponseState.Error -> {
                    textColor = MaterialTheme.colorScheme.error
                    result1 = uiState.errorMessage
                    ResultText(result1, textColor, inputTextAlign = TextAlign.Center)
                }

                is ResponseState.Initial -> {
                    ResultText(result1, textColor, inputTextAlign = TextAlign.Center)
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
            Spacer(modifier = Modifier.size(200.dp))
        }
    }
}

@Composable
private fun ResultCard(
    result1: String,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, top = 10.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        ResultText(result1, Color.Black, inputTextAlign = TextAlign.Start)
    }
}

@Composable
fun ResultText(
    resultText: String,
    textColor: Color,
    modifier: Modifier = Modifier,
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
private fun SpeechToTextUi() {
    OutlinedTextField(
        value = " ",
        singleLine = false,
        enabled = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
        onValueChange = {},
        label = { Text("") },
        textStyle = TextStyle(
            textAlign = TextAlign.Start,
            //fontFamily = FontFamily(Font(R.font.radiocanadabigregular)),
            fontSize = 26.sp
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedBorderColor = Color.Black, // Set the focused outline to black
            unfocusedBorderColor = Color.Black,
        ),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun TopButtons() {
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
                    .clickable { },
                imageVector = ImageVector.vectorResource(R.drawable.settings_24px),
                contentDescription = "",
            )
        }
    }
}

@Composable
private fun BottomUi(
    sendPrompt: (String) -> Unit,
) {
    var prompt by rememberSaveable { mutableStateOf("") }
    Column {
        Row(
            modifier = Modifier.padding(end = 16.dp, start = 5.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            CustomButton(sendPrompt) { prompt }
        }
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 10.dp, top = 5.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // Ensures both children match their heights
        ) {
            CustomTextField(
                { prompt },
                Modifier.Companion
                    .weight(1f)
                    .fillMaxHeight()
            ) { prompt = it }
        }
    }
}

@Composable
private fun CustomButton(sendPrompt: (String) -> Unit, prompt: () -> String) {
    OutlinedButton(
        onClick = {
            sendPrompt(prompt())
        },
        enabled = prompt().trim().isNotEmpty(),
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape), // Make it circular
        shape = CircleShape, // Ensure the button's shape is circular
        contentPadding = PaddingValues(0.dp) //remove extra padding
    ) {
        Text(text = stringResource(R.string.ai))
    }
}

@Composable
private fun CustomTextField(
    prompt: () -> String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = prompt(),
        label = { Text("") },
        onValueChange = { onValueChange(it) },
        modifier = modifier, // Makes the TextField fill the parent's height
        shape = RoundedCornerShape(50.dp),
        maxLines = 3,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
        ),
        trailingIcon = {
            OutlinedButton(
                onClick = {

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between line and icon
                ) {
                    Box(
                        modifier = Modifier
                            .width(1.dp) // Line thickness
                            .fillMaxHeight() // Line height
                            .background(Color.Black) // Line color
                    )
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
    MainScreen()
}