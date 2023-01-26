package com.example.androidservice

import android.app.*
import android.content.Context
import android.content.Intent

import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MyForeGroundService : Service() {

    private val NOTIFICATION_CHANNEL_ID = "Services Notification"
    private val ONGOING_NOTIFICATION_ID = 1

    private val ACTION_STOP_LISTEN = "action_stop_listen"

    private lateinit var broadCastPendingIntent: PendingIntent
    private lateinit var notificationPendingIntent: PendingIntent


    override fun onBind(intent: Intent): IBinder {
        TODO()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if ((intent!!.action != null) && (intent.action == ACTION_STOP_LISTEN)) {
            stopForeground(true)
            stopSelf()
        } else {
            createNotificationChannel()
            createPendingIntents()
            startForeground(ONGOING_NOTIFICATION_ID, createNotification())
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Notif"
            val descriptionText = "Notif Descript"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createPendingIntents() {
        // If the notification supports a direct reply action, use
// PendingIntent.FLAG_MUTABLE instead.
        notificationPendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
        broadCastPendingIntent =
            Intent(this, MyBroadCastReceiver::class.java).putExtra(
                "Action",
                ACTION_STOP_LISTEN
            ).let { stopForeGroundIntent ->
                PendingIntent.getBroadcast(
                    this, 0, stopForeGroundIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
    }

    private fun createNotificationAction(): NotificationCompat.Action {
        return NotificationCompat.Action(
            R.drawable.ic_launcher_background,
            "Stop",
            broadCastPendingIntent
        )
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("ringtone is playing")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(notificationPendingIntent)
            .setTicker("Music Playing")
            .setAutoCancel(true)
            .addAction(createNotificationAction())
            .build()
    }
}