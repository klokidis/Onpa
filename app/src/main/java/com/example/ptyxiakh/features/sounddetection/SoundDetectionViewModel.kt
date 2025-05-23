package com.example.ptyxiakh.features.sounddetection

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptyxiakh.utils.AlertingSoundsProvider.Companion.alertingSounds
import com.example.ptyxiakh.utils.HapticUtils
import com.example.ptyxiakh.utils.SoundDetectionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject

// UI State to hold detected sound
data class SoundDetectionUiState(
    val detectedPrimarySound: String = "...",
    val detectedSecondarySound: String = "",
    val isListening: Boolean = false
)

@HiltViewModel
class SoundDetectionViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _soundDetectorState = MutableStateFlow(SoundDetectionUiState())
    val soundDetectorState: StateFlow<SoundDetectionUiState> = _soundDetectorState.asStateFlow()

    private var interpreter: Interpreter? = null
    private var audioRecord: AudioRecord? = null

    init {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                interpreter = Interpreter(loadModelFile())
            }
        } catch (e: IOException) {
            Log.e("SoundDetectionViewModel", "Failed to load model file", e)
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startListening(canVibrate: Boolean) {
        val sampleRate = 16000
        val inputSize = 15600

        viewModelScope.launch(Dispatchers.IO) {
            if (interpreter == null) {
                interpreter = Interpreter(loadModelFile())
            }

            val bufferSize = AudioRecord.getMinBufferSize(
                sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
            )

            if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                Log.e("Sound Detection", "Invalid buffer size: $bufferSize")
                return@launch
            }

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e("Sound Detection", "AudioRecord initialization failed")
                return@launch
            }

            audioRecord?.startRecording()
            _soundDetectorState.update { it.copy(isListening = true) }

            val accumulatedSamples = mutableListOf<Float>()
            val buffer = ShortArray(bufferSize)

            while (soundDetectorState.value.isListening) {
                val readSize = audioRecord?.read(buffer, 0, bufferSize) ?: break

                if (readSize > 0) {
                    // Normalize to [-1, 1] float32 range
                    accumulatedSamples.addAll(
                        buffer.take(readSize).map { it.toFloat() / Short.MAX_VALUE }
                    )
                    // Check if we have enough samples
                    while (accumulatedSamples.size >= inputSize) {
                        val floatBuffer = FloatArray(inputSize)
                        for (i in 0 until inputSize) {
                            floatBuffer[i] =
                                accumulatedSamples.removeAt(0) // Remove the first sample
                        }

                        // Run TensorFlow Lite inference
                        val (primaryNumber, secondaryNumber) = classifySound(floatBuffer)
                        val primaryString = SoundDetectionProvider.getSoundDetected(primaryNumber)
                        val secondaryString = SoundDetectionProvider.getSoundDetected(secondaryNumber)


                        withContext(Dispatchers.Main) {
                            if (alertingSounds.contains(primaryNumber)) {
                                HapticUtils.triggerVibration(
                                    canVibrate = canVibrate,
                                    context = context,
                                    milliseconds = 100
                                )
                            }
                            _soundDetectorState.update {
                                it.copy(
                                    detectedPrimarySound = primaryString,
                                    detectedSecondarySound = secondaryString
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun classifySound(audioData: FloatArray): Pair<Int, Int> {
        if (interpreter == null) {
            Log.e("Sound Detection", "Interpreter is null")
            return Pair(0, 0)
        }

        val output = Array(1) { FloatArray(521) }  // YAMNet outputs (N, 521)
        interpreter?.run(arrayOf(audioData), output)

        val sortedIndices = output[0].indices.sortedByDescending { output[0][it] }
        val primaryLabelIndex = sortedIndices.getOrNull(0) ?: -1
        val secondaryLabelIndex = sortedIndices.getOrNull(1) ?: -1

        Log.d("Sound Detection", "Primary: $primaryLabelIndex, Secondary: $secondaryLabelIndex")

        return Pair(
            primaryLabelIndex,
            secondaryLabelIndex
        )
    }

    private fun loadModelFile(): ByteBuffer {
        context.assets.openFd("yamnet.tflite").use { assetFileDescriptor ->
            FileInputStream(assetFileDescriptor.fileDescriptor).use { fileInputStream ->
                val fileChannel = fileInputStream.channel
                return fileChannel.map(
                    FileChannel.MapMode.READ_ONLY,
                    assetFileDescriptor.startOffset,
                    assetFileDescriptor.declaredLength
                )
            }
        }
    }


    fun stopListening() {
        _soundDetectorState.update {
            it.copy(isListening = false)
        }

        try {
            interpreter?.close()
            interpreter = null
        } catch (e: Exception) {
            Log.e("SoundDetectionViewModel", "Error closing interpreter", e)
        }

        try {
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        } catch (e: Exception) {
            Log.e("SoundDetectionViewModel", "Error stopping/releasing AudioRecord", e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }

}
