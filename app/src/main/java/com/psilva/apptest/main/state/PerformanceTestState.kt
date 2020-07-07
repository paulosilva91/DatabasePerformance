package com.psilva.apptest.main.state

import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum
import com.psilva.android.databaseperformance.databases.export.csv.model.DatabaseResultModel
import com.psilva.apptest.main.ResultDataModel
import java.util.*
import kotlin.collections.LinkedHashMap

class PerformanceTestState() {
    private var _quantityTestToRun: Long = 0
    private lateinit var _databaseOperationTypeEnum: DatabaseOperationTypeEnum

    private var _testList: LinkedHashMap<Long, DatabaseResultModel> = LinkedHashMap()

    fun addDatabaseTest(databaseOperationEnum: DatabaseOperationEnum, resultModel: ResultDataModel?, time: Long) {
        if(resultModel != null) {
            when (databaseOperationEnum) {
                DatabaseOperationEnum.CREATE -> resultModel.databaseLastRunDurationCreate = time
                DatabaseOperationEnum.READ -> resultModel.databaseLastRunDurationRead = time
                DatabaseOperationEnum.UPDATE -> resultModel.databaseLastRunDurationUpdate = time
                DatabaseOperationEnum.DELETE -> resultModel.databaseLastRunDurationDelete = time
            }
            resultModel.databaseLastRun = Calendar.getInstance().time

            val databaseResultModel = DatabaseResultModel(resultModel.database, databaseOperationEnum, resultModel.databaseTestType, time, resultModel.databaseLastRun!!.time)

            _testList[time] = databaseResultModel
        }
    }

    fun getDatabasePerformanceTest() : Collection<DatabaseResultModel> {
        return _testList.values
    }

    fun getQuantityTestToRun(): Long {
        return _quantityTestToRun
    }

    fun setQuantityTestToRun(quantityTestToRun: Long) {
        _quantityTestToRun = quantityTestToRun
    }

    fun getDatabaseOperationType(): DatabaseOperationTypeEnum {
        return _databaseOperationTypeEnum
    }

    fun setDatabaseOperationType(databaseOperationType: DatabaseOperationTypeEnum) {
        _databaseOperationTypeEnum = databaseOperationType
    }
}