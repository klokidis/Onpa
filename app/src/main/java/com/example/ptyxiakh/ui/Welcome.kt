package com.example.ptyxiakh.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ptyxiakh.R
import com.example.ptyxiakh.viewmodels.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun Welcome(
    navigateSetUp: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val scrollState = rememberScrollState()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300) // Small delay before starting animation
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(20.dp))
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(800)) + slideInHorizontally(
                initialOffsetX = { -100 }, animationSpec = tween(800)
            )
        ) {
            Icon(
                modifier = Modifier
                    .size(200.dp),
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.icon),
            )
        }
        AnimatedText(isVisible, stringResource(R.string.welcome), 1000)
        AnimatedText(isVisible, stringResource(R.string.app_details), 1400, isMedium = true)

        Spacer(modifier = Modifier.weight(0.6f))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(1600))
        ) {
            Button(
                onClick = {
                    userViewModel.deleteAllUser()//just in case
                    navigateSetUp()
                },
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                Text(
                    text = stringResource(R.string.sign_up),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(
                        top = 6.dp,
                        bottom = 6.dp,
                        start = 50.dp,
                        end = 50.dp
                    )
                )
            }
        }
    }
}

@Composable
fun AnimatedText(isVisible: Boolean, text: String, duration: Int, isMedium: Boolean = false) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(duration)) + slideInHorizontally(
            initialOffsetX = { -100 }, animationSpec = tween(duration)
        )
    ) {
        Text(
            text = text,
            style = if (isMedium) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge.copy(
                fontSize = 30.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = if (isMedium) 10.dp else 5.dp,
                ),
            textAlign = TextAlign.Center
        )
    }
}