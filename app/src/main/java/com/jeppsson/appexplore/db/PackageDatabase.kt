package com.jeppsson.appexplore.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Package::class], version = 1)
abstract class PackageDatabase : RoomDatabase() {

    abstract fun dao(): PackageDao

    companion object {

        private var INSTANCE: PackageDatabase? = null

        fun getAppDatabase(context: Context): PackageDatabase {
            val instance = INSTANCE
            if (instance != null) {
                return instance
            }

            val newInstance = Room.databaseBuilder(context.applicationContext,
                    PackageDatabase::class.java, "package-db")
                    .build()
            INSTANCE = newInstance

            return newInstance
        }
    }
}
