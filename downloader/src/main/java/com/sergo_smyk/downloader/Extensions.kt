package com.sergo_smyk.downloader

inline fun LongArray.withNotEmpty(action: (LongArray) -> Unit) {
    if (isNotEmpty()) {
        action.invoke(this)
    }
}