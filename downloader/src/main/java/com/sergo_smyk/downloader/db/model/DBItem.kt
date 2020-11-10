package com.sergo_smyk.downloader.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item")
internal data class DBItem(
    @PrimaryKey
    val id: Int,
    val name: String,
    val progress: Float,
)