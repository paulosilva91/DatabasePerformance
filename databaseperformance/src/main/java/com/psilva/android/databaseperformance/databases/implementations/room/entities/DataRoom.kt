package com.psilva.android.databaseperformance.databases.implementations.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DataRoom(
    @PrimaryKey var id: Long,
    @ColumnInfo(name = "stringValue") var stringValue: String?,
    @ColumnInfo(name = "intValue") var intValue: Int,
    @ColumnInfo(name = "longValue") var longValue: Long)