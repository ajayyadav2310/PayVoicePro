package com.ajay.payvoicepro

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    private val green = Color.rgb(33, 233, 133)
    private val bg = Color.rgb(7, 17, 13)
    private val card = Color.rgb(16, 28, 22)

    private val apps = listOf(
        "PhonePe" to "com.phonepe.app",
        "Google Pay" to "com.google.android.apps.nbu.paisa.user",
        "Paytm" to "net.one97.paytm",
        "BHIM UPI" to "in.org.npci.upiapp",
        "Amazon Pay" to "in.amazon.mShop.android.shopping",
        "CRED" to "com.dreamplug.androidapp",
        "super.money" to "money.super"
    )

    private val prefs by lazy {
        getSharedPreferences("payvoice", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        showDashboard()
    }

    private fun showDashboard() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 25, 22, 20)
            setBackgroundColor(bg)
        }

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        content.addView(label("PAYVOICE PRO", 28f, green, true))
        content.addView(label("Your Payments, Your Voice", 14f, Color.LTGRAY))

        content.addView(space(22))
        content.addView(label("Welcome back!", 22f, Color.WHITE, true))

        content.addView(space(15))
        content.addView(label("Select Mode", 17f, green, true))

        val mode = Spinner(this)
        mode.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Personal Mode", "Shopkeeper Mode", "Business Mode")
        )
        mode.setSelection(prefs.getInt("mode", 0))

        mode.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {}

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    prefs.edit().putInt("mode", position).apply()
                }
            }

        content.addView(mode)

        content.addView(space(20))
        content.addView(label("Payment App Selection", 19f, Color.WHITE, true))

        apps.forEach { (name, pkg) ->
            val check = CheckBox(this).apply {
                text = name
                textSize = 15f
                setTextColor(Color.WHITE)
                buttonTintList =
                    android.content.res.ColorStateList.valueOf(green)

                isChecked = prefs.getBoolean("app_$pkg", true)

                setOnCheckedChangeListener { _, checked ->
                    prefs.edit().putBoolean("app_$pkg", checked).apply()
                }
            }
            content.addView(check)
        }

        content.addView(space(15))

        content.addView(label("Voice Language", 18f, green, true))

        val language = Spinner(this)
        language.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Hindi", "English")
        )
        language.setSelection(prefs.getInt("language", 0))

        language.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {}

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    prefs.edit().putInt("language", position).apply()
                }
            }

        content.addView(language)

        content.addView(space(15))

        content.addView(actionButton("Enable Notification Access") {
            startActivity(
                Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            )
        })

        content.addView(actionButton("Test Voice") {
            PaymentNotificationListener.speakTest(this)
        })

        content.addView(actionButton("Privacy & Support") {
            AlertDialog.Builder(this)
                .setTitle("PayVoice Pro Support")
                .setMessage(
                    "Support: ajayyadav966171@gmail.com\n\n" +
                    "Announcements are based on notifications, " +
                    "not verified bank transactions."
                )
                .setPositiveButton("OK", null)
                .show()
        })

        content.addView(space(20))
        content.addView(label("PayVoice Pro v1.0.0", 12f, Color.GRAY))

        scroll.addView(content)
        root.addView(scroll)
        setContentView(root)
    }

    private fun label(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 7, 0, 7)
        }
    }

    private fun actionButton(
        title: String,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            text = title
            isAllCaps = false
            setTextColor(Color.WHITE)
            setBackgroundColor(card)
            setOnClickListener { action() }

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                58
            ).apply {
                bottomMargin = 10
            }
        }
    }

    private fun space(height: Int): android.view.View {
        return android.view.View(this).apply {
            layoutParams = LinearLayout.LayoutParams(1, height)
        }
    }
}
