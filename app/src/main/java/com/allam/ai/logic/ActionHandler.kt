package com.allam.ai.logic

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.util.Log

class ActionHandler(private val context: Context) {

    fun handleCommand(text: String): Pair<Boolean, String?> {
        val command = text.lowercase()

        return when {
            command.contains("كام الساعه") || command.contains("الساعة") || command.contains("الوقت") || command.contains("time") -> {
                val time = java.text.SimpleDateFormat("h:mm a", java.util.Locale("ar", "EG")).format(java.util.Date())
                val response = "الساعة دلوقتي $time يا باشا"
                Pair(true, response)
            }
            command.contains("افتح الكاميرا") || command.contains("صور") || command.contains("camera") -> {
                openCamera()
                Pair(true, "عيني، فتحتلك الكاميرا")
            }
            command.contains("شغل أغنية") || command.contains("شغل مزيكا") || command.contains("music") -> {
                playMusic()
                Pair(true, "من عينيا، هشغلك أغنية دلوقتي")
            }
            command.contains("افتح") || command.contains("open") -> {
                openApp(command)
                Pair(true, "جاري فتح التطبيق")
            }
            command.contains("اتصل") || command.contains("call") -> {
                makeCall(command)
                Pair(true, "جاري الإتصال")
            }
            command.contains("رسالة") || command.contains("message") || command.contains("واتساب") -> {
                sendWhatsApp(command)
                Pair(true, "تمام، هبعت الرسالة")
            }
            command.contains("منبه") || command.contains("alarm") -> {
                setAlarm(command)
                Pair(true, "ظبطت المنبه يا ريس")
            }
            else -> Pair(false, null) // Not a system command, send to AI
        }
    }

    private fun openCamera() {
        val intent = Intent(android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun playMusic() {
        try {
            val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("ActionHandler", "Failed to open music player: ${e.message}")
        }
    }

    private fun openApp(text: String) {
        val appName = text.replace("افتح", "").replace("open", "").trim()
        val intent = context.packageManager.getLaunchIntentForPackage(appName) // Simplification
        // Real implementation would search installed apps names
        try {
            val pm = context.packageManager
            val apps = pm.getInstalledApplications(0)
            for (app in apps) {
                val label = pm.getApplicationLabel(app).toString().lowercase()
                if (label.contains(appName)) {
                    context.startActivity(pm.getLaunchIntentForPackage(app.packageName))
                    return
                }
            }
        } catch (e: Exception) {
            Log.e("ActionHandler", "Failed to open app: $appName")
        }
    }

    private fun makeCall(text: String) {
        // Simple extraction logic - needs more robust NLP or Regex
        val number = text.filter { it.isDigit() }
        if (number.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    private fun sendWhatsApp(text: String) {
        // Logic to open WhatsApp with a contact or message
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode("Hello from Allam")}")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun setAlarm(text: String) {
        // Placeholder for alarm logic
        val intent = Intent(AlarmClock.ACTION_SET_ALARM)
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Allam Alarm")
        intent.putExtra(AlarmClock.EXTRA_HOUR, 8)
        intent.putExtra(AlarmClock.EXTRA_MINUTES, 30)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
