package com.sergo_smyk.downloader.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DownloadRequest(
        val appId: String,
        val title: String = "",
        val description: String = "",
        val link: String,
        val fileName: String,
        val savePath: String,
        val isReloadRequired: Boolean = true
): Parcelable