package com.sergo_smyk.downloader.tools

import android.app.DownloadManager
import android.app.DownloadManager.Request.VISIBILITY_HIDDEN
import android.content.Context
import android.net.Uri
import com.sergo_smyk.downloader.api.DownloadRequest

internal object ServiceHelper {

    fun buildDownloadRequest(context: Context, request: DownloadRequest): DownloadManager.Request {
        val uri = Uri.parse(request.link)
        return DownloadManager.Request(uri)
            .setTitle(request.title)
            .setDescription(request.description)
            .setNotificationVisibility(VISIBILITY_HIDDEN)
            .setDestinationInExternalFilesDir(context, request.savePath, request.fileName)
    }
}