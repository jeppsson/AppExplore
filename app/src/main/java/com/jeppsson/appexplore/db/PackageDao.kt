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

    @Query("SELECT * FROM packages WHERE packageName LIKE :p LIMIT 1")
    fun findAppLive(p: String): LiveData<Package>

    @Query("SELECT * FROM packages ORDER BY appName ASC")
    fun loadAllApps(): LiveData<List<Package>>
}
