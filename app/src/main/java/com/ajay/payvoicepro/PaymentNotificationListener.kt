package com.ajay.payvoicepro

import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
                configureVoice()
            }
        }
    }

    private fun configureVoice() {

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

        val body = extras
            .getCharSequence("android.text")
            ?.toString().orEmpty()

        val bigText = extras
            .getCharSequence("android.bigText")
            ?.toString().orEmpty()

        val fullText = "$title $body $bigText".trim()

        if (fullText.isBlank()) return

        val lower = fullText.lowercase(Locale.ROOT)

        val paymentKeywords = listOf(
            "received",
            "credited",
            "payment",
            "paid",
            "₹",
            "inr",
            "rs.",
            "failed",
            "declined",
            "unsuccessful"
        )

        if (paymentKeywords.none { lower.contains(it) }) {
            return
        }

        val uniqueKey =
            "${sbn.key}:${sbn.postTime}"

        if (!seenNotifications.add(uniqueKey)) {
            return
        }

        if (seenNotifications.size > 200) {
            seenNotifications.remove(
                seenNotifications.first()
            )
        }

        val isFailed = listOf(
            "failed",
            "declined",
            "unsuccessful"
        ).any { lower.contains(it) }

        val amount = Regex(
            "(?:₹|rs\\.?|inr)\\s?([0-9,]+(?:\\.\\d{1,2})?)",
            RegexOption.IGNORE_CASE
        ).find(fullText)?.groupValues?.get(1)

        val announcement = when {

            isFailed ->
                "Payment failed notification received"

            amount != null ->
                "Payment notification. Amount $amount rupees"

            else ->
                "Payment notification received"
        }

        if (ttsReady) {

            configureVoice()

            tts?.speak(
                announcement,
                TextToSpeech.QUEUE_ADD,
                null,
                uniqueKey
            )
        }
    }

    override fun onDestroy() {

        tts?.stop()
        tts?.shutdown()

        tts = null
        ttsReady = false

        super.onDestroy()
    }

    companion object {

        fun speakTest(context: Context) {

            lateinit var testEngine: TextToSpeech

            testEngine = TextToSpeech(
                context.applicationContext
            ) { status ->

                if (status == TextToSpeech.SUCCESS) {

                    val prefs = context.getSharedPreferences(
                        "payvoice",
                        Context.MODE_PRIVATE
                    )

                    val locale = if (
                        prefs.getInt("language", 0) == 0
                    ) {
                        Locale("hi", "IN")
                    } else {
                        Locale.US
                    }

                    testEngine.language = locale

                    testEngine.setOnUtteranceProgressListener(
                        object : UtteranceProgressListener() {

                            override fun onStart(id: String?) {}

                            override fun onDone(id: String?) {
                                testEngine.shutdown()
                            }

                            @Deprecated("Deprecated in Java")
                            override fun onError(id: String?) {
                                testEngine.shutdown()
                            }
                        }
                    )

                    val message = if (
                        prefs.getInt("language", 0) == 0
                    ) {
                        "पे वॉइस प्रो तैयार है"
                    } else {
                        "PayVoice Pro is ready"
                    }

                    testEngine.speak(
                        message,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "payvoice_test"
                    )

                } else {
                    testEngine.shutdown()
                }
            }
        }
    }
}
