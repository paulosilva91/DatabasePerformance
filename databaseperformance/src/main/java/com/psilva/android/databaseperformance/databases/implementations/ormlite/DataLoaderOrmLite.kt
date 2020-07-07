package com.psilva.android.databaseperformance.databases.implementations.ormlite

import android.content.Context
import com.psilva.android.databaseperformance.databases.BaseLoader
import com.psilva.android.databaseperformance.databases.Timings
import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestResultListener
import com.psilva.android.databaseperformance.databases.implementations.ormlite.entities.DataOrmLite
import com.psilva.android.databaseperformance.databases.implementations.ormlite.entities.DataOrmLiteDao

class DataLoaderOrmLite(context: Context, databasePerformanceTestResultListener: IPerformanceTestResultListener) : BaseLoader<DataOrmLite>() {

    private lateinit var _data: MutableList<DataOrmLite>
    private var _ormLite: DataOrmLiteDao
    private var _context: Context = context

    companion object {
        const val TAG = "OrmLite"
        val CURRENT_DB_ENUM = DatabaseEnum.ORMLITE
    }

    init {
        _ormLite = DataOrmLiteDao(OrmLiteDatabaseHelper(_context).connectionSource)
        setDatabasePerformanceTestResultListener(databasePerformanceTestResultListener)
    }




    override fun create(id: Long?, stringValue: String, intValue: Int?, longValue: Long?): DataOrmLite {
        return DataOrmLite(id!!, stringValue, intValue!!, longValue!!)
    }

    override fun createTimingLogger(): Timings {
        return Timings(TAG)
    }

    public override suspend fun execute(databaseOperationTypeEnum: DatabaseOperationTypeEnum, size: Long) {
        val list: MutableList<DataOrmLite> = mutableListOf<DataOrmLite>()
        for (i in 0 until size) {
            list.add(generateData(i))
        }

        createData(list)
        readData()
        updateData()
        deleteData()
    }


    private suspend fun createData(list: MutableList<DataOrmLite>) {
        CREATE_DATA.startTiming()

        try {
            for (item in list) {
                _ormLite.create(item)
            }
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE)
    }

    private suspend fun readData() {
        READ_DATA.startTiming()

        try {
            _data = _ormLite.queryForAll()
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.READ, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.READ)
    }

    private suspend fun updateData() {
        for (i in _data.indices) {
            _data[i].stringValue = i.toString()
            _data[i].intValue = generateInt()
            _data[i].longValue = generateLong()
        }

        UPDATE_DATA.startTiming()

        try {
            for (item in _data) {
                _ormLite.update(item)
            }
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE)
    }

    private suspend fun deleteData() {
        DELETE_DATA.startTiming()

        try {
            for (item in _data) {
                _ormLite.delete(item)
            }
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE)
    }

    private fun generateData(index : Long) : DataOrmLite {
        return DataOrmLite(index + 1, generateString(), generateInt(), generateLong())
    }
}