package com.jeppsson.appexplore.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.NonNull

@Entity(tableName = "packages")
class Package {

    @PrimaryKey
    @NonNull
    var packageName: String? = null

    var appName: String? = null

    var minSdkVersion: Int = 0

    var targetSdkVersion: Int = 0

    override fun toString(): String {
        return Package::class.java.simpleName + " packageName: " + packageName
    }
}
