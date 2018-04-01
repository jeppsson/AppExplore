package com.jeppsson.appexplore.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

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
