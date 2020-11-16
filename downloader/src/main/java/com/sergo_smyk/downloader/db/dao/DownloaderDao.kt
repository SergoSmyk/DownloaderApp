package com.sergo_smyk.downloader.db.dao

import androidx.room.*
import com.sergo_smyk.downloader.api.DownloadStatus
import com.sergo_smyk.downloader.db.model.DBItem
import kotlinx.coroutines.flow.Flow

@Dao
internal interface DownloaderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg dbItem: DBItem)

    @Query(
        """
        UPDATE item 
        SET progress = :progress, status = :status, reason = :reason, totalSize = :totalBytes 
        WHERE downloadId = :downloadId"""
    )
    fun update(downloadId: Long, progress: Float, totalBytes: Long, status: Int, reason: Int)

    @Query(" UPDATE item SET status = :status WHERE downloadId = :downloadId")
    fun updateStatus(downloadId: Long, status: Int)

    @Query("SELECT * FROM item WHERE appId = :appId")
    fun getItemByAppId(appId: String): DBItem?

    @Query("SELECT * FROM item")
    fun getAll(): List<DBItem>

    @Query("SELECT * FROM item WHERE status = :downloadStatusCode")
    fun getAllDownloaded(downloadStatusCode: Int = DownloadStatus.STATUS_DOWNLOADED.code): List<DBItem>

    @Query("SELECT * FROM item WHERE downloadId = :downloadId")
    fun getItemByDownloadId(downloadId: Long): DBItem

    @Query("SELECT * FROM item WHERE appId = :appId")
    fun observeItem(appId: String): Flow<DBItem>

    @Query("SELECT downloadId FROM item")
    fun getAllDownloaderIds(): LongArray

    @Query("SELECT status FROM item")
    fun getAllStatuses(): List<Int>

    @Query("DELETE FROM item WHERE appId = :appId")
    fun deleteByAppId(appId: String)
}