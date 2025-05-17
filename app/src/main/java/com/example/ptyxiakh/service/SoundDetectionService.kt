package com.example.ptyxiakh.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.domain.models.usecases.ServiceStateUseCases
import com.example.ptyxiakh.R
import com.example.ptyxiakh.utils.SoundDetectionProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class SoundDetectionService : LifecycleService() {

    @Inject
    lateinit var serviceStateUseCases: ServiceStateUseCases

    companion object {
        private const val CHANNEL_ID = "SoundDetectionServiceChannel"
        private const val NOTIFICATION_ID = 1
        private const val ACTION_STOP_SERVICE = "com.example.ptyxiakh.STOP_SERVICE"
    }


    private val isListening = AtomicBoolean(false)
    private var audioRecord: AudioRecord? = null
    private var interpreter: Interpreter? = null
    private var alertingSounds: List<Int> =
        listOf(20, 317, 318, 319, 390, 391, 353, 340, 195, 349, 348, 394, 292)

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun onCreate() {
        super.onCreate()
        serviceStateUseCases.setServiceState(true)
        createNotificationChannel()
        startForegroundService()

        lifecycleScope.launch(Dispatchers.IO) {
            interpreter = Interpreter(loadModelFile())
        }
        startListening()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent?.action == ACTION_STOP_SERVICE) {
            stopForegroundService()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val name = "Sound Detection Service"
            val descriptionText = "Notification for sound detection"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun triggerAlarmNotification(soundDetected: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sound Detected!")
            .setContentText("Detected sound: $soundDetected")
            .setSmallIcon(R.drawable.noise_aware_24px)
            .setPriority(NotificationCompat.PRIORITY_MAX) // Highest priority
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setSound(Settings.System.DEFAULT_ALARM_ALERT_URI)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID + 2, notification)
    }

    private fun startForegroundService() {
        val stopIntent = Intent(this, SoundDetectionService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sound Detection")
            .setContentText("Listening for important sounds...")
            .setSmallIcon(R.drawable.noise_aware_24px)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .addAction(R.drawable.stop_24px, "Stop", stopPendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }


    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startListening() {
        val sampleRate = 16000
        val inputSize = 15600

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord?.startRecording()

        isListening.set(true)

        val accumulatedSamples = mutableListOf<Float>()

        lifecycleScope.launch(Dispatchers.IO) {
            val buffer = ShortArray(bufferSize)
            while (isListening.get()) {
                val readSize = audioRecord?.read(buffer, 0, bufferSize) ?: break
                if (readSize > 0) {
                    accumulatedSamples.addAll(
                        buffer.take(readSize).map { it.toFloat() / Short.MAX_VALUE })
                    while (accumulatedSamples.size >= inputSize) {
                        val floatBuffer = FloatArray(inputSize)
                        for (i in 0 until inputSize) {
                            floatBuffer[i] = accumulatedSamples.removeAt(0)
                        }
                        val primary = classifySound(floatBuffer)
                        Log.d("background detected", SoundDetectionProvider.getSoundDetected(primary))
                        withContext(Dispatchers.Main) {
                            if (alertingSounds.contains(primary)) {
                                triggerAlarmNotification(SoundDetectionProvider.getSoundDetected(primary))

                                if (vibrator.hasVibrator()) {
                                    vibrator.vibrate(
                                        VibrationEffect.createOneShot(
                                            3000,
                                            VibrationEffect.DEFAULT_AMPLITUDE
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun classifySound(audioData: FloatArray): Int {
        val input = ByteBuffer.allocateDirect(audioData.size * 4).order(ByteOrder.nativeOrder())
        input.asFloatBuffer().put(audioData)

        val output = Array(1) { FloatArray(521) }
        interpreter?.run(input, output)

        val sortedIndices = output[0].indices.sortedByDescending { output[0][it] }
        val primaryLabelIndex = sortedIndices.getOrNull(0) ?: -1

        return primaryLabelIndex
    }

    private fun loadModelFile(): ByteBuffer {
        assets.openFd("yamnet.tflite").use { assetFileDescriptor ->
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


    override fun onDestroy() {
        super.onDestroy()
        stopListening()

        audioRecord?.apply {
            stop()
            release()
        }
        audioRecord = null
        interpreter?.close()
        interpreter = null
    }


    private fun stopListening() {
        serviceStateUseCases.setServiceState(false)
        isListening.set(false)
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        interpreter = null
    }

    private fun stopForegroundService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopListening()
        stopSelf()
    }

}
