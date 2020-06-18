package com.psilva.apptest.main

import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import java.util.*

data class ResultDataModel(
    var database: DatabaseEnum,
    var databaseLastRun: Date,
    var databaseLastRunDurationCreate: Long = 0,
    var databaseLastRunDurationRead: Long = 0,
    var databaseLastRunDurationUpdate: Long = 0,
    var databaseLastRunDurationDelete: Long = 0
)