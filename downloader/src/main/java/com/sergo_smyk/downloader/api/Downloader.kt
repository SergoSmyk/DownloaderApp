package com.sergo_smyk.downloader.api

import kotlinx.coroutines.flow.Flow

interface Downloader {
    fun download(request: DownloadRequest)

    suspend fun observe(requestId: String): Flow<DownloadItem>

    suspend fun getStatus(requestId: String): DownloadItem?

    suspend fun removeItemFromDownloader(requestId: String)
}