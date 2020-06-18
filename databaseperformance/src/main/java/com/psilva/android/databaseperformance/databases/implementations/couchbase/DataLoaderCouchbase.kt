package com.psilva.android.databaseperformance.databases.implementations.couchbase

import android.content.Context
import com.couchbase.lite.*
import com.couchbase.lite.DataSource.database
import com.psilva.android.databaseperformance.databases.BaseLoader
import com.psilva.android.databaseperformance.databases.Timings
import com.psilva.android.databaseperformance.databases.implementations.couchbase.entities.DataCouchbase
import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestResultListener
import java.util.*


class DataLoaderCouchbase(context: Context, databasePerformanceTestResultListener: IPerformanceTestResultListener) : BaseLoader<DataCouchbase>() {

    private lateinit var _data: MutableList<Result>
    private lateinit var _couchdatabase: Database
    private var _context: Context = context


    companion object {
        const val TAG = "Couchbase"
        val CURRENT_DB_ENUM = DatabaseEnum.COUCHBASE
    }


    init {
        initCouchbase()
        setDatabasePerformanceTestResultListener(databasePerformanceTestResultListener)
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
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE, ex)
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE)
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
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE, ex)
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.READ)
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
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE, ex)
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE)
    }

    private fun deleteData() {
        DELETE_DATA.startTiming()

        try {
            _couchdatabase.delete()
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, ex)
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE)
    }

    private fun closeCouchbase() {
        _couchdatabase.close()
    }
}