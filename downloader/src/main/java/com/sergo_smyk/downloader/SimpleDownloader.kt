package com.sergo_smyk.downloader

import android.app.Application
import android.app.DownloadManager
import androidx.core.content.ContextCompat
import com.sergo_smyk.downloader.api.DownloadItem
import com.sergo_smyk.downloader.api.DownloadRequest
import com.sergo_smyk.downloader.api.Downloader
import com.sergo_smyk.downloader.db.DownloadsDatabase
import com.sergo_smyk.downloader.db.dao.DownloaderDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class SimpleDownloader<T: DownloaderService>(
    private val application: Application,
    private val serviceClass: Class<T>
    ) : Downloader {

    private val dao: DownloaderDao
        get() = DownloadsDatabase.getOrCreate(application).downloaderDao()

    override fun download(request: DownloadRequest) {
        val intent = DownloaderService.buildIntent(application, request, serviceClass)
        application.startService(intent)
    }

    override suspend fun observe(requestId: String): Flow<DownloadItem> {
        return dao.observeItem(requestId)
            .filterNotNull()
            .map { it.toDownloadItem() }
    }

    override suspend fun getStatus(requestId: String): DownloadItem? {
        return dao.getItemByAppId(requestId)?.toDownloadItem()
    }

    override suspend fun removeItemFromDownloader(requestId: String) {
        dao.getItemByAppId(requestId)?.let { item ->
            ContextCompat.getSystemService(application, DownloadManager::class.java)!!
                .remove(item.downloadId)
            DownloaderService.getDownloadedFile(application, item)?.delete()
            dao.deleteByAppId(requestId)
        }
    }
}