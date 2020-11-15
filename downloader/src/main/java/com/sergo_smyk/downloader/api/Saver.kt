package com.sergo_smyk.downloader.api

import android.app.Application
import java.io.File

interface Saver {

    suspend fun saveFileToOtherPlace(application: Application, file: File, item: DownloadItem)
}