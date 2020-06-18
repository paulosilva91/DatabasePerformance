package com.psilva.android.databaseperformance.databases.implementations.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.psilva.android.databaseperformance.databases.implementations.room.entities.DataRoom
import com.psilva.android.databaseperformance.databases.implementations.room.entities.DataRoomDao

@Database(entities = [DataRoom::class], version = 1)
abstract class RoomDatabaseHelper : RoomDatabase() {

    abstract fun getDataRoomDao(): DataRoomDao
}