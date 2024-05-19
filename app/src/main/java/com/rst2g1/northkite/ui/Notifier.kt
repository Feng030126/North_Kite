package com.rst2g1.northkite.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rst2g1.northkite.R

object Notifier {

    private const val channelId = "northKite"
    private const val channelName = "North Kite Channel"
    private const val channelDescription = "This is my channel for notifications"

    fun createChannel(context: Context) {
        Log.d("NOTI", "CHANNEL CREATED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = channelDescription
            }

            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(context: Context, title: String, message: String) {

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_logo_app) // Set your notification icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(1, builder.build())
        }
    }
}

data class Notification(

    var id: String = "", // Unique identifier for the notification
    val userID: String = "",
    val title: String = "",
    val message: String = ""

)

object NotificationID{

    private var currentId = 0

    fun generateUniqueId(): String {
        // Generate a unique ID, e.g., using timestamp or UUID
        return "notification_${currentId++}"
    }

}
