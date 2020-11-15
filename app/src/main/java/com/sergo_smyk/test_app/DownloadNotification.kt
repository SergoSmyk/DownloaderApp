package com.sergo_smyk.test_app

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import androidx.core.content.ContextCompat
import com.sergo_smyk.downloader.api.NotificationMaker

class DownloadNotification : NotificationMaker {

    override fun getId() = 123

    override fun makeFoundation(application: Application): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(application)
        }

        return NotificationCompat.Builder(application, CHANNEL_ID)
            .setContentTitle("Downloading file")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(0, 1, true)
            .setPriority(PRIORITY_MIN)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(application: Application) {
        val chan = NotificationChannel(
            CHANNEL_ID,
            "Downloader Service Channel",
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = ContextCompat.getSystemService(application, NotificationManager::class.java)
        service?.createNotificationChannel(chan)
    }

    companion object {
        private const val CHANNEL_ID = "downloads_channel"
    }
}