package com.psilva.android.databaseperformance.databases.implementations.ormlite

import android.content.Context
import com.j256.ormlite.table.TableUtils
import com.psilva.android.databaseperformance.databases.BaseLoader
import com.psilva.android.databaseperformance.databases.Timings
import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestResultListener
import com.psilva.android.databaseperformance.databases.implementations.ormlite.entities.DataOrmLite
import com.psilva.android.databaseperformance.databases.implementations.ormlite.entities.DataOrmLiteDao

class DataLoaderOrmLite(context: Context, databasePerformanceTestResultListener: IPerformanceTestResultListener) : BaseLoader<DataOrmLite>() {

    private var quantityData: Long = 0
    private lateinit var _data: MutableList<DataOrmLite>
    private var ormLite: DataOrmLiteDao
    private var _context: Context = context

    companion object {
        const val TAG = "OrmLite"
        val CURRENT_DB_ENUM = DatabaseEnum.ORMLITE
    }

    init {
        ormLite = DataOrmLiteDao(OrmLiteDatabaseHelper(_context).connectionSource)
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

        quantityData = size

        for (i in 0 until size) {
            list.add(generateData(i))
        }

        createData(list)
        readData()
        updateData()
        deleteData()

        ormLite.connectionSource.close()
    }


    private suspend fun createData(list: MutableList<DataOrmLite>) {
        CREATE_DATA.startTiming()

        try {
            for (item in list) {
                ormLite.create(item)
            }
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE, quantityData)
    }

    private suspend fun readData() {
        READ_DATA.startTiming()

        try {
            _data = ormLite.queryForAll()
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.READ, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.READ, quantityData)
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
                ormLite.update(item)
            }
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE, quantityData)
    }

    private suspend fun deleteData() {
        DELETE_DATA.startTiming()

        try {
            for (item in _data) {
                ormLite.delete(item)
            }
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, quantityData)
    }

    private fun generateData(index : Long) : DataOrmLite {
        return DataOrmLite(index + 1, generateString(), generateInt(), generateLong())
    }
}