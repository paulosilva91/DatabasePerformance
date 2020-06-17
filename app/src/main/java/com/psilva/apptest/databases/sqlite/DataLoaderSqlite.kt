package com.psilva.apptest.databases.sqlite

import android.content.Context
import com.psilva.apptest.databases.BaseLoader
import com.psilva.apptest.databases.Timings
import com.psilva.apptest.databases.enums.DatabaseEnum
import com.psilva.apptest.databases.enums.DatabaseOperationEnum
import com.psilva.apptest.databases.interfaces.IPerformanceTestResultListener
import com.psilva.apptest.databases.sqlite.entities.DataSqlite

class DataLoaderSqlite(context: Context, databasePerformanceTestResultListener: IPerformanceTestResultListener) : BaseLoader<DataSqlite>() {

    private lateinit var _data: List<DataSqlite>
    private lateinit var _sqliteDatabase: SqliteDatabaseHelper
    private var _context: Context = context
    private var _databasePerformanceTestResultListener: IPerformanceTestResultListener


    companion object {
        const val TAG = "Sqlite"
        val CURRENT_DB_ENUM = DatabaseEnum.SQLITE
    }


    init {
        initSqlite()
        _databasePerformanceTestResultListener = databasePerformanceTestResultListener
    }


    override fun create(id: Long?, stringValue: String, intValue: Int?, longValue: Long?): DataSqlite {
        return DataSqlite(id!!, stringValue, intValue!!, longValue!!)
    }

    override fun createTimingLogger(): Timings {
        return Timings(TAG)
    }

    public override suspend fun execute(size: Long) {
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
            onProcessError(DatabaseOperationEnum.CREATE, ex)
            return
        }
        onProcessSuccess(DatabaseOperationEnum.CREATE)
    }

    private fun readData() {
        READ_DATA.startTiming()

        try {
            _data = _sqliteDatabase.getAll()
        }
        catch (ex: Exception) {
            onProcessError(DatabaseOperationEnum.READ, ex)
            return
        }

        onProcessSuccess(DatabaseOperationEnum.READ)
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
            onProcessError(DatabaseOperationEnum.UPDATE, ex)
            return
        }

        onProcessSuccess(DatabaseOperationEnum.UPDATE)
    }

    private fun deleteData() {
        DELETE_DATA.startTiming()

        try {
            _sqliteDatabase.delete()
        }
        catch (ex: Exception) {
            onProcessError(DatabaseOperationEnum.DELETE, ex)
            return
        }

        onProcessSuccess(DatabaseOperationEnum.DELETE)
    }



    private fun initSqlite() {
        _sqliteDatabase = SqliteDatabaseHelper(_context)
    }

    private fun generateData(index : Long) : DataSqlite {
        return DataSqlite(index + 1, generateString(), generateInt(), generateLong())
    }

    private fun onProcessSuccess(databaseOperationEnum: DatabaseOperationEnum) {
        _databasePerformanceTestResultListener.onResultTimeSuccess(CURRENT_DB_ENUM, databaseOperationEnum, stopTiming())
    }

    private fun onProcessError(databaseOperationEnum: DatabaseOperationEnum, exception: Exception) {
        _databasePerformanceTestResultListener.onResultError(CURRENT_DB_ENUM, databaseOperationEnum, stopTiming(), exception)
    }
}