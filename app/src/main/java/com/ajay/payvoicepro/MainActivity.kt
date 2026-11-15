package com.ajay.payvoicepro

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {

    private val bg = Color.rgb(6, 15, 12)
    private val surface = Color.rgb(15, 29, 23)
    private val surface2 = Color.rgb(21, 39, 31)
    private val green = Color.rgb(39, 230, 137)
    private val white = Color.rgb(241, 247, 243)
    private val muted = Color.rgb(151, 171, 159)

    private lateinit var statusText: TextView
    private lateinit var accessButton: TextView

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

    override fun onResume() {
        super.onResume()
        if (::statusText.isInitialized) updateAccessStatus()
    }

    private fun isNotificationAccessEnabled(): Boolean {
        val component = ComponentName(
            this, PaymentNotificationListener::class.java
        )
        val enabled = Settings.Secure.getString(
            contentResolver, "enabled_notification_listeners"
        ) ?: return false

        return enabled.split(":").any {
            ComponentName.unflattenFromString(it) == component
        }
    }

    private fun updateAccessStatus() {
        val active = isNotificationAccessEnabled()
        statusText.text = if (active)
            "●  Notification access is active"
        else
            "●  Notification access is off"

        statusText.setTextColor(
            if (active) green else Color.rgb(255, 112, 112)
        )

        accessButton.text = if (active)
            "Notification access enabled  ✓"
        else
            "Enable notification access  →"

        accessButton.alpha = if (active) 0.72f else 1f
    }

    private fun showDashboard() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(20), dp(12), dp(20), dp(20))
        }

        root.setOnApplyWindowInsetsListener { view, insets ->
            view.setPadding(
                dp(20), dp(12) + insets.systemWindowInsetTop,
                dp(20), dp(20)
            )
            insets
        }

        val scroll = ScrollView(this).apply {
            clipToPadding = false
            isFillViewport = true
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, dp(18))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = rounded(surface, 22)
        }

        header.addView(text("PAYVOICE PRO", 25f, green, true))
        header.addView(text("YOUR PAYMENTS, YOUR VOICE", 11f, muted, true))
        content.addView(header, matchWrap())
        content.addView(space(18))

        content.addView(text("Dashboard", 22f, white, true))
        content.addView(text("Manage your payment announcements", 13f, muted))
        content.addView(space(14))

        val statusCard = card()
        statusCard.addView(text("SERVICE STATUS", 11f, muted, true))

        statusText = text("Checking notification access…", 15f, green, true)
        statusCard.addView(statusText)

        accessButton = actionTile("Enable notification access  →") {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        statusCard.addView(accessButton, matchWrap())
        content.addView(statusCard, matchWrap())
        content.addView(space(16))

        content.addView(sectionTitle("Your mode"))

        val mode = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf("Personal", "Shopkeeper", "Business")
            )
            setSelection(prefs.getInt("mode", 0).coerceIn(0, 2))
            backgroundTintList = ColorStateList.valueOf(green)

            onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        prefs.edit().putInt("mode", position).apply()
                    }
                }
        }

        val modeCard = card()
        modeCard.addView(mode, matchWrap())
        content.addView(modeCard, matchWrap())
        content.addView(space(16))

        content.addView(sectionTitle("Payment apps"))
        content.addView(
            text("Choose which app notifications PayVoice can read.", 12f, muted)
        )
        content.addView(space(6))

        val appsCard = card()

        apps.forEach { (name, pkg) ->
            val check = CheckBox(this).apply {
                text = name
                textSize = 14f
                setTextColor(white)
                buttonTintList = ColorStateList.valueOf(green)
                isChecked = prefs.getBoolean("app_$pkg", true)
                setPadding(0, dp(3), 0, dp(3))

                setOnCheckedChangeListener { _, checked ->
                    prefs.edit().putBoolean("app_$pkg", checked).apply()
                }
            }
            appsCard.addView(check, matchWrap())
        }

        content.addView(appsCard, matchWrap())
        content.addView(space(16))

        content.addView(sectionTitle("Announcement language"))

        val languageCard = card()

        val language = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf("Hindi", "English")
            )
            setSelection(prefs.getInt("language", 0).coerceIn(0, 1))
            backgroundTintList = ColorStateList.valueOf(green)

            onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        prefs.edit().putInt("language", position).apply()
                    }
                }
        }

        languageCard.addView(language, matchWrap())
        content.addView(languageCard, matchWrap())
        content.addView(space(16))

        content.addView(sectionTitle("Quick actions"))

        content.addView(
            actionTile("▶   Test voice announcement") {
                PaymentNotificationListener.speakTest(this)
            },
            matchWrap()
        )

        content.addView(space(8))

        content.addView(
            actionTile("Privacy, terms & support  →") {
                PrivacySupport.showMenu(this)
            },
            matchWrap()
        )

        content.addView(space(20))

        val footer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(12), dp(16), dp(12), dp(12))
            background = rounded(surface, 18)
        }

        footer.addView(
            text("PAYVOICE PRO", 14f, green, true).apply {
                gravity = Gravity.CENTER
            }
        )

        footer.addView(
            text(
                "Version 1.0.0  •  Support: ajayyadav966171@gmail.com",
                11f, muted
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        content.addView(footer, matchWrap())

        scroll.addView(content)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )

        setContentView(root)
        updateAccessStatus()
    }

    private fun card(): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = rounded(surface, 18)
        }

    private fun sectionTitle(value: String): TextView =
        text(value, 17f, white, true).apply {
            setPadding(0, 0, 0, dp(8))
        }

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            typeface = if (bold)
                Typeface.create("sans-serif", Typeface.BOLD)
            else
                Typeface.create("sans-serif", Typeface.NORMAL)
            includeFontPadding = true
            setPadding(0, dp(3), 0, dp(3))
        }

    private fun actionTile(
        title: String,
        action: () -> Unit
    ): TextView =
        TextView(this).apply {
            text = title
            textSize = 14f
            setTextColor(
                if (title.startsWith("Notification access enabled"))
                    muted else green
            )
            typeface = Typeface.create(
                "sans-serif-medium", Typeface.NORMAL
            )
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(13), dp(14), dp(13))
            background = rounded(surface2, 13)
            isClickable = true
            isFocusable = true
            setOnClickListener { action() }
        }

    private fun rounded(color: Int, radius: Int) =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(1), Color.rgb(31, 55, 43))
        }

    private fun matchWrap() =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

    private fun space(height: Int) =
        View(this).apply {
            layoutParams = LinearLayout.LayoutParams(1, dp(height))
        }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
