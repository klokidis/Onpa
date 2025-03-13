package com.example.ptyxiakh.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ptyxiakh.R
import com.example.ptyxiakh.viewmodels.SoundDetectionViewModel

@Composable
fun SoundDetectionScreen(
    navigate: () -> Unit,
    soundDetectionViewModel: SoundDetectionViewModel = hiltViewModel(),
) {
    val soundDetectorState by soundDetectionViewModel.soundDetectorState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var isShowDescription by rememberSaveable { mutableStateOf(true) }

    // Track permission state
    var hasPermission by remember { mutableStateOf(false) }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
            if (!isGranted) {
                Toast.makeText(
                    context,
                    "Permission denied! Cannot record audio.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    LaunchedEffect(recordAudioPermissionLauncher) {
        // Check if the permission is already granted when the screen is first loaded
        hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = {
                    soundDetectionViewModel.stopListening()
                    navigate()
                },
                modifier = Modifier
                    .padding(start = 5.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Use the appropriate icon
                    contentDescription = stringResource(id = R.string.back),
                    modifier = Modifier.size(35.dp)
                )
            }
        }
        Spacer(modifier = Modifier.padding(10.dp))
        Icon(
            modifier = Modifier
                .size(150.dp),
            painter = painterResource(R.drawable.noise_aware_24px),
            contentDescription = stringResource(R.string.icon),
        )

        if (isShowDescription) {
            Text(
                text = stringResource(R.string.sound_recognition),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 30.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 50.dp,
                        bottom = 10.dp,
                    ),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.sound_recognition_meaning),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 10.dp,
                        end = 10.dp,
                        bottom = 40.dp
                    ),
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = stringResource(R.string.sound_detected) + "\n" + soundDetectorState.detectedPrimarySound,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 30.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 50.dp,
                    ),
                textAlign = TextAlign.Center
            )
           /* Text(
                text = stringResource(R.string.or) + "\n" + soundDetectorState.detectedSecondarySound,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )*/
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                if (!soundDetectorState.isListening) {
                    if (hasPermission) {
                        isShowDescription = false
                        // If permission is granted, start listening
                        soundDetectionViewModel.startListening()
                    } else {
                        // Request permission if not granted
                        recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                } else {
                    isShowDescription = true
                    soundDetectionViewModel.stopListening()
                }
            },
            shape = CircleShape,
            modifier = Modifier
                .size(80.dp),
            contentPadding = PaddingValues(2.dp)
        ) {
            Icon(
                imageVector = if (soundDetectorState.isListening) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = if (soundDetectorState.isListening) stringResource(R.string.stop_sound) else stringResource(
                    R.string.start_sound
                ),
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.padding(bottom = 40.dp))
    }

}

