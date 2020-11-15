package com.sergo_smyk.downloader.tools

import android.database.Cursor
import com.sergo_smyk.downloader.model.DMEntry
import android.app.DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR as DOWNLOADED_BYTES
import android.app.DownloadManager.COLUMN_DESCRIPTION as DESCRIPTION
import android.app.DownloadManager.COLUMN_ID as DOWNLOAD_ID
import android.app.DownloadManager.COLUMN_REASON as REASON
import android.app.DownloadManager.COLUMN_STATUS as STATUS
import android.app.DownloadManager.COLUMN_TITLE as TITLE
import android.app.DownloadManager.COLUMN_TOTAL_SIZE_BYTES as TOTAL_BYTES

internal class DMCursorReader {

    /**
     * Read info about downloads from DownloadManager Cursor
     * AND close Cursor after that
     */
    fun readInfoFromCursor(cursor: Cursor): List<DMEntry> {
        val list = mutableListOf<DMEntry>()
        while (cursor.moveToNext()) {
            list.add(
                DMEntry(
                    cursor.getLongByName(DOWNLOAD_ID),
                    cursor.getLongByName(TOTAL_BYTES),
                    cursor.getStringByName(TITLE),
                    cursor.getStringByName(DESCRIPTION),
                    cursor.getLongByName(DOWNLOADED_BYTES),
                    cursor.getIntByName(STATUS),
                    cursor.getIntByName(REASON)
                )
            )
        }
        cursor.close()
        return list
    }

    private fun Cursor.getIntByName(name: String): Int {
        return getInt(getColumnIndex(name))
    }

    private fun Cursor.getLongByName(name: String): Long {
        return getLong(getColumnIndex(name))
    }

    private fun Cursor.getStringByName(name: String): String {
        return getString(getColumnIndex(name))
    }
}