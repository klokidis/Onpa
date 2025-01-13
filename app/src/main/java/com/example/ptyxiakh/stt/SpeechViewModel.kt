package com.example.ptyxiakh.stt
import android.app.Application
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.Locale

class SpeechViewModel(application: Application) : AndroidViewModel(application) {
    val recognizedText = mutableStateOf("")
    val isListening = mutableStateOf(false)

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(application)
    private val tts: TextToSpeech by lazy {
        TextToSpeech(getApplication()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.US // Set the language after successful initialization.
            }
        }
    }



    init {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
                recognizedText.value = text ?: ""
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        val intent = RecognizerIntent.getVoiceDetailsIntent(getApplication())
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
        speechRecognizer.startListening(intent)
        isListening.value = true
    }

    fun stopListening() {
        speechRecognizer.stopListening()
        isListening.value = false
    }

    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer.destroy()
        tts.shutdown()
    }
}