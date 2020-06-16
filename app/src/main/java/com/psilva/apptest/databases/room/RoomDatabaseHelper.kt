package com.psilva.apptest.databases.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.psilva.apptest.databases.room.entities.DataRoom
import com.psilva.apptest.databases.room.entities.DataRoomDao

@Database(entities = [DataRoom::class], version = 1)
abstract class RoomDatabaseHelper : RoomDatabase() {

    abstract fun getDataRoomDao(): DataRoomDao
}