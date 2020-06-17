package com.psilva.apptest.databases.ormlite

import android.content.Context
import com.psilva.apptest.databases.BaseLoader
import com.psilva.apptest.databases.Timings
import com.psilva.apptest.databases.enums.DatabaseEnum
import com.psilva.apptest.databases.enums.DatabaseOperationEnum
import com.psilva.apptest.databases.interfaces.IPerformanceTestResultListener
import com.psilva.apptest.databases.ormlite.entities.DataOrmLite
import com.psilva.apptest.databases.ormlite.entities.DataOrmLiteDao

class DataLoaderOrmLite(context: Context, databasePerformanceTestResultListener: IPerformanceTestResultListener) : BaseLoader<DataOrmLite>() {

    private lateinit var _data: MutableList<DataOrmLite>
    private var _ormLite: DataOrmLiteDao
    private var _context: Context = context
    private var _databasePerformanceTestResultListener: IPerformanceTestResultListener

    companion object {
        const val TAG = "OrmLite"
        val CURRENT_DB_ENUM = DatabaseEnum.ORMLITE
    }

    init {
        _ormLite = DataOrmLiteDao(OrmLiteDatabaseHelper(_context).connectionSource)
        _databasePerformanceTestResultListener = databasePerformanceTestResultListener
    }




    override fun create(id: Long?, stringValue: String, intValue: Int?, longValue: Long?): DataOrmLite {
        return DataOrmLite(id!!, stringValue, intValue!!, longValue!!)
    }

    override fun createTimingLogger(): Timings {
        return Timings(TAG)
    }

    public override suspend fun execute(size: Long) {
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
            onProcessError(DatabaseOperationEnum.CREATE, ex)
        }

        onProcessSuccess(DatabaseOperationEnum.CREATE)
    }

    private suspend fun readData() {
        READ_DATA.startTiming()

        try {
            _data = _ormLite.queryForAll()
        }
        catch (ex: Exception) {
            onProcessError(DatabaseOperationEnum.READ, ex)
        }

        onProcessSuccess(DatabaseOperationEnum.READ)
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
            onProcessError(DatabaseOperationEnum.UPDATE, ex)
        }

        onProcessSuccess(DatabaseOperationEnum.UPDATE)
    }

    private suspend fun deleteData() {
        DELETE_DATA.startTiming()

        try {
            for (item in _data) {
                _ormLite.delete(item)
            }
        }
        catch (ex: Exception) {
            onProcessError(DatabaseOperationEnum.DELETE, ex)
        }

        onProcessSuccess(DatabaseOperationEnum.DELETE)
    }

    private fun generateData(index : Long) : DataOrmLite {
        return DataOrmLite(index + 1, generateString(), generateInt(), generateLong())
    }

    private fun onProcessSuccess(databaseOperationEnum: DatabaseOperationEnum) {
        _databasePerformanceTestResultListener.onResultTimeSuccess(CURRENT_DB_ENUM, databaseOperationEnum, stopTiming())
    }

    private fun onProcessError(databaseOperationEnum: DatabaseOperationEnum, exception: Exception) {
        _databasePerformanceTestResultListener.onResultError(CURRENT_DB_ENUM, databaseOperationEnum, stopTiming(), exception)
    }
}