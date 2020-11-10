package com.sergo_smyk.downloader.api

import com.sergo_smyk.downloader.DownloadRequest

interface Downloader {
    fun download(name: String, link: String, savePath: String)

    fun download(request: DownloadRequest)
}