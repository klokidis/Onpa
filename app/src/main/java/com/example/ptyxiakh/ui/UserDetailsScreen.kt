package com.example.ptyxiakh.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ptyxiakh.R
import com.example.ptyxiakh.model.User
import com.example.ptyxiakh.model.UserData
import com.example.ptyxiakh.viewmodels.UserDetailsViewModel

@Composable
fun UserDetailsScreen(
    user: User?,
    userData: List<UserData>,
    navigate: () -> Unit,
    userDetailsViewModel: UserDetailsViewModel = viewModel(),
    addOneUserData: (Int, String, String) -> Unit,
) {

    val userDetailsUiState by userDetailsViewModel.userDetailsUiState.collectAsState()
    val scrollState = rememberScrollState()
    val firstList: List<UserData> = listOf(
        UserData(
            userId = user?.userId ?: 0,
            category = stringResource(R.string.name),
            value = user?.userName ?: "name"
        ),
        UserData(
            userId = user?.userId ?: 0,
            category = stringResource(R.string.language),
            value = if (user?.voiceLanguage == "en") {
                stringResource(R.string.eng)
            } else {
                stringResource(R.string.gr)
            }
        ),
        )

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
            imageVector = Icons.Default.DataUsage,
            contentDescription = stringResource(R.string.data_icon),
        )

        Text(
            text = stringResource(R.string.user_details),
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
            text = stringResource(R.string.user_details_meaning),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 20.dp //5
                ),
            textAlign = TextAlign.Center
        )
        /*
                Text(
                    text = stringResource(R.string.for_user) + user?.userName + ":",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        ),
                    textAlign = TextAlign.Center
                )
        */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 10.dp,
                    end = 30.dp,
                    bottom = 5.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.category),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(start = 115.dp))
            Text(
                text = stringResource(R.string.value),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }

        if (userData.isEmpty()) {//first data
            UserData(firstList)
        }

        UserData(userData) //saved data

        userDetailsUiState.newUserDetails.forEachIndexed { index, pair -> //new data before saving
            NewUserData(index, userDetailsViewModel::editValuesBasedOnLength)
        }

        ListButtons(userDetailsViewModel::addLine, userDetailsViewModel::minusLine)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                addOneUserData(
                    user?.userId ?: 0,
                    firstList.first().category,
                    firstList.first().value
                )
                addOneUserData(
                    user?.userId ?: 0,
                    firstList[1].category,
                    firstList[1].value
                )
                if (user?.userId != null && userDetailsUiState.newUserDetails.isNotEmpty()) {
                    userDetailsUiState.newUserDetails.forEachIndexed { index, pair ->
                        if (pair.first.trim().isNotEmpty() && pair.second.trim().isNotEmpty()) {
                            addOneUserData(user.userId, pair.first, pair.second)
                        }
                    }
                } else {
                    navigate()
                }
                userDetailsViewModel.emptyList() //deletes after passing them to data
            },
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(0.dp),
            enabled = user?.userId != null
        ) {
            Text(
                text = stringResource(R.string.create_account),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ListButtons(addLine: () -> Unit, minusLast: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                minusLast()
            },
            modifier = Modifier
                .weight(1f),
            contentPadding = PaddingValues(0.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = stringResource(R.string.remove),
            )
        }
        Spacer(modifier = Modifier.padding(start = 60.dp))
        Button(
            onClick = {
                addLine()
            },
            modifier = Modifier
                .weight(1f),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add),
            )
        }
    }
}

@Composable
fun NewUserData(
    index: Int,
    editValuesBasedOnLength: (Int, String, String) -> Unit
) {
    val category = rememberSaveable { mutableStateOf("") }
    val value = rememberSaveable { mutableStateOf("") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = category.value,
            onValueChange = {
                category.value = it
                editValuesBasedOnLength(index, category.value, value.value)
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            label = {
                Text(
                    "",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            maxLines = 1,
            modifier = Modifier
                .width(130.dp),
            singleLine = true,
            shape = RoundedCornerShape(20.dp)
        )
        Icon(
            modifier = Modifier
                .size(50.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
            contentDescription = stringResource(R.string.arrow),
        )
        OutlinedTextField(
            value = value.value,
            onValueChange = {
                value.value = it
                editValuesBasedOnLength(index, category.value, value.value)
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            label = {
                Text(
                    "",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            maxLines = 1,
            modifier = Modifier
                .width(130.dp)
                .wrapContentSize(),
            singleLine = true,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun UserData(
    userData: List<UserData>,
) {
    userData.forEach { thisData ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            OneData(thisData.category)
            Icon(
                modifier = Modifier
                    .size(50.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                contentDescription = stringResource(R.string.arrow),
            )
            OneData(thisData.value)
        }
    }
}

@Composable
fun OneData(
    name: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(130.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}