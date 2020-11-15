package com.sergo_smyk.downloader.db.model

import androidx.room.Entity
import com.sergo_smyk.downloader.api.DownloadRequest
import com.sergo_smyk.downloader.api.DownloadItem
import com.sergo_smyk.downloader.api.DownloadReason
import com.sergo_smyk.downloader.api.DownloadStatus
import com.sergo_smyk.downloader.model.SimpleDownloadItem

@Entity(
    tableName = "item",
    primaryKeys = ["appId", "downloadId"]
)
internal data class DBItem(
    val appId: String,
    val downloadId: Long,
    val title: String,
    val description: String,
    val totalSize: Long,
    val progress: Float,
    val status: Int,
    val reason: Int,
) {
    fun toDownloadItem(): DownloadItem {
        return SimpleDownloadItem(
            id = appId,
            title = title,
            description = description,
            totalSize = totalSize,
            progress = progress,
            status = DownloadStatus.get(status),
            reason = DownloadReason.get(reason)
        )
    }

    companion object {
        fun fromRequest(downloadId: Long, request: DownloadRequest): DBItem {
            return DBItem(
                appId = request.appId,
                title = request.title,
                description = request.description,
                downloadId = downloadId,
                totalSize = 0,
                progress = 0f,
                status = DownloadStatus.STATUS_UNKNOWN.code,
                reason = DownloadReason.NONE.code
            )
        }
    }
}