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
    val spokenText: String = "",
    val partialText: String = "",
    val fullTranscripts: List<String> = emptyList(),
    val partialTranscripts: List<String> = emptyList(),
    val isSpeaking: Boolean = false,
    val hasError: Boolean = false,
    val offlineError: Boolean = false,
    val availableSTT: Boolean = true,
)

class VoiceToTextViewModel(application: Application) : AndroidViewModel(application),
    RecognitionListener {

    private val _sttState = MutableStateFlow(VoiceToTextState())
    val sttState: StateFlow<VoiceToTextState> = _sttState.asStateFlow()

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(application.applicationContext)

    init {
        recognizer.setRecognitionListener(this)
        Log.d(TAG, "VoiceToTextViewModel initialized")
    }

    fun startListening(languageCode: String = "en") {
        val context = getApplication<Application>().applicationContext

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _sttState.update { it.copy(availableSTT = false, hasError = true) }
            recognizer.stopListening()
        } else {
            Log.d(TAG, "Starting recognition with language: $languageCode")
            val intent = createRecognizerIntent(languageCode)
            recognizer.startListening(intent)
            _sttState.update { it.copy(availableSTT = true, isSpeaking = true, hasError = false) }
        }
    }

    private fun createRecognizerIntent(languageCode: String): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    fun stopListening() {
        Log.d(TAG, "Stopping recognition")
        recognizer.stopListening()
        _sttState.update { it.copy(isSpeaking = false) }
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(TAG, "Ready for speech")
        _sttState.update { it.copy(hasError = false) }
    }

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "Speech started")
    }

    override fun onRmsChanged(rmsdB: Float) {
        // Optional: Log or use rmsdB for visual feedback
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        // Not used
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "Speech ended")
    }

    override fun onError(error: Int) {
        val errorMessage = mapError(error)
        Log.e(TAG, "Recognition error: $errorMessage")

        // Retry recognition for recoverable errors
        if (_sttState.value.isSpeaking && isRecoverableError(error)) {
            Log.d(TAG, "Retrying recognition...")
            startListening()
        } else {
            _sttState.update { it.copy(hasError = true, isSpeaking = false) }
        }
    }

    private fun mapError(error: Int): String {
        Log.e(TAG, "error: $error")
        when (error) {
            SpeechRecognizer.ERROR_AUDIO -> return "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> return "Client error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> return "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> {
                _sttState.update { it.copy(offlineError = true) }
                return "Network error"
            }

            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                _sttState.update { it.copy(offlineError = true) }
                return "Network error"
            }

            SpeechRecognizer.ERROR_NO_MATCH -> return "No match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> return "Recognizer busy"
            SpeechRecognizer.ERROR_SERVER -> return "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> return "Speech timeout"
            else -> {
                _sttState.update { it.copy(offlineError = true) }
                return "Unknown error" //add unknow for offline error
            }
        }
    }

    private fun isRecoverableError(error: Int): Boolean {
        return error !in listOf(
            SpeechRecognizer.ERROR_CLIENT,
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS,
            SpeechRecognizer.ERROR_NETWORK,
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
        ) && !sttState.value.offlineError && sttState.value.availableSTT
    }

    override fun onResults(results: Bundle?) {
        val spokenText =
            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
        spokenText?.let {
            Log.d(TAG, "Final result: $it")
            _sttState.update { state ->
                state.copy(
                    spokenText = it,
                    fullTranscripts = state.fullTranscripts + it,
                    partialTranscripts = emptyList(),
                    partialText = ""
                )
            }
        }
        startListening() // Restart listening
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val partialText =
            partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
        partialText?.let { text ->
            // Remove commas and split into words
            val cleanedText = text.replace(",", "").trim()
            val existingWords = _sttState.value.partialTranscripts.flatMap { it.split(" ") }.toSet()

            // Add only new unique words
            val uniqueWords = cleanedText.split(" ").filterNot { existingWords.contains(it) }

            if (uniqueWords.isNotEmpty()) {
                val updatedText = uniqueWords.joinToString(" ")
                Log.d(TAG, "Filtered partial result: $updatedText")
                _sttState.update { state ->
                    state.copy(
                        partialText = updatedText,
                        partialTranscripts = state.partialTranscripts + updatedText
                    )
                }
            }
        }
    }


    override fun onEvent(eventType: Int, params: Bundle?) {
        // Not used
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "Clearing resources")
        recognizer.destroy()
    }

    companion object {
        private const val TAG = "VoiceToTextViewModel"
    }
}
