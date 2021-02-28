package com.psilva.android.databaseperformance.databases.implementations.sqlite

import android.content.Context
import com.psilva.android.databaseperformance.databases.BaseLoader
import com.psilva.android.databaseperformance.databases.Timings
import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum
import com.psilva.android.databaseperformance.databases.implementations.sqlite.entities.DataSqlite
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestResultListener

class DataLoaderSqlite(context: Context, databasePerformanceTestResultListener: IPerformanceTestResultListener) : BaseLoader<DataSqlite>() {

    private var quantityData: Long = 0
    private lateinit var data: List<DataSqlite>
    private lateinit var sqliteDatabase: SqliteDatabaseHelper
    private var context: Context = context



    companion object {
        const val TAG = "SQLite"
        val CURRENT_DB_ENUM = DatabaseEnum.SQLITE
    }


    init {
        initSqlite()
        setDatabasePerformanceTestResultListener(databasePerformanceTestResultListener)
    }


    override fun create(id: Long?, stringValue: String, intValue: Int?, longValue: Long?): DataSqlite {
        return DataSqlite(id!!, stringValue, intValue!!, longValue!!)
    }

    override fun createTimingLogger(): Timings {
        return Timings(TAG)
    }

    public override suspend fun execute(databaseOperationTypeEnum: DatabaseOperationTypeEnum, size: Long) {
        val list: MutableList<DataSqlite> = mutableListOf<DataSqlite>()

        quantityData = size

        for (i in 0 until size) {
            list.add(generateData(i))
        }

        createData(list)
        readData()
        updateData(list)
        deleteData()

        sqliteDatabase.close()
    }





    private fun createData(list: MutableList<DataSqlite>) {
        CREATE_DATA.startTiming()

        try {
            sqliteDatabase.insertAll(list)
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE, ex)
            return
        }
        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE, quantityData)
    }

    private fun readData() {
        initSqlite()
        READ_DATA.startTiming()

        try {

            data = sqliteDatabase.getAll()
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.READ, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.READ, quantityData)
    }

    private fun updateData(list: MutableList<DataSqlite>) {
        for (entity in list) {
            entity.stringValue = generateString()
            entity.intValue = generateInt()
            entity.longValue = generateLong()
        }
        initSqlite()
        UPDATE_DATA.startTiming()

        try {
            sqliteDatabase.updateAll(list)
        }
        catch (ex: java.lang.Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE, quantityData)
    }

    private fun deleteData() {
        initSqlite()
        DELETE_DATA.startTiming()

        try {
            sqliteDatabase.delete()
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, quantityData)
    }

    private fun initSqlite() {
        sqliteDatabase = SqliteDatabaseHelper(context)
    }

    private fun generateData(index : Long) : DataSqlite {
        return DataSqlite(index + 1, generateString(), generateInt(), generateLong())
    }
}