package com.example.onpa.features.tts

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun rememberTextToSpeech(onFinished: () -> Unit): MutableState<TextToSpeech?> {
    val context = LocalContext.current
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Get the default TTS locale from the system settings
                val defaultTtsLocale = tts.value?.voice?.locale ?: Locale.getDefault()
                tts.value?.language = defaultTtsLocale

                Log.d("TTS", "Using language: ${defaultTtsLocale.displayLanguage}")
                Log.d("TTS", "Available languages: ${tts.value?.availableLanguages?.joinToString()}")

                tts.value?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.d("TTS", "Started speaking")
                    }

                    override fun onDone(utteranceId: String?) {
                        onFinished()
                    }

                    override fun onError(utteranceId: String?) {
                        Log.e("TTS", "Error occurred during speech.")
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        Log.e("TTS", "Error occurred during speech. Code: $errorCode")
                    }
                })
            }
        }
        tts.value = textToSpeech

        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
            tts.value = null
        }
    }
    return tts
}