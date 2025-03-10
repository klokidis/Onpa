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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ptyxiakh.R
import com.example.ptyxiakh.viewmodels.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUp(
    navigate: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
) {
    var name by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var isDropDownMenuClicked by remember { mutableStateOf(false) }
    val availableLanguages = listOf<String>("en", "el-GR")
    var selectedLanguage by rememberSaveable { mutableStateOf(availableLanguages[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(10.dp))
        Icon(
            modifier = Modifier
                .size(170.dp),
            imageVector = Icons.Default.Person,
            contentDescription = stringResource(R.string.icon),
        )

        Text(
            text = stringResource(R.string.sign_up),
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
            text = stringResource(R.string.sign_up_meaning),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 40.dp
                ),
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            label = {
                Text(
                    stringResource(R.string.enter_name),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp, end = 25.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = stringResource(R.string.user_icon)
                )
            },
            shape = RoundedCornerShape(20.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 35.dp, end = 35.dp, bottom = 5.dp)
        ) {
            Text(
                text = stringResource(R.string.select_language),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.clickable {
                isDropDownMenuClicked = true
            }) {
                Text(
                    if (selectedLanguage == "en") {
                        stringResource(R.string.eng)
                    } else {
                        stringResource(R.string.gr)
                    },
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
                DropdownMenu(
                    expanded = isDropDownMenuClicked,
                    modifier = Modifier.wrapContentSize(),
                    onDismissRequest = { isDropDownMenuClicked = false }
                ) {
                    Row {
                        Column {
                            availableLanguages.forEach { language ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedLanguage = language
                                        isDropDownMenuClicked = false
                                    },
                                    text = {
                                        Text(
                                            if (language == "en") {
                                                stringResource(R.string.eng)
                                            } else {
                                                stringResource(R.string.gr)
                                            },
                                            color = if (selectedLanguage == language) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                Color.Gray
                                            },
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.padding(20.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    if (name.trim().isNotEmpty()) {
                        coroutineScope.launch {
                            userViewModel.addUser(name.trim(), selectedLanguage)
                        }
                    }
                },
                enabled = name.trim().isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.next_screen_icon)
                )
            }
        }
    }
}