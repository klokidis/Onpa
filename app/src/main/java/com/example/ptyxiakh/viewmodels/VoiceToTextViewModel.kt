package com.example.ptyxiakh.viewmodels

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
    val aiClicked: Boolean = false,
    val language: String = "en",
    val spokenPromptText: String = "", // text that sends to ai (removes the already used text)
    val isSttInitialized: Boolean = false
)

@HiltViewModel
class VoiceToTextViewModel @Inject constructor(
    application: Application,
    // Inject any other dependencies here if needed (e.g., a repository or a service)
) : AndroidViewModel(application), RecognitionListener {

    private val _sttState = MutableStateFlow(VoiceToTextState())
    val sttState: StateFlow<VoiceToTextState> = _sttState.asStateFlow()
    private val supportedSpeechRecognitionLanguages = listOf(//all the available languages of the google api
        "en-US", // English (US)
        "en-AU", // English (Australia)
        "en-CA", // English (Canada)
        "en-GH", // English (Ghana)
        "en-IN", // English (India)
        "en-IE", // English (Ireland)
        "en-KE", // English (Kenya)
        "en-NZ", // English (New Zealand)
        "en-NG", // English (Nigeria)
        "en-PH", // English (Philippines)
        "en-SG", // English (Singapore)
        "en-ZA", // English (South Africa)
        "en-TZ", // English (Tanzania)
        "en-GB", // English (UK)
        "af-ZA", // Afrikaans
        "am-ET", // Amharic
        "ar-DZ", // Arabic (Algeria)
        "ar-BH", // Arabic (Bahrain)
        "ar-EG", // Arabic (Egypt)
        "ar-IQ", // Arabic (Iraq)
        "ar-IL", // Arabic (Israel)
        "ar-JO", // Arabic (Jordan)
        "ar-KW", // Arabic (Kuwait)
        "ar-LB", // Arabic (Lebanon)
        "ar-MA", // Arabic (Morocco)
        "ar-OM", // Arabic (Oman)
        "ar-QA", // Arabic (Qatar)
        "ar-SA", // Arabic (Saudi Arabia)
        "ar-PS", // Arabic (Palestine)
        "ar-SY", // Arabic (Syria)
        "ar-TN", // Arabic (Tunisia)
        "ar-AE", // Arabic (UAE)
        "ar-YE", // Arabic (Yemen)
        "hy-AM", // Armenian
        "az-AZ", // Azerbaijani
        "eu-ES", // Basque
        "bn-BD", // Bengali (Bangladesh)
        "bn-IN", // Bengali (India)
        "bs-BA", // Bosnian
        "bg-BG", // Bulgarian
        "my-MM", // Burmese
        "ca-ES", // Catalan
        "zh-HK", // Chinese (Hong Kong)
        "zh-CN", // Chinese (Mandarin)
        "zh-TW", // Chinese (Taiwanese)
        "hr-HR", // Croatian
        "cs-CZ", // Czech
        "da-DK", // Danish
        "nl-BE", // Dutch (Belgium)
        "nl-NL", // Dutch (Netherlands)
        "et-EE", // Estonian
        "fil-PH", // Filipino
        "fi-FI", // Finnish
        "fr-BE", // French (Belgium)
        "fr-CA", // French (Canada)
        "fr-FR", // French (France)
        "fr-CH", // French (Switzerland)
        "gl-ES", // Galician
        "ka-GE", // Georgian
        "de-AT", // German (Austria)
        "de-DE", // German (Germany)
        "de-CH", // German (Switzerland)
        "el-GR", // Greek
        "gu-IN", // Gujarati
        "ha-NG", // Hausa
        "he-IL", // Hebrew
        "hi-IN", // Hindi
        "hu-HU", // Hungarian
        "is-IS", // Icelandic
        "ig-NG", // Igbo
        "id-ID", // Indonesian
        "ga-IE", // Irish
        "it-IT", // Italian (Italy)
        "it-CH", // Italian (Switzerland)
        "ja-JP", // Japanese
        "jv-ID", // Javanese
        "kn-IN", // Kannada
        "kk-KZ", // Kazakh
        "km-KH", // Khmer
        "rw-RW", // Kinyarwanda
        "ko-KR", // Korean
        "lo-LA", // Lao
        "lv-LV", // Latvian
        "lt-LT", // Lithuanian
        "lb-LU", // Luxembourgish
        "mk-MK", // Macedonian
        "ms-MY", // Malay (Malaysia)
        "ml-IN", // Malayalam
        "mt-MT", // Maltese
        "mi-NZ", // Māori
        "mr-IN", // Marathi
        "mn-MN", // Mongolian
        "ne-NP", // Nepali
        "nb-NO", // Norwegian Bokmål
        "nn-NO", // Norwegian Nynorsk
        "ps-AF", // Pashto
        "fa-IR", // Persian
        "pl-PL", // Polish
        "pt-BR", // Portuguese (Brazil)
        "pt-PT", // Portuguese (Portugal)
        "pa-IN", // Punjabi (India)
        "pa-PK", // Punjabi (Pakistan)
        "ro-RO", // Romanian
        "ru-RU", // Russian
        "sr-RS", // Serbian
        "si-LK", // Sinhala
        "sk-SK", // Slovak
        "sl-SI", // Slovenian
        "so-SO", // Somali
        "st-ZA", // Southern Sotho
        "es-AR", // Spanish (Argentina)
        "es-BO", // Spanish (Bolivia)
        "es-CL", // Spanish (Chile)
        "es-CO", // Spanish (Colombia)
        "es-CR", // Spanish (Costa Rica)
        "es-DO", // Spanish (Dominican Republic)
        "es-EC", // Spanish (Ecuador)
        "es-SV", // Spanish (El Salvador)
        "es-GQ", // Spanish (Equatorial Guinea)
        "es-GT", // Spanish (Guatemala)
        "es-HN", // Spanish (Honduras)
        "es-MX", // Spanish (Mexico)
        "es-NI", // Spanish (Nicaragua)
        "es-PA", // Spanish (Panama)
        "es-PY", // Spanish (Paraguay)
        "es-PE", // Spanish (Peru)
        "es-PR", // Spanish (Puerto Rico)
        "es-ES", // Spanish (Spain)
        "es-US", // Spanish (US)
        "es-UY", // Spanish (Uruguay)
        "es-VE", // Spanish (Venezuela)
        "sw-KE", // Swahili (Kenya)
        "sw-TZ", // Swahili (Tanzania)
        "sv-SE", // Swedish
        "ta-IN", // Tamil (India)
        "ta-MY", // Tamil (Malaysia)
        "ta-SG", // Tamil (Singapore)
        "ta-LK", // Tamil (Sri Lanka)
        "te-IN", // Telugu
        "th-TH", // Thai
        "tr-TR", // Turkish
        "uk-UA", // Ukrainian
        "ur-IN", // Urdu (India)
        "ur-PK", // Urdu (Pakistan)
        "uz-UZ", // Uzbek
        "vi-VN", // Vietnamese
        "cy-GB", // Welsh
        "xh-ZA", // Xhosa
        "yo-NG", // Yoruba
        "zu-ZA"  // Zulu
    )

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
                _sttState.update { it.copy(offlineError = true) }
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
            if (sttState.value.aiClicked && sttState.value.spokenPromptText.length > (sttState.value.fullTranscripts + it).joinToString(
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
                    aiClicked = false,
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
                aiClicked = true,
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
                aiClicked = true,
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