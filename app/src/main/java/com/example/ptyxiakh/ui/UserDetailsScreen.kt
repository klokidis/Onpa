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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    addOneUserData: (Int, String, String) -> Unit,
    deleteOneData: (Int) -> Unit,
    userDetailsViewModel: UserDetailsViewModel = viewModel(),
) {

    val userDetailsUiState by userDetailsViewModel.userDetailsUiState.collectAsState()
    val firstList: List<UserData> = listOf(
        //default values before saving user details
        UserData(
            userId = user?.userId ?: 0,
            category = stringResource(R.string.name),
            value = user?.userName ?: "name"
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
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

        if (userData.isEmpty()) {// before saving the user details
            UserData(firstList, null)
        }

        UserData(userData, deleteOneData) //saved data after saving the user details

        userDetailsUiState.newUserDetails.forEachIndexed { index, pair -> //new data before saving
            NewUserData(
                pair,
                index,
                userDetailsViewModel::setValuesBasedOnLength,
                userDetailsViewModel::minusNewUserDetailsLine
            )
        }

        ListButtons(userDetailsViewModel::addLine)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (userData.isEmpty()) { //if this is the first initialize add the basic data
                    addOneUserData(
                        user?.userId ?: 0,
                        firstList.first().category,
                        firstList.first().value
                    )
                }
                if (user?.userId != null && userDetailsUiState.newUserDetails.isNotEmpty()) {
                    userDetailsUiState.newUserDetails.forEachIndexed { index, pair ->
                        if (pair.first.trim().isNotEmpty() && pair.second.trim().isNotEmpty()) {
                            addOneUserData(user.userId, pair.first, pair.second)
                        }
                    }
                }
                navigate()
                userDetailsViewModel.emptyNewUserDetailsList() //deletes after passing them to data
            },
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(0.dp),
            enabled = user?.userId != null
        ) {
            Text(
                text = stringResource(if (userData.isEmpty()) R.string.create_account else R.string.this_continue),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ListButtons(addLine: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
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
    pair: Pair<String, String>,
    index: Int,
    editValuesBasedOnLength: (Int, String, String) -> Unit,
    remove: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = pair.first,
            onValueChange = {
                editValuesBasedOnLength(index, it, pair.second)
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
            value = pair.second,
            onValueChange = {
                editValuesBasedOnLength(index, pair.first, it)
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
        Spacer(modifier = Modifier.padding(start = 5.dp))
        IconButton(
            onClick = { remove(index) },
            modifier = Modifier
                .size(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.arrow),
            )
        }
    }
}

@Composable
fun UserData(
    userData: List<UserData>,
    deleteOneData: ((Int) -> Unit)?
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
            Spacer(modifier = Modifier.padding(start = 5.dp))
            if (deleteOneData != null && userData.size != 1) {
                IconButton(
                    onClick = { deleteOneData.invoke(thisData.id) },
                    modifier = Modifier
                        .size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.arrow),
                    )
                }
            }
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