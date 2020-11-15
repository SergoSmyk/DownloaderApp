package com.sergo_smyk.downloader.api

import android.app.DownloadManager as DM

enum class DownloadStatus(val code: Int) {
    STATUS_UNKNOWN(Int.MIN_VALUE),
    STATUS_SAVED(Int.MAX_VALUE),
    STATUS_PENDING(DM.STATUS_PENDING),
    STATUS_RUNNING(DM.STATUS_RUNNING),
    STATUS_PAUSED(DM.STATUS_PAUSED),
    STATUS_DOWNLOADED(DM.STATUS_SUCCESSFUL),
    STATUS_FAILED(DM.STATUS_FAILED);

    companion object {
        fun get(status: Int): DownloadStatus {
            return values().firstOrNull { it.code == status } ?: STATUS_UNKNOWN
        }
    }
}