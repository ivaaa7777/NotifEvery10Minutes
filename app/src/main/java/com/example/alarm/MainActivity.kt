package com.example.alarm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 600000
    private val CHANNEL_ID = "IVAAA"
    private val notificationId = 100
    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sendBtn = findViewById<Button>(R.id.SendBtn)
        val CloseBtn = findViewById<Button>(R.id.CloseBtn)
        sendBtn.setOnClickListener{
            createNotificationChannel()
            sendSnoozeClickNotification()
            fun onResume() {
                handler.postDelayed(Runnable {
                    handler.postDelayed(runnable!!, delay.toLong())
                    sendSnoozeClickNotification()

                }.also { runnable = it }, delay.toLong())
                super.onResume()
            }
            onResume()
        }
        CloseBtn.setOnClickListener{
            fun onPause() {
                super.onPause()
                handler.removeCallbacks(runnable!!)
            }
            onPause()
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        }


    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm"
            val descriptionText = "Alarm Every 10 Minutes"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendSnoozeClickNotification(){
        val snoozeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra(Notification.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val snoozePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, snoozeIntent, 0)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(androidx.coordinatorlayout.R.drawable.notification_bg)
            .setContentTitle("Hello Still Dear")
            .setContentText("Notification Will be Sent Every 10 Minutes")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                androidx.vectordrawable.animated.R.drawable.notification_icon_background, "Disable",
                snoozePendingIntent)
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }
}