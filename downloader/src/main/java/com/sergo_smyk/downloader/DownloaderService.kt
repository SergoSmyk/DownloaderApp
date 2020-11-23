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
import com.sergo_smyk.downloader.api.NotificationMaker
import com.sergo_smyk.downloader.api.Saver
import com.sergo_smyk.downloader.db.DownloadsDatabase
import com.sergo_smyk.downloader.db.dao.DownloaderDao
import com.sergo_smyk.downloader.db.model.DBItem
import com.sergo_smyk.downloader.tools.DMCursorReader
import com.sergo_smyk.downloader.tools.ServiceHelper
import kotlinx.coroutines.*
import java.io.File

abstract class DownloaderService : Service() {

    private val cursorReader = DMCursorReader()

    private var serviceScope: CoroutineScope? = null
    private var observeJob: Job? = null

    private lateinit var downloadManager: DownloadManager
    private lateinit var downloadDbDao: DownloaderDao

    abstract fun provideNotificationMaker(): NotificationMaker

    open fun provideSaver(): Saver? = null

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        initVariables()
        createDownloadsObserver()
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
        with(provideNotificationMaker()) {
            startForeground(
                getId(),
                makeFoundation(application)
                    .setOngoing(true)
                    .setCategory(NotificationCompat.CATEGORY_STATUS)
                    .build()
            )
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
            val itemInDb = downloadDbDao.getItemByAppId(request.appId)
            when {
                itemInDb == null -> createDownload(request)
                request.isReloadRequired -> reloadDownload(itemInDb, request)
                else -> createDownloadsObserver()
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

    private fun reloadDownload(item: DBItem, request: DownloadRequest) {
        downloadManager.remove(item.downloadId)
        getDownloadedFile(applicationContext, item)?.delete()
        createDownload(request)
    }

    private fun createDownloadsObserver() {
        observeJob?.cancel()
        startForeground()
        observeJob = serviceScope?.launch(Dispatchers.IO) {
            while (isStopObservingRequired()) {
                readInfoFromDownloadManager()
                delay(CHECK_DOWNLOAD_MANAGER_DELAY)
            }
            stopForeground(true)
            delay(STOP_SERVICE_DELAY)
            stopSelf()
        }
    }

    private fun isStopObservingRequired(): Boolean {
        return downloadDbDao.getAllStatuses().any {
            it != DownloadStatus.STATUS_SAVED.code
        }
    }

    private suspend fun readInfoFromDownloadManager() {
        downloadDbDao.getAllDownloaderIds().withNotEmpty {
            val query = DownloadManager.Query().setFilterById(*it)
            downloadManager.query(query)?.let { cursor ->
                readInfoFromCursorAndSave(cursor)
            } ?: run {
                Log.e(TAG, "Cursor is NULL")
            }
        }
    }

    private suspend fun readInfoFromCursorAndSave(cursor: Cursor) {
        readInfoFromCursor(cursor)
        saveDownloadedFiles()
    }

    private fun readInfoFromCursor(cursor: Cursor) {
        cursorReader.readInfoFromCursor(cursor).forEach { dmEntry ->
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
    }

    private suspend fun saveDownloadedFiles() {
        downloadDbDao.getAllDownloaded().forEach { downloaded ->
            provideSaver()?.let { saver ->
                getDownloadedFile(applicationContext, downloaded)?.let { downloadedFile ->
                    saver.saveFileToOtherPlace(
                        application,
                        downloadedFile,
                        downloaded.toDownloadItem()
                    )
                }
            }
            downloadDbDao.updateStatus(downloaded.downloadId, DownloadStatus.STATUS_SAVED.code)
        }
    }

    companion object {
        private const val CHECK_DOWNLOAD_MANAGER_DELAY = 200L
        private const val STOP_SERVICE_DELAY = 10_000L

        private const val TAG = "DownloaderService"

        private const val DOWNLOAD_REQUEST = "download_item_extra"

        fun <T: DownloaderService> buildIntent(context: Context, request: DownloadRequest, clazz: Class<T>): Intent {
            return Intent(context, clazz).apply {
                putExtras(Bundle().apply {
                    putParcelable(DOWNLOAD_REQUEST, request)
                })
            }
        }

        internal fun getDownloadedFile(context: Context, item: DBItem): File? {
            val appDir = context.getExternalFilesDir(null)
            val fileDir = File(appDir, item.savePath)
            val file = File(fileDir, item.fileName)
            return if (file.exists()) {
                file
            } else null
        }
    }
}