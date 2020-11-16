package com.sergo_smyk.test_app

import android.app.Application
import android.util.Log
import com.sergo_smyk.downloader.api.*
import kotlinx.coroutines.delay
import java.io.File

class TestApp : Application(), DownloaderApplication {

    override fun provideNotificationMaker(): NotificationMaker {
        return DownloadNotification()
    }

    override fun provideSaver(): Saver? {
        return object : Saver {
            override suspend fun saveFileToOtherPlace(
                application: Application,
                file: File,
                item: DownloadItem
            ) {
                Log.i("TestApp", "Saving file: ${file.name}, isExist:${file.exists()} item: $item")
                delay(5_000)
            }
        }
    }
}