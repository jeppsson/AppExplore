package com.jeppsson.appexplore.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PackageDao {

    @Insert
    void insert(Package p);

    @Update
    int update(Package p);

    @Delete
    void delete(Package p);

    @Query("SELECT * FROM packages WHERE packageName LIKE :p LIMIT 1")
    Package findApp(String p);

    @Query("SELECT * FROM packages ORDER BY appName ASC")
    List<Package> allApps();

    @Query("SELECT * FROM packages WHERE packageName LIKE :p LIMIT 1")
    LiveData<Package> findAppLive(String p);

    @Query("SELECT * FROM packages ORDER BY appName ASC")
    LiveData<List<Package>> loadAllApps();
}
