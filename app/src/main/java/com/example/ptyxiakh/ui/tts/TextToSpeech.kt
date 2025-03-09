package com.example.ptyxiakh.ui.tts

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
                tts.value?.language = Locale.US
                tts.value?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.d("availableLanguages", tts.value?.availableLanguages.toString())
                    }

                    override fun onDone(utteranceId: String?) {
                        onFinished()
                    }

                    @Deprecated(
                        "Deprecated in Java", ReplaceWith(
                            "Log.e(\"TTS\", \"Error occurred during speech.\")",
                            "android.util.Log"
                        )
                    )
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
        }
    }
    return tts
}

/*
fun checkLanguages() {
val textToSpeech = TextToSpeech(context) { status ->
    if (status == TextToSpeech.SUCCESS) {
        val locale = Locale("es", "ES") // Spanish (Spain)

        // Check if the language is available
        val langAvailable = tts.value?.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE

        if (langAvailable) {
            // Set Spanish as the language
            tts.value?.language = locale
        } else {
            // Prompt the user to install the language data if it's not available
            val installStatus = tts.value?.setLanguage(locale)
            if (installStatus == TextToSpeech.LANG_MISSING_DATA || installStatus == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Direct the user to install language data via settings
                val intent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
                context.startActivity(intent)
            }
        }
    }
    Log.d("Voice", tts.value?.availableLanguages.toString())
}
tts.value = textToSpeech
}
*/