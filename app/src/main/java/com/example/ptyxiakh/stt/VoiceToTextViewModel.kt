package com.example.ptyxiakh.stt

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class VoiceToTextState(
    val listOfSpokenText: List<String> = listOf(),
    val spokenText: String = "",
    val isSpeaking: Boolean = false,
    val error: Boolean = false
)

class VoiceToTextViewModel(application: Application) : AndroidViewModel(application),
    RecognitionListener {

    private val _sttState = MutableStateFlow(VoiceToTextState())
    val sttState: StateFlow<VoiceToTextState> = _sttState.asStateFlow()

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(application.applicationContext)

    init {
        recognizer.setRecognitionListener(this)
    }

    fun startListening(languageCode: String = "en") {
        if (!SpeechRecognizer.isRecognitionAvailable(getApplication())) {
            _sttState.update { it.copy(error = true) }
            return
        }
        _sttState.update { it.copy(isSpeaking = true) }
        startRecognition(languageCode)
    }

    private fun startRecognition(languageCode: String) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
        }

        recognizer.startListening(intent)
        _sttState.update { it.copy(isSpeaking = true) }
    }

    fun stopListening() {
        _sttState.update { it.copy(isSpeaking = true) }
        recognizer.stopListening()
    }

    override fun onReadyForSpeech(params: Bundle?) {
        _sttState.update { it.copy(error = false) }
    }

    override fun onEndOfSpeech() {
        if (sttState.value.isSpeaking) {
            startRecognition("en") // Replace "en" with dynamic language if needed
        }
    }

    override fun onError(error: Int) {
        val message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
            else -> "Unknown error"
        }
        Log.e("VoiceToTextViewModel", "Error: $message")
    }

    override fun onResults(results: Bundle?) {
        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.getOrNull(0)
            ?.let { result ->
                _sttState.update {
                    it.copy(
                        spokenText = result,
                        listOfSpokenText = it.listOfSpokenText + result
                    )
                }
            }

        if (sttState.value.isSpeaking) {
            startRecognition("en") // Replace "en" with dynamic language if needed
        }
    }

    // Unused RecognitionListener methods
    override fun onBeginningOfSpeech() = Unit
    override fun onRmsChanged(rmsdB: Float) = Unit
    override fun onBufferReceived(buffer: ByteArray?) = Unit
    override fun onPartialResults(partialResults: Bundle?) = Unit
    override fun onEvent(eventType: Int, params: Bundle?) = Unit

    override fun onCleared() {
        super.onCleared()
        recognizer.destroy()
    }
}