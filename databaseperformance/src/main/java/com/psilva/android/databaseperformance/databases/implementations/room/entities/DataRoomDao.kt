package com.psilva.android.databaseperformance.databases.implementations.room.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DataRoomDao {

    @Query("SELECT * FROM DataRoom")
    suspend fun getAll(): Array<DataRoom>

    @Insert
    suspend fun insertAll(data: List<DataRoom>)

    @Update
    suspend fun update(data: List<DataRoom>)

    @Query("DELETE FROM dataRoom")
    suspend fun deleteAll()
}