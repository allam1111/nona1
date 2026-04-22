package com.allam.ai.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ai.picovoice.porcupine.*
import android.util.Log
import com.allam.ai.ui.MainActivity
import com.allam.ai.logic.SpeechManager
import com.allam.ai.logic.TtsManager
import com.allam.ai.logic.ActionHandler
import com.allam.ai.data.AiClient
import kotlinx.coroutines.*

class VoiceAssistantService : Service() {

    private val TAG = "AllamService"
    private var porcupineManager: PorcupineManager? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    // Update with your Picovoice AccessKey
    private val ACCESS_KEY = "YOUR_PICOVOICE_ACCESS_KEY"

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        initPorcupine()
    }

    private fun startForegroundService() {
        val channelId = "allam_channel"
        val channelName = "Allam Assistant Service"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Allam is listening")
            .setContentText("Say 'Ya Allam' to activate")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()

        startForeground(1, notification)
    }

    private fun initPorcupine() {
        try {
            // Note: In a real project, you'd use a custom .ppn file for "Allam"
            // For now, using the default 'Porcupine' wake word as a placeholder
            porcupineManager = PorcupineManager.Builder()
                .setAccessKey(ACCESS_KEY)
                .setKeyword(BuiltInKeyword.PORCUPINE)
                .setSensitivity(0.7f)
                .build(applicationContext) { keywordIndex ->
                    if (keywordIndex == 0) {
                        Log.d(TAG, "Wake word detected!")
                        onWakeWordDetected()
                    }
                }
            
            porcupineManager?.start()
        } catch (e: Exception) {
            Log.e(TAG, "Porcupine init failed: ${e.message}")
        }
    }

    private fun onWakeWordDetected() {
        // 1. Play sound or vibrate to acknowledge
        // 2. Start Speech-to-Text via SpeechManager
        // 3. Stop hotword detection while listening to command
        stopHotword()
        
        // This would interact with the UI/MainActivity to show the listening wave
        val intent = Intent("com.allam.ai.WAKE_UP")
        sendBroadcast(intent)
    }

    private fun stopHotword() {
        porcupineManager?.stop()
    }

    fun startHotword() {
        porcupineManager?.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        porcupineManager?.delete()
        scope.cancel()
    }
}
