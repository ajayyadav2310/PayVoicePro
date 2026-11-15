package com.ajay.payvoicepro

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import java.util.Locale

class PaymentNotificationListener : NotificationListenerService() {

    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private val seenNotifications = LinkedHashSet<String>()

    private val supportedApps = setOf(
        "com.phonepe.app",
        "com.google.android.apps.nbu.paisa.user",
        "net.one97.paytm",
        "in.org.npci.upiapp",
        "in.amazon.mShop.android.shopping",
        "com.dreamplug.androidapp",
        "money.super"
    )

    override fun onCreate() {
        super.onCreate()

        tts = TextToSpeech(this) { status ->
            ttsReady = status == TextToSpeech.SUCCESS

            if (ttsReady) {
                setVoiceLanguage()
            }
        }
    }

    override fun onNotificationPosted(
        sbn: StatusBarNotification
    ) {
        val packageName = sbn.packageName

        if (packageName !in supportedApps) return

        val prefs = getSharedPreferences(
            "payvoice",
            MODE_PRIVATE
        )

        if (!prefs.getBoolean("app_$packageName", true)) {
            return
        }

        val extras = sbn.notification.extras

        val title = extras
            .getCharSequence("android.title")
            ?.toString().orEmpty()

        val message = extras
            .getCharSequence("android.text")
            ?.toString().orEmpty()

        val fullText = "$title $message".trim()

        if (fullText.isBlank()) return

        val lower = fullText.lowercase(Locale.ROOT)

        val keywords = listOf(
            "received",
            "credited",
            "payment",
            "paid",
            "₹",
            "inr",
            "rs."
        )

        if (keywords.none { lower.contains(it) }) {
            return
        }

        val uniqueKey =
            "${sbn.key}:${sbn.postTime}:$fullText"

        if (!seenNotifications.add(uniqueKey)) {
            return
        }

        if (seenNotifications.size > 200) {
            seenNotifications.remove(
                seenNotifications.first()
            )
        }

        val amount = Regex(
            "(?:₹|rs\\.?|inr)\\s?([0-9,]+(?:\\.\\d{1,2})?)",
            RegexOption.IGNORE_CASE
        ).find(fullText)?.groupValues?.get(1)

        val isFailed = listOf(
            "failed",
            "declined",
            "unsuccessful"
        ).any { lower.contains(it) }

        val announcement = when {
            isFailed -> "Payment failed notification received"

            amount != null ->
                "Payment notification. Amount $amount rupees"

            else -> "Payment notification received"
        }

        if (ttsReady) {
            tts?.speak(
                announcement,
                TextToSpeech.QUEUE_ADD,
                null,
                uniqueKey
            )
        }
    }

    private fun setVoiceLanguage() {
        val prefs = getSharedPreferences(
            "payvoice",
            MODE_PRIVATE
        )

        val locale = if (
            prefs.getInt("language", 0) == 0
        ) {
            Locale("hi", "IN")
        } else {
            Locale.US
        }

        tts?.language = locale
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroy()
    }
}
