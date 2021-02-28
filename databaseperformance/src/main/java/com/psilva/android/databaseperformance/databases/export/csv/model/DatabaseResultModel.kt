package com.psilva.android.databaseperformance.databases.export.csv.model

import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum

class DatabaseResultModel {
    lateinit var databaseName: DatabaseEnum
    lateinit var databaseOperationEnum: DatabaseOperationEnum
    lateinit var databaseOperationTypeEnum: DatabaseOperationTypeEnum
    var quantityData: Long = 0
    var timeValue: Long = 0
    var dateTested: Long = 0


    constructor() {}
    constructor(databaseName: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, databaseOperationTypeEnum: DatabaseOperationTypeEnum, quantityData: Long, timeValue: Long, dateTested: Long) {
        this.databaseName = databaseName
        this.databaseOperationEnum = databaseOperationEnum
        this.databaseOperationTypeEnum = databaseOperationTypeEnum
        this.quantityData = quantityData
        this.dateTested = dateTested
        this.timeValue = timeValue
    }

    override fun toString(): String {
        return "DatabaseResultModel [name=$databaseName, databaseOperationEnum=$databaseOperationEnum, databaseOperationTypeEnum=$databaseOperationTypeEnum, quantityData=$quantityData, timeValue=$timeValue]"
    }
}