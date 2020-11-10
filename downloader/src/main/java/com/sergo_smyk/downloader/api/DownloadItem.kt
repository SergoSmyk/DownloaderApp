package com.sergo_smyk.downloader.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DownloadItem(
        val id: Int,
        val name: String,
        val progress: Float,
): Parcelable