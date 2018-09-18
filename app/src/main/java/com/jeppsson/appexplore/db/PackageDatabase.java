package com.jeppsson.appexplore.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
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
