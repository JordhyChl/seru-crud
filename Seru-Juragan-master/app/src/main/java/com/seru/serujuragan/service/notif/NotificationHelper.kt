package com.seru.serujuragan.service.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import com.seru.serujuragan.R
import com.seru.serujuragan.ui.home.HomeActivity

/**
 * Created by Mahendra Dev on 16/02/2020
 */
object NotificationHelper {

    fun createNotificationChannel(context: Context, importance: Int, name: String, description: String){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(true)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun handleNotif(context: Context, remoteMessage: RemoteMessage) {

        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val resultPendingIntent = PendingIntent.getActivity(context,
            0,intent, PendingIntent.FLAG_CANCEL_CURRENT)

        //val channelId = "${context.packageName}"
        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.logo_vector)
            .setContentTitle("Notifikasi Juragan")
            .setContentText(remoteMessage.notification?.body)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelId = "Notif"
            val channelName = "Notif"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, channelName, importance)
            mChannel.enableVibration(true)
            mBuilder.setChannelId(channelId)
            notifManager.createNotificationChannel(mChannel)
        }

        notifManager.notify(0,mBuilder.build())
    }
}