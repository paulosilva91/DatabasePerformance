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


    suspend fun execute(size: Long) {
        val list: MutableList<DataOrmLite> = mutableListOf<DataOrmLite>()
        for (i in 0 until size) {
            list.add(generateData(i))
        }

        createData(list)
        readData()
        updateData()
        deleteData()
    }


    suspend fun createData(list: MutableList<DataOrmLite>) {
        CREATE_DATA.startTiming()

        for (item in list) {
            _ormLite.create(item)
        }

        onProcessSuccess(DatabaseOperationEnum.CREATE)
    }

    suspend fun readData() {
        READ_DATA.startTiming()

        _data = _ormLite.queryForAll()

        onProcessSuccess(DatabaseOperationEnum.READ)
    }

    suspend fun updateData() {
        for (i in _data.indices) {
            _data[i].stringValue = i.toString()
            _data[i].intValue = generateInt()
            _data[i].longValue = generateLong()
        }

        UPDATE_DATA.startTiming()

        for (item in _data) {
            _ormLite.update(item)
        }

        onProcessSuccess(DatabaseOperationEnum.UPDATE)
    }

    suspend fun deleteData() {
        DELETE_DATA.startTiming()

        for (item in _data) {
            _ormLite.delete(item)
        }

        onProcessSuccess(DatabaseOperationEnum.DELETE)
    }

    fun generateData(index : Long) : DataOrmLite {
        return DataOrmLite(index + 1, generateString(), generateInt(), generateLong())
    }

    private fun onProcessSuccess(databaseOperationEnum: DatabaseOperationEnum) {
        _databasePerformanceTestResultListener.onResultTimeSuccess(CURRENT_DB_ENUM, databaseOperationEnum, stopTiming())
    }
}