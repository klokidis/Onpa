package com.example.onpa.features.stt

import android.app.Application
import android.content.Intent
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.onpa.utils.SttLanguagesProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class VoiceToTextState(
    val fullTranscripts: List<String> = emptyList(),
    val partialTranscripts: List<String> = emptyList(),
    val isSpeaking: Boolean = false,
    val canRunAgain: Boolean = true, //so it doesn't loop
    val offlineError: Boolean = false,
    val availableSTT: Boolean = true,
    val aiClickedBeforeFinalResults: Boolean = false,
    val language: String = "en",
    val spokenPromptText: String = "", // text that sends to ai (removes the already used text)
    val isSttInitialized: Boolean = false
)

@HiltViewModel
class VoiceToTextViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application), RecognitionListener {

    private val _sttState = MutableStateFlow(VoiceToTextState())
    val sttState: StateFlow<VoiceToTextState> = _sttState.asStateFlow()
    private val supportedSpeechRecognitionLanguages = SttLanguagesProvider.supportedSpeechRecognitionLanguages //all the available languages of the google api

    private var noiseSuppressor: NoiseSuppressor? = null //reduce noise
    private var echoCanceler: AcousticEchoCanceler? = null //remove echo from audio input
    private var gainControl: AutomaticGainControl? =
        null //It boosts or reduces the microphone's input gain (volume) dynamically.

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(application.applicationContext)

    init {
        Log.d(TAG, "isSttInitialized false")
        _sttState.update { it.copy(isSttInitialized = false) }
        recognizer.setRecognitionListener(this)
        Log.d(TAG, "VoiceToTextViewModel initialized")
        // Apply noise reduction
        enableNoiseReduction()
        _sttState.update { it.copy(isSttInitialized = true) }
        Log.d(TAG, "isSttInitialized true")
    }

    fun startListening() {
        val context = getApplication<Application>().applicationContext
        _sttState.update { it.copy(isSpeaking = true) }
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _sttState.update { it.copy(availableSTT = false) }
            recognizer.stopListening()
            Log.d(TAG, "not available")
        } else {
            Log.d(TAG, "Starting recognition with language: ${sttState.value.language}")
            val intent = createRecognizerIntent(sttState.value.language)
            recognizer.startListening(intent)
            _sttState.update {
                it.copy(
                    availableSTT = true,
                    isSpeaking = true,
                    offlineError = false
                )
            }
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
        recognizer.cancel()
        _sttState.update { it.copy(isSpeaking = false) }
    }

    fun clearTexts() {
        _sttState.update {
            it.copy(
                fullTranscripts = emptyList(),
                partialTranscripts = emptyList(),
                spokenPromptText = "",
            )
        }
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(TAG, "Ready for speech")
        _sttState.update { it.copy() }
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
        if (sttState.value.isSpeaking && isRecoverableError(error)) {
            Log.d(TAG, "Retrying recognition...")
            startListening()
        } else {
            _sttState.update { it.copy(isSpeaking = false) }
        }
    }

    private fun mapError(error: Int): String {
        Log.e(TAG, "error: $error")
        return when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK,
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                _sttState.update { it.copy(offlineError = true) }
                "Network error"
            }

            SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
            else -> {
                "Unknown error"
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
            if (sttState.value.aiClickedBeforeFinalResults && sttState.value.spokenPromptText.length > (sttState.value.fullTranscripts + it).joinToString(
                    " "
                ).length
            ) {
                fixSpokenPromptText() //if the final result is smaller than the Partial Result
            }
            Log.d(TAG, "Final result: $it")
            _sttState.update { state ->
                state.copy(
                    fullTranscripts = state.fullTranscripts + it,
                    partialTranscripts = emptyList(),
                    aiClickedBeforeFinalResults = false,
                )
            }
        }
        if (sttState.value.isSpeaking) {
            startListening() // Restart listening
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val partialText =
            partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
        partialText?.let { text ->
            val cleanedText = text.replace(",", "").trim()
            val previousTranscript = _sttState.value.partialTranscripts.joinToString(" ")

            // Extract only new portion of the text
            val newPortion = cleanedText.removePrefix(previousTranscript).trim()

            if (newPortion.isNotEmpty()) {
                Log.d(TAG, "Filtered partial result: $newPortion")
                _sttState.update { state ->
                    state.copy(
                        partialTranscripts = state.partialTranscripts + newPortion
                    )
                }
            }
        }
    }

    private fun enableNoiseReduction() {
        val audioSessionId = 0 // Default session (let Android decide)

        if (NoiseSuppressor.isAvailable()) {
            noiseSuppressor = NoiseSuppressor.create(audioSessionId)
            noiseSuppressor?.enabled = true
            Log.d(TAG, "NoiseSuppressor enabled")
        }

        if (AcousticEchoCanceler.isAvailable()) {
            echoCanceler = AcousticEchoCanceler.create(audioSessionId)
            echoCanceler?.enabled = true
            Log.d(TAG, "AcousticEchoCanceler enabled")
        }

        if (AutomaticGainControl.isAvailable()) {
            gainControl = AutomaticGainControl.create(audioSessionId)
            gainControl?.enabled = true
            Log.d(TAG, "AutomaticGainControl enabled")
        }
    }

    fun disableNoiseReduction() {
        noiseSuppressor?.let {
            it.enabled = false
            it.release()
        }
        echoCanceler?.let {
            it.enabled = false
            it.release()
        }
        gainControl?.let {
            it.enabled = false
            it.release()
        }

        noiseSuppressor = null
        echoCanceler = null
        gainControl = null

        Log.d(TAG, "Noise reduction disabled and resources released")
    }


    fun changeLanguage(numberInList: Int) {
        _sttState.update { state ->
            state.copy(
                language = supportedSpeechRecognitionLanguages[numberInList],
            )
        }
    }

    fun changeCanRunAgain(newValue: Boolean) {
        _sttState.update { state ->
            state.copy(
                canRunAgain = newValue,
            )
        }
    }

    fun changeSpokenPromptText() {
        _sttState.update { state ->
            state.copy(
                aiClickedBeforeFinalResults = true,
                spokenPromptText = (state.fullTranscripts + state.partialTranscripts)
                    .joinToString(" ")
                    .replaceFirst("\" ", "\"") // Converts list to a string with spaces
                    .replace(",", "") // Removes commas
                    .replace(Regex("\\s+"), " ") // Replaces multiple spaces with a single space
                    .trim() // Ensures no leading/trailing spaces
            )
        }
        Log.d(TAG, sttState.value.spokenPromptText)
    }

    fun fixSpokenPromptText() {
        _sttState.update { state ->
            state.copy(
                aiClickedBeforeFinalResults = true,
                spokenPromptText = (state.fullTranscripts)
                    .joinToString(" ")
                    .replaceFirst("\" ", "\"") // Converts list to a string with spaces
                    .replace(",", "") // Removes commas
                    .replace(Regex("\\s+"), " ") // Replaces multiple spaces with a single space
                    .trim() // Ensures no leading/trailing spaces
            )
        }
        Log.d(TAG, sttState.value.spokenPromptText)
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        // Not used
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "Clearing resources")
        disableNoiseReduction()
        destroyRecognizer()
        noiseSuppressor?.release()
        echoCanceler?.release()
        gainControl?.release()
    }

    fun destroyRecognizer() {
        recognizer.cancel()
        recognizer.stopListening()
        recognizer.destroy()
    }


    companion object {
        private const val TAG = "VoiceToTextViewModel"
    }
}