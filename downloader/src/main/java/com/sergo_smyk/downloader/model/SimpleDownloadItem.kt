package com.sergo_smyk.downloader.model

import com.sergo_smyk.downloader.api.DownloadItem
import com.sergo_smyk.downloader.api.DownloadReason
import com.sergo_smyk.downloader.api.DownloadStatus

data class SimpleDownloadItem(
    override val id: String,
    override val title: String,
    override val description: String,
    override val totalSize: Long,
    override val progress: Float,
    override val status: DownloadStatus,
    override val reason: DownloadReason
) : DownloadItem