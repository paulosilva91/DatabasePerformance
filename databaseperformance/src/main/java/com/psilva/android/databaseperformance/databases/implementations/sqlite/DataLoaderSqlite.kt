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

    private lateinit var _data: List<DataSqlite>
    private lateinit var _sqliteDatabase: SqliteDatabaseHelper
    private var _context: Context = context



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
        for (i in 0 until size) {
            list.add(generateData(i))
        }

        createData(list)
        readData()
        updateData(list)
        deleteData()
    }





    private fun createData(list: MutableList<DataSqlite>) {
        CREATE_DATA.startTiming()

        try {
            _sqliteDatabase.insertAll(list)
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE, ex)
            return
        }
        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE)
    }

    private fun readData() {
        READ_DATA.startTiming()

        try {
            _data = _sqliteDatabase.getAll()
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.READ, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.READ)
    }

    private fun updateData(list: MutableList<DataSqlite>) {
        for (entity in list) {
            entity.stringValue = generateString()
            entity.intValue = generateInt()
            entity.longValue = generateLong()
        }
        UPDATE_DATA.startTiming()

        try {
            _sqliteDatabase.updateAll(list)
        }
        catch (ex: java.lang.Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE)
    }

    private fun deleteData() {
        DELETE_DATA.startTiming()

        try {
            _sqliteDatabase.delete()
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE)
    }

    private fun initSqlite() {
        _sqliteDatabase = SqliteDatabaseHelper(_context)
    }

    private fun generateData(index : Long) : DataSqlite {
        return DataSqlite(index + 1, generateString(), generateInt(), generateLong())
    }
}