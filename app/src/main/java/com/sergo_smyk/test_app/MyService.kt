package com.sergo_smyk.test_app

import android.app.Application
import android.util.Log
import com.sergo_smyk.downloader.DownloaderService
import com.sergo_smyk.downloader.api.DownloadItem
import com.sergo_smyk.downloader.api.NotificationMaker
import com.sergo_smyk.downloader.api.Saver
import kotlinx.coroutines.delay
import java.io.File

class MyService : DownloaderService() {

    private val notificationMaker = DownloadNotification()

    private val saver = object : Saver {
        override suspend fun saveFileToOtherPlace(
            application: Application,
            file: File,
            item: DownloadItem
        ) {
            Log.i("TestApp", "Saving file: ${file.name}, isExist:${file.exists()} item: $item")
            delay(5_000)
        }
    }

    override fun provideNotificationMaker() = notificationMaker

    override fun provideSaver() = saver
}
