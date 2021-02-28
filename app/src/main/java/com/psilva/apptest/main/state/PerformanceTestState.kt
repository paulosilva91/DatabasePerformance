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
    private var _testList: MutableList<DatabaseResultModel> = mutableListOf()

    fun addDatabaseTest(databaseOperationEnum: DatabaseOperationEnum, resultModel: ResultDataModel?, quantityData: Long, time: Long) {
        if(resultModel != null) {
            when (databaseOperationEnum) {
                DatabaseOperationEnum.CREATE -> resultModel.databaseLastRunDurationCreate = time
                DatabaseOperationEnum.READ -> resultModel.databaseLastRunDurationRead = time
                DatabaseOperationEnum.UPDATE -> resultModel.databaseLastRunDurationUpdate = time
                DatabaseOperationEnum.DELETE -> resultModel.databaseLastRunDurationDelete = time
            }
            resultModel.databaseLastRun = Calendar.getInstance().time
            resultModel.databaseTestQuantityData = quantityData

            val databaseResultModel = DatabaseResultModel(resultModel.database, databaseOperationEnum, resultModel.databaseTestType, resultModel.databaseTestQuantityData, time, resultModel.databaseLastRun!!.time)

            _testList.add(databaseResultModel)
        }
    }

    fun getDatabasePerformanceTest() : Collection<DatabaseResultModel> {
        return _testList
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