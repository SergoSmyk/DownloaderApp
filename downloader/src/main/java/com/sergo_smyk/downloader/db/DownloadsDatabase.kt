package com.sergo_smyk.downloader.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sergo_smyk.downloader.db.DownloadsDatabase.Companion.DATABASE_VERSION
import com.sergo_smyk.downloader.db.model.DownloadItem

@Database(
    entities = [DownloadItem::class],
    version = DATABASE_VERSION
)
internal abstract class DownloadsDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "downloader_db"

        fun create(context: Context): DownloadsDatabase {
            return Room.databaseBuilder(
                context,
                DownloadsDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}