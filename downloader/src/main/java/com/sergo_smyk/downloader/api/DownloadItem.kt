package com.sergo_smyk.downloader.api

interface DownloadItem {
    val id: String
    val title: String
    val description: String
    val totalSize: Long
    val progress: Float
    val status: DownloadStatus
    val reason: DownloadReason
}