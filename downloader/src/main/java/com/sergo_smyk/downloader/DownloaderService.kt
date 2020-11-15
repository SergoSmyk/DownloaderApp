package com.sergo_smyk.downloader

import android.app.DownloadManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sergo_smyk.downloader.api.DownloadRequest
import com.sergo_smyk.downloader.api.DownloadStatus
import com.sergo_smyk.downloader.api.DownloaderApplication
import com.sergo_smyk.downloader.db.DownloadsDatabase
import com.sergo_smyk.downloader.db.dao.DownloaderDao
import com.sergo_smyk.downloader.db.model.DBItem
import com.sergo_smyk.downloader.tools.DMCursorReader
import com.sergo_smyk.downloader.tools.ServiceHelper
import kotlinx.coroutines.*

internal class DownloaderService : Service() {

    private val cursorReader = DMCursorReader()

    private var serviceScope: CoroutineScope? = null
    private lateinit var downloadManager: DownloadManager
    private lateinit var downloadDbDao: DownloaderDao

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        (application as? DownloaderApplication)?.let {
            initVariables()
            createDownloadsObserver()
        } ?: run {
            Log.e(TAG, "Application isn't instance of DownloadApplication")
            stopSelf()
        }
    }

    private fun initVariables() {
        serviceScope?.cancel()
        serviceScope = CoroutineScope(Dispatchers.IO)

        downloadDbDao = DownloadsDatabase.getOrCreate(application).downloaderDao()
        downloadManager = ContextCompat.getSystemService(application, DownloadManager::class.java)!!
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        serviceScope?.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        intent?.let {
            processIntent(it)
        }
        return START_NOT_STICKY
    }

    private fun startForeground() {
        (application as? DownloaderApplication)?.let {
            with(it.provideNotificationMaker()) {
                startForeground(
                    getId(),
                    makeFoundation(application)
                        .setOngoing(true)
                        .setCategory(NotificationCompat.CATEGORY_STATUS)
                        .build()
                )
            }
        } ?: run {
            Log.e(TAG, "Application isn't instance of DownloadApplication")
            stopSelf()
        }
    }

    private fun processIntent(intent: Intent) {
        intent.extras?.getParcelable<DownloadRequest>(DOWNLOAD_REQUEST)?.let { request ->
            checkRequest(request)
        } ?: run {
            Log.e(TAG, "No download request in intent")
        }
    }

    private fun checkRequest(request: DownloadRequest) {
        serviceScope?.launch(Dispatchers.IO) {
            if (downloadDbDao.getItemByAppId(request.appId) == null) {
                createDownload(request)
            } else {
                createDownloadsObserver()
            }
        }
    }

    private fun createDownload(request: DownloadRequest) {
        val managerRequest = ServiceHelper.buildDownloadRequest(applicationContext, request)

        downloadManager.enqueue(managerRequest).let { downloadId ->
            downloadDbDao.save(DBItem.fromRequest(downloadId, request))
            createDownloadsObserver()
        }
    }

    private var observeJob: Job? = null

    private fun createDownloadsObserver() {
        observeJob?.cancel()
        startForeground()
        observeJob = serviceScope?.launch(Dispatchers.IO) {
            while (downloadDbDao.getAllStatuses()
                    .any { it != DownloadStatus.STATUS_DOWNLOADED.code }
            ) {
                val ids = downloadDbDao.getAllDownloaderIds()
                if (ids.isNotEmpty()) {
                    val cursor = downloadManager.query(
                        DownloadManager.Query().setFilterById(*ids)
                    )
                    readInfoFromCursorAndSave(cursor)
                }
                delay(200)
            }
            stopForeground(true)
            delay(10_000)
            stopSelf()
        }
    }

    private fun readInfoFromCursorAndSave(cursor: Cursor?) {
        cursor?.let { nnCursor ->
            cursorReader.readInfoFromCursor(nnCursor).forEach { dmEntry ->
                val progress = if (dmEntry.totalBytes > 0) {
                    dmEntry.downloadedBytes.toFloat() / dmEntry.totalBytes
                } else 0f

                downloadDbDao.update(
                    downloadId = dmEntry.downloadId,
                    progress = progress,
                    totalBytes = dmEntry.totalBytes,
                    status = dmEntry.status,
                    reason = dmEntry.reason
                )
            }
        } ?: run {
            Log.e(TAG, "Cursor is NULL")
        }
    }

    companion object {
        private const val TAG = "DownloaderService"

        private const val DOWNLOAD_REQUEST = "download_item_extra"

        fun buildIntent(context: Context, request: DownloadRequest): Intent {
            return Intent(context, DownloaderService::class.java).apply {
                putExtras(Bundle().apply {
                    putParcelable(DOWNLOAD_REQUEST, request)
                })
            }
        }
    }
}