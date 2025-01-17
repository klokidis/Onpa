package com.example.ptyxiakh.stt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class VoiceToTextState(
    val spokenText: String = "",
    val isSpeaking: Boolean = false,
    val error: String? = null
)

class VoiceToTextParser(
    private val context: Context
) : RecognitionListener {

    private val _sttState = MutableStateFlow(VoiceToTextState())
    val sttState: StateFlow<VoiceToTextState> = _sttState.asStateFlow()

    val recognizer = SpeechRecognizer.createSpeechRecognizer(context)

    fun startListening(languageCode: String = "en") {
        _sttState.update {
            VoiceToTextState()
        }

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _sttState.update {
                it.copy(
                    error = "the SpeechRecognizer is jot available"
                )
            }
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
        }

        recognizer.setRecognitionListener(this)
        recognizer.startListening(intent)
        _sttState.update {
            it.copy(
                isSpeaking = true
            )
        }
    }

    fun stopListening() {
        recognizer.stopListening()
        _sttState.update {
            it.copy(
                isSpeaking = false
            )
        }
    }

    override fun onReadyForSpeech(params: Bundle?) {
        _sttState.update {
            it.copy(
                error = null
            )
        }
    }

    override fun onBeginningOfSpeech() = Unit //don't need

    override fun onRmsChanged(rmsdB: Float) = Unit

    override fun onBufferReceived(buffer: ByteArray?) = Unit

    override fun onEndOfSpeech() {
        _sttState.update {
            it.copy(
                isSpeaking = false
            )
        }
    }

    override fun onError(error: Int) {
        if (error == SpeechRecognizer.ERROR_CLIENT) {
            return
        }
        _sttState.update {
            it.copy(
                error = "Error: $error"
            )
        }
    }

    override fun onResults(results: Bundle?) {
        results
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.getOrNull(0)
            ?.let { result ->
                _sttState.update {
                    it.copy(
                        spokenText = result
                    )
                }
            }
    }

    override fun onPartialResults(partialResults: Bundle?) = Unit

    override fun onEvent(eventType: Int, params: Bundle?) = Unit
}