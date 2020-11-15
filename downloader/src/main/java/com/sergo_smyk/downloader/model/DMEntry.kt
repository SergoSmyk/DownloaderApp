package com.sergo_smyk.downloader.model

internal data class DMEntry (
    val downloadId: Long,
    val totalBytes: Long,
    val title: String,
    val description: String,
    val downloadedBytes: Long,
    val status: Int,
    val reason: Int
)