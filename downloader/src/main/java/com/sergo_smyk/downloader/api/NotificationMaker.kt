package com.sergo_smyk.downloader.api

import android.app.Application
import androidx.core.app.NotificationCompat

interface NotificationMaker {

    fun getId(): Int

    fun makeFoundation(application: Application): NotificationCompat.Builder
}