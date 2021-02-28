package com.psilva.android.databaseperformance.databases.implementations.greendao

import android.content.Context
import com.psilva.android.databaseperformance.databases.BaseLoader
import com.psilva.android.databaseperformance.databases.Timings
import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum
import com.psilva.android.databaseperformance.databases.implementations.greendao.entities.DataGreenDao
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestResultListener
import java.lang.Exception

class DataLoaderGreenDao(context: Context, databasePerformanceTestResultListener: IPerformanceTestResultListener) : BaseLoader<DataGreenDao>() {

    private var quantityData: Long = 0
    private var context: Context = context
    private lateinit var greenDaoDatabase: GreenDaoDatabaseHelper

    companion object {
        const val TAG = "GreenDao"
        val CURRENT_DB_ENUM = DatabaseEnum.GREEN_DAO
    }


    init {
        initGreenDAO()
        setDatabasePerformanceTestResultListener(databasePerformanceTestResultListener)
    }


    override fun create(id: Long?, stringValue: String, intValue: Int?, longValue: Long?): DataGreenDao {
        return DataGreenDao(id!!, stringValue, intValue!!, longValue!!)
    }

    override fun createTimingLogger(): Timings {
        return Timings(TAG)
    }

    public override suspend fun execute(databaseOperationTypeEnum: DatabaseOperationTypeEnum, size: Long) {
        val list: MutableList<DataGreenDao> = mutableListOf()

        quantityData = size

        for(i in 0 until size) {
            list.add(generateData(i))
        }

        createData(list)
        readData()
        updateData(list)
        deleteData()

        greenDaoDatabase.close()
    }


    private fun createData(list: MutableList<DataGreenDao>) {
        CREATE_DATA.startTiming()

        try {
            greenDaoDatabase.insertAll(list)
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE, quantityData)
    }

    private fun readData() {
        READ_DATA.startTiming()

        try {
            val result = greenDaoDatabase.getAll()
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.READ, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.READ, quantityData)
    }

    private fun updateData(list: MutableList<DataGreenDao>) {
        for (entity in list) {
            entity.stringValue = generateString()
            entity.intValue = generateInt()
            entity.longValue = generateLong()
        }
        UPDATE_DATA.startTiming()

        try {
            greenDaoDatabase.updateAll(list)
        }
        catch (ex: java.lang.Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE, quantityData)
    }

    private fun deleteData() {
        DELETE_DATA.startTiming()

        try {
            greenDaoDatabase.deleteAll()
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, quantityData)
    }


    private fun initGreenDAO() {
        greenDaoDatabase = GreenDaoDatabaseHelper(context)
    }

    private fun generateData(index : Long) : DataGreenDao {
        return DataGreenDao(index + 1, generateString(), generateInt(), generateLong())
    }
}