package com.sergo_smyk.downloader.api

interface DownloaderApplication {

    fun provideNotificationMaker(): NotificationMaker

    fun provideSaver(): Saver? = null
}