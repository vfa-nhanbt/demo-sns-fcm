package com.nhanbt.demosnsfcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

const val TAG = "onMessageReceived"

class FirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM messages here.
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        } else {
            Log.d(TAG, "Message data is empty!")
        }
        val gson = Gson()
        Log.d("SNS_RESPONSE", "Remote message data: ${remoteMessage.data["default"]}")
        val data = gson.fromJson(remoteMessage.data["default"], Data::class.java)
        Log.d("SNS_RESPONSE", "Response: $data")

        createNotification(data!!.eventType!!, data!!.message!!)
    }

    private fun createNotification(title: String, description: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val urlString =
            "https://stackoverflow.com/questions/2201917/how-can-i-open-a-url-in-androids-web-browser-from-my-application"
        val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
        val pendingIntent =
            PendingIntent.getActivity(MyApp.getAppContext(), 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification =
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setVibrate(
                    longArrayOf(1000, 1000, 1000, 1000)
                )
                .setOnlyAlertOnce(true)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(description)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(1, notification)
    }
}
