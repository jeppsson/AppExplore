package com.jeppsson.appexplore.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PackageDao {

    @Insert
    fun insert(p: Package)

    @Update
    fun update(p: Package): Int

    @Delete
    fun delete(p: Package)

    @Query("SELECT * FROM packages WHERE packageName LIKE :p LIMIT 1")
    fun findApp(p: String): Package?

    @Query("SELECT * FROM packages ORDER BY appName ASC")
    fun allApps(): List<Package>

    @Query("SELECT * FROM packages WHERE (packageName LIKE :p OR appName LIKE :p) ORDER BY appName ASC")
    fun findAppsLive(p: String): LiveData<List<Package>>

    @Query(
        "SELECT * FROM packages WHERE (packageName LIKE :p OR appName LIKE :p)" +
                " AND targetSdkVersion >= :targetSdkVersionMin AND targetSdkVersion <= :targetSdkVersionMax" +
                " AND flags & :flags == :flags" +
                " ORDER BY appName ASC"
    )
    fun findAppsSdkLive(
        p: String,
        targetSdkVersionMin: Int,
        targetSdkVersionMax: Int,
        flags: Int
    ): LiveData<List<Package>>

    @Query("SELECT * FROM packages WHERE packageName LIKE :p LIMIT 1")
    fun findAppLive(p: String): LiveData<Package>
}
