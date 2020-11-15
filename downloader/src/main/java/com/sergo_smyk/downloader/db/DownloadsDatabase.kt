package com.sergo_smyk.downloader.db

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sergo_smyk.downloader.db.DownloadsDatabase.Companion.DATABASE_VERSION
import com.sergo_smyk.downloader.db.dao.DownloaderDao
import com.sergo_smyk.downloader.db.model.DBItem

@Database(
    entities = [DBItem::class],
    version = DATABASE_VERSION
)
internal abstract class DownloadsDatabase : RoomDatabase() {

    abstract fun downloaderDao(): DownloaderDao

    companion object {
        const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "downloader_db"

        private var INSTANCE: DownloadsDatabase? = null

        fun getOrCreate(application: Application): DownloadsDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    application,
                    DownloadsDatabase::class.java,
                    DATABASE_NAME
                ).build()
            }

            return INSTANCE!!
        }
    }
}