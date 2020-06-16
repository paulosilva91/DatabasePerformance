package com.psilva.apptest.databases.interfaces

import com.psilva.apptest.databases.enums.DatabaseEnum
import com.psilva.apptest.databases.enums.DatabaseOperationEnum

interface IPerformanceTestResultListener {
    fun onResultTimeSuccess(databaseEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, time: Long)
}