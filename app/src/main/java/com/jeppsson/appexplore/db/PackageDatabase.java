package com.jeppsson.appexplore.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Package.class}, version = 1)
public abstract class PackageDatabase extends RoomDatabase {

    private static PackageDatabase INSTANCE;

    public abstract PackageDao dao();

    public static PackageDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    PackageDatabase.class, "package-db")
                    .build();
        }

        return INSTANCE;
    }
}
