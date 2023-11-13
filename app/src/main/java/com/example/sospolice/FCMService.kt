package com.example.sospolice

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            val latitude = remoteMessage.data["latitude"]
            val longitude = remoteMessage.data["longitude"]
            showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body, latitude, longitude)
        }
        // Check if the message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // TODO: Send the FCM token to your server
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification(title: String?, message: String?,latitude:String?,longitude:String?) {

        createNotificationChannel()
        val title1 = "SOS Location"
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($title1)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        val pendingIntent = getActivity(
            this,
            0,
            mapIntent,
            FLAG_UPDATE_CURRENT
        )
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.image)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0,1000))

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Display the notification
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "This channel is for creating sos notifications"
            channel.enableLights(true)
            channel.lightColor = Color.RED
            val notificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }


    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "SOS-NotificationChannel"
        private const val CHANNEL_NAME = "SOS Notification Channel"
        private const val NOTIFICATION_ID = 1234
    }
}