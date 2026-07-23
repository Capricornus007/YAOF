package dev.cprn007.yaof.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracked_apps")
data class AppEntity(
    @PrimaryKey
    val ownerRepo: String,
    val appName: String,
    val currentVersion: String? = null,
    val iconUrl: String? = null,
    val sourceUrl: String,
    val providerType: String = "github",
    val apkFilterRegex: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)
