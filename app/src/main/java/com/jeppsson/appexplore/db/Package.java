package com.jeppsson.appexplore.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "packages")
public class Package {

    @PrimaryKey
    @NonNull
    public String packageName;

    public String appName;

    public int minSdkVersion;

    public int targetSdkVersion;

    @Override
    public String toString() {
        return Package.class.getSimpleName() + " packageName: " + packageName;
    }
}
