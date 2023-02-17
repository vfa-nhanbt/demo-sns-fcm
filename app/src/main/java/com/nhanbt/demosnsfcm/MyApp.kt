package com.nhanbt.demosnsfcm
import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

const val channelId = "com.nhanbt.demosnsfcm.channel.id"
const val channelName = "com.nhanbt.demosnsfcm"

class MyApp : Application() {
    companion object {
        private lateinit var app: MyApp

        fun getAppContext(): Context {
            return app.applicationContext
        }
    }


    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate() {
        super.onCreate()
        app = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
