package com.ajay.payvoicepro

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    private val green = Color.rgb(33, 233, 133)
    private val background = Color.rgb(7, 17, 13)
    private val card = Color.rgb(16, 28, 22)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = background
        window.navigationBarColor = background

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 25, 22, 20)
            setBackgroundColor(background)
        }

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        content.addView(text(
            "PAYVOICE PRO",
            27f, green, true
        ))

        content.addView(text(
            "Your Payments, Your Voice",
            14f, Color.LTGRAY
        ))

        content.addView(space(25))

        content.addView(text(
            "Welcome back 👋",
            22f, Color.WHITE, true
        ))

        content.addView(space(15))

        content.addView(createCard(
            "VOICE ANNOUNCEMENT",
            "Ready to configure your payment alerts"
        ))

        content.addView(space(15))

        content.addView(text(
            "Choose Your Mode",
            18f, Color.WHITE, true
        ))

        val mode = Spinner(this)

        val modes = arrayOf(
            "Personal Mode",
            "Shopkeeper Mode",
            "Business Mode"
        )

        mode.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            modes
        )

        content.addView(mode)
        content.addView(space(20))

        content.addView(text(
            "Quick Settings",
            18f, Color.WHITE, true
        ))

        content.addView(createButton("Payment Apps"))
        content.addView(createButton("Voice Settings"))
        content.addView(createButton("Payment History"))
        content.addView(createButton("Daily Reports"))
        content.addView(createButton("Privacy & Security"))
        content.addView(createButton("Help & Support"))

        content.addView(space(20))

        content.addView(text(
            "PayVoice Pro v1.0.0",
            12f, Color.GRAY
        ))

        content.addView(text(
            "Support: ajayyadav966171@gmail.com",
            12f, Color.GRAY
        ))

        scroll.addView(content)
        root.addView(scroll)

        setContentView(root)
    }

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) {
                typeface = Typeface.DEFAULT_BOLD
            }
            setPadding(0, 8, 0, 8)
        }
    }

    private fun createCard(
        heading: String,
        description: String
    ): LinearLayout {

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 18, 18, 18)
            setBackgroundColor(card)

            addView(text(
                heading, 16f, green, true
            ))

            addView(text(
                description, 14f, Color.WHITE
            ))
        }
    }

    private fun createButton(label: String): Button {
        return Button(this).apply {
            text = label
            setTextColor(Color.WHITE)
            setBackgroundColor(card)
            textSize = 14f
            isAllCaps = false

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                58
            ).apply {
                bottomMargin = 8
            }
        }
    }

    private fun space(height: Int): android.view.View {
        return android.view.View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                1, height
            )
        }
    }
}
