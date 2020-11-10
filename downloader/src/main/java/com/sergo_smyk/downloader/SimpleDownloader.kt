package com.sergo_smyk.downloader

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import com.sergo_smyk.downloader.api.DownloadItem
import com.sergo_smyk.downloader.api.Downloader

class SimpleDownloader(
        private val context: Context
) : Downloader {

    override fun download(name: String, link: String, savePath: String) {
        val request = DownloadRequest(
                name = name,
                link = link,
                savePath = savePath
        )

        download(request)
    }

    override fun download(request: DownloadRequest) {
        val uri = Uri.parse(request.link)
        val intent = DownloadService.buildIntent(request)
        context.bindService(intent, object: ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }
        }, )
    }
}