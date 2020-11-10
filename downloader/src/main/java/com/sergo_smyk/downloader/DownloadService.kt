package com.sergo_smyk.downloader

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import com.sergo_smyk.downloader.api.DownloadItem

class DownloadService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        intent?.extras?.getParcelable<DownloadRequest>(DOWNLOAD_REQUEST)?.let { item ->
            return innerBind(item)
        } ?: throw NullPointerException("No Download item in intent")
    }

    private fun innerBind(item: DownloadRequest): IBinder {
        throw Exception()
    }

    companion object {
        private const val DOWNLOAD_REQUEST = "download_item_extra"
        private const val CHANNEL_ID = "downloader_notification_channel"

        fun buildIntent(request: DownloadRequest): Intent {
            return Intent().apply {
                putExtras(Bundle().apply {
                    putParcelable(DOWNLOAD_REQUEST, request)
                })
            }
        }
    }
}