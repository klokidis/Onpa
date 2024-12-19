package com.example.ptyxiakh.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ptyxiakh.GeminiViewModel
import com.example.ptyxiakh.R
import com.example.ptyxiakh.UiState


@Composable
fun MainScreen(
    geminiViewModel: GeminiViewModel = viewModel()
) {
    val placeholderPrompt = stringResource(R.string.prompt_placeholder)
    val placeholderResult = stringResource(R.string.results_placeholder)
    var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
    var result by rememberSaveable { mutableStateOf(placeholderResult) }
    val uiState by geminiViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = " ",
            singleLine = false,
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(10.dp),
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
        if (uiState is UiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.weight(1f))
        } else {
            var textColor = MaterialTheme.colorScheme.onSurface
            if (uiState is UiState.Error) {
                textColor = MaterialTheme.colorScheme.error
                result = (uiState as UiState.Error).errorMessage
            } else if (uiState is UiState.Success) {
                textColor = MaterialTheme.colorScheme.onSurface
                result = (uiState as UiState.Success).outputText
            }
            val scrollState = rememberScrollState()
            Text(
                text = "Button(\n" +
                        "    onClick = {\n" +
                        "        geminiViewModel.sendPrompt(prompt)\n" +
                        "    },\n" +
                        "    enabled = prompt.isNotEmpty(),\n" +
                        "    modifier = Modifier\n" +
                        "        .size(56.dp) // Set the size of the button (adjust as needed)\n" +
                        "        .clip(CircleShape), // Make it circular\n" +
                        "    shape = CircleShape, // Ensure the button's shape is circular\n" +
                        "    contentPadding = PaddingValues(0.dp) // Optional: remove extra padding\n" +
                        ") {\n" +
                        "    Text(\n" +
                        "        text = stringResource(R.string.action_go),\n" +
                        "        textAlign = TextAlign.Center, // Center the text\n" +
                        "        modifier = Modifier.fillMaxSize(), // Ensure the text takes up the button's space\n" +
                        "        style = MaterialTheme.typography.button // Optional: use the button style\n" +
                        "    )\n" +
                        "}\n" +
                        "Button(\\n\" +\n" +
                        "                        \"    onClick = {\\n\" +\n" +
                        "                        \"        geminiViewModel.sendPrompt(prompt)\\n\" +\n" +
                        "                        \"    },\\n\" +\n" +
                        "                        \"    enabled = prompt.isNotEmpty(),\\n\" +\n" +
                        "                        \"    modifier = Modifier\\n\" +\n" +
                        "                        \"        .size(56.dp) // Set the size of the button (adjust as needed)\\n\" +\n" +
                        "                        \"        .clip(CircleShape), // Make it circular\\n\" +\n" +
                        "                        \"    shape = CircleShape, // Ensure the button's shape is circular\\n\" +\n" +
                        "                        \"    contentPadding = PaddingValues(0.dp) // Optional: remove extra padding\\n\" +\n" +
                        "                        \") {\\n\" +\n" +
                        "                        \"    Text(\\n\" +\n" +
                        "                        \"        text = stringResource(R.string.action_go),\\n\" +\n" +
                        "                        \"        textAlign = TextAlign.Center, // Center the text\\n\" +\n" +
                        "                        \"        modifier = Modifier.fillMaxSize(), // Ensure the text takes up the button's space\\n\" +\n" +
                        "                        \"        style = MaterialTheme.typography.button // Optional: use the button style\\n\" +\n" +
                        "                        \"    )\\n\" +\n" +
                        "                        \"}\\n",
                textAlign = TextAlign.Start,
                color = textColor,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 16.dp, end = 16.dp)
                    .weight(1f)
                    .verticalScroll(scrollState)
            )
        }

    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column {
            Row(
                modifier = Modifier.padding(end = 16.dp, start = 5.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        geminiViewModel.sendPrompt(prompt)
                    },
                    enabled = prompt.isNotEmpty(),
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape), // Make it circular
                    shape = CircleShape, // Ensure the button's shape is circular
                    contentPadding = PaddingValues(0.dp) //remove extra padding
                ) {
                    Text(text = stringResource(R.string.ai))
                }
            }
            Row(
                modifier = Modifier.padding(all = 16.dp)
            ) {
                OutlinedTextField(
                    value = prompt,
                    label = { Text("") },
                    onValueChange = { prompt = it },
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(end = 5.dp)
                        .align(Alignment.CenterVertically),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Black, // Set the focused outline to black
                        unfocusedBorderColor = Color.Black,
                    ),
                )
                Button(
                    onClick = {

                    },
                    enabled = prompt.isNotEmpty()
                ) {
                    Text(text = stringResource(R.string.action_go))
                }
            }
        }
    }
}