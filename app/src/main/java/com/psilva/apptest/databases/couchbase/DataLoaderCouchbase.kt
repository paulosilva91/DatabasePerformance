package com.psilva.apptest.databases.couchbase

import android.content.Context
import android.util.Log
import com.couchbase.lite.*
import com.couchbase.lite.DataSource.database
import com.psilva.apptest.databases.BaseLoader
import com.psilva.apptest.databases.Timings
import com.psilva.apptest.databases.couchbase.entities.DataCouchbase
import com.psilva.apptest.databases.enums.DatabaseEnum
import com.psilva.apptest.databases.enums.DatabaseOperationEnum
import com.psilva.apptest.databases.interfaces.IPerformanceTestResultListener
import java.util.*


class DataLoaderCouchbase(context: Context, databasePerformanceTestResultListener: IPerformanceTestResultListener) : BaseLoader<DataCouchbase>() {

    private lateinit var _data: MutableList<Result>
    private lateinit var _couchdatabase: Database
    private var _context: Context = context
    private var _databasePerformanceTestResultListener: IPerformanceTestResultListener


    companion object {
        const val TAG = "Couchbase"
        val CURRENT_DB_ENUM = DatabaseEnum.COUCHBASE
    }


    init {
        initCouchbase()
        _databasePerformanceTestResultListener = databasePerformanceTestResultListener
    }

    override fun create(id: Long?, stringValue: String, intValue: Int?, longValue: Long?): DataCouchbase {
        return DataCouchbase(id!!, stringValue, intValue!!, longValue!!)
    }

    override fun createTimingLogger(): Timings {
        return Timings(TAG)
    }

    public override suspend fun execute(size: Long) {
        val list: MutableList<MutableDocument> = ArrayList(size.toInt())

        for (i in 0 until size) {
            list.add(generateData(i))
        }

        createData(list)
        readData()
        updateData(list)
        deleteData()

        closeCouchbase()
    }

    private fun initCouchbase() {
        CouchbaseLite.init(_context)

        _couchdatabase = Database("couchbase_db", DatabaseConfiguration())
    }

    private fun generateData(index : Long) : MutableDocument {
        return DataCouchbase(index + 1, generateString(), generateInt(), generateLong()).toMutableDocument()
    }

    private fun createData(list: MutableList<MutableDocument>) {
        CREATE_DATA.startTiming()

        try {
            for (item in list) {
                _couchdatabase.save(item)
            }
        }
        catch (ex: Exception) {
            onProcessError(DatabaseOperationEnum.CREATE, ex)
        }

        onProcessSuccess(DatabaseOperationEnum.CREATE)
    }

    private fun readData() {
        READ_DATA.startTiming()

        try {
            val query: Query = QueryBuilder.select(SelectResult.all())
                .from(database(_couchdatabase))
            val result: ResultSet = query.execute()
            _data = result.allResults()
        }
        catch (ex: Exception) {
            onProcessError(DatabaseOperationEnum.CREATE, ex)
        }

        onProcessSuccess(DatabaseOperationEnum.READ)
    }

    private fun updateData(list: MutableList<MutableDocument>) {
        for (entity in list) {
            entity.setString("stringValue", generateString())
            entity.setInt("intValue", generateInt())
            entity.setLong("longValue", generateLong())
        }

        UPDATE_DATA.startTiming()

        try {
            for (entity in list) {
                _couchdatabase.save(entity)
            }
        }
        catch (ex: Exception) {
            onProcessError(DatabaseOperationEnum.UPDATE, ex)
        }

        onProcessSuccess(DatabaseOperationEnum.UPDATE)
    }

    private fun deleteData() {
        DELETE_DATA.startTiming()

        try {
            _couchdatabase.delete()
        }
        catch (ex: Exception) {
            onProcessError(DatabaseOperationEnum.DELETE, ex)
        }

        onProcessSuccess(DatabaseOperationEnum.DELETE)
    }

    private fun closeCouchbase() {
        _couchdatabase.close()
    }

    private fun onProcessSuccess(databaseOperationEnum: DatabaseOperationEnum) {
        _databasePerformanceTestResultListener.onResultTimeSuccess(CURRENT_DB_ENUM, databaseOperationEnum, stopTiming())
    }

    private fun onProcessError(databaseOperationEnum: DatabaseOperationEnum, exception: Exception) {
        _databasePerformanceTestResultListener.onResultError(CURRENT_DB_ENUM, databaseOperationEnum, stopTiming(), exception)
    }
}