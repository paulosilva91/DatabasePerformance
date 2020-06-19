package com.psilva.android.databaseperformance.databases.interfaces

import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import java.lang.Exception

interface IPerformanceTestListener {
    fun onPerformanceTestStart()
    fun onPerformanceTestEnd()
    fun onPerformanceTestError(databaseEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, exception: Exception)
}