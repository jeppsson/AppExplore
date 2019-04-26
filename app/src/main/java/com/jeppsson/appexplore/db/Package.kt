package com.jeppsson.appexplore.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.NonNull

@Entity(tableName = "packages")
data class Package(
    @PrimaryKey
    val packageName: String,
    val appName: String = "",
    val minSdkVersion: Int = 0,
    val targetSdkVersion: Int = 0
)
