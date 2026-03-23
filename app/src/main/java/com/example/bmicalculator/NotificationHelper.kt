package com.example.bmicalculator

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Helper that posts a local Android notification when the kid earns a reward.
 *
 * Sequence diagram role: "Notification Service" – called by [RewardsManager]
 * after a reward is successfully persisted, delivering the reward alert to
 * the kid (and optionally to the parent via a separate channel).
 *
 * Note: On Android 13+ (API 33) the caller must hold the
 * POST_NOTIFICATIONS runtime permission before calling
 * [sendRewardNotification].  The permission is declared in
 * AndroidManifest.xml; the request dialog is shown in [RewardsActivity].
 */
class NotificationHelper(private val context: Context) {

    init {
        createNotificationChannels()
    }

    /** Notify the kid that they earned a new reward. */
    fun sendRewardNotification(reward: Reward) {
        val notification = NotificationCompat.Builder(context, CHANNEL_KID)
            .setSmallIcon(android.R.drawable.star_on)
            .setContentTitle("You earned a reward! ${reward.title}")
            .setContentText(reward.description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(reward.description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId(), notification)
        }
    }

    /** Notify the parent that the kid earned a new reward. */
    fun sendParentNotification(reward: Reward) {
        val notification = NotificationCompat.Builder(context, CHANNEL_PARENT)
            .setSmallIcon(android.R.drawable.star_on)
            .setContentTitle("Your child earned a reward!")
            .setContentText("${reward.title} – ${reward.points} points")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId(), notification)
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_KID,
                    "Kid Rewards",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Reward notifications for the child" }
            )

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_PARENT,
                    "Parent Alerts",
                    NotificationManager.IMPORTANCE_LOW
                ).apply { description = "Parent notifications about child rewards" }
            )
        }
    }

    companion object {
        const val CHANNEL_KID = "channel_kid_rewards"
        const val CHANNEL_PARENT = "channel_parent_alerts"

        private var nextId = 1000
        private fun notificationId(): Int = nextId++
    }
}
