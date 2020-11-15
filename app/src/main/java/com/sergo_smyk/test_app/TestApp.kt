package com.sergo_smyk.test_app

import android.app.Application
import com.sergo_smyk.downloader.api.Downloader
import com.sergo_smyk.downloader.api.DownloaderApplication
import com.sergo_smyk.downloader.api.NotificationMaker

class TestApp : Application(), DownloaderApplication {

    override fun provideNotificationMaker(): NotificationMaker {
        return DownloadNotification()
    }
}