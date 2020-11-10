package com.sergo_smyk.downloader

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DownloadRequest(
        val name: String,
        val link: String,
        val savePath: String
): Parcelable