package com.ajay.payvoicepro

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri

object PrivacySupport {

    private const val EMAIL =
        "ajayyadav966171@gmail.com"

    fun showMenu(activity: Activity) {

        val options = arrayOf(
            "Privacy Policy",
            "Terms & Conditions",
            "Contact Support",
            "About PayVoice Pro"
        )

        AlertDialog.Builder(activity)
            .setTitle("Help & Information")
            .setItems(options) { _, position ->

                when (position) {
                    0 -> showPrivacy(activity)
                    1 -> showTerms(activity)
                    2 -> contactSupport(activity)
                    3 -> showAbout(activity)
                }
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showPrivacy(activity: Activity) {

        val policy = """
            PAYVOICE PRO
            PRIVACY POLICY

            1. Notification Access

            PayVoice Pro reads notifications
            from apps selected by the user
            after explicit permission.

            2. Local Processing

            Notification text is processed
            on the device for voice announcements.

            3. Personal Information

            The app should not collect or
            upload notification contents
            to a PayVoice Pro server.

            4. Permissions

            Notification Access is optional.
            Users can revoke it from Android
            Settings at any time.

            5. Payment Disclaimer

            Announcements are based on
            notifications, not bank verification.
            Always verify payments in your
            official payment or bank app.

            6. Data Control

            Users can disable app selection,
            revoke permissions and uninstall
            the application.

            7. Contact

            $EMAIL

            IMPORTANT:
            This is an initial policy draft.
            A public HTTPS privacy policy URL
            must be published and verified
            before Play Store release.
        """.trimIndent()

        AlertDialog.Builder(activity)
            .setTitle("Privacy Policy")
            .setMessage(policy)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showTerms(activity: Activity) {

        val terms = """
            PAYVOICE PRO
            TERMS & CONDITIONS

            1. PayVoice Pro is a notification
            based voice announcement utility.

            2. It does not verify transactions
            with banks or UPI networks.

            3. Notifications may be delayed,
            missing, duplicated or incorrect.

            4. Never release goods or services
            solely on the basis of a voice
            announcement.

            5. Always verify payment in your
            official bank or payment application.

            6. Users are responsible for
            granting and managing permissions.

            7. Contact:
            $EMAIL
        """.trimIndent()

        AlertDialog.Builder(activity)
            .setTitle("Terms & Conditions")
            .setMessage(terms)
            .setPositiveButton("Accept & Close", null)
            .show()
    }

    private fun contactSupport(activity: Activity) {

        val intent = Intent(
            Intent.ACTION_SENDTO,
            Uri.parse("mailto:$EMAIL")
        )

        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            "PayVoice Pro Support"
        )

        try {
            activity.startActivity(intent)
        } catch (_: Exception) {

            AlertDialog.Builder(activity)
                .setMessage("Support Email: $EMAIL")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun showAbout(activity: Activity) {

        AlertDialog.Builder(activity)
            .setTitle("About PayVoice Pro")
            .setMessage(
                "PayVoice Pro\n\n" +
                "Version: 1.0.0\n\n" +
                "Your Payments, Your Voice.\n\n" +
                "Support: $EMAIL"
            )
            .setPositiveButton("OK", null)
            .show()
    }
}
