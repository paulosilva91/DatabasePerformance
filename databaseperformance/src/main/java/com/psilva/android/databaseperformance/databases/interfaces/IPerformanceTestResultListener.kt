package com.psilva.android.databaseperformance.databases.interfaces

import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum

interface IPerformanceTestResultListener {
    fun onResultTimeSuccess(databaseEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, quantityData: Long, time: Long)
    fun onResultError(currentDbEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, stopTiming: Long, exception: Exception)
}