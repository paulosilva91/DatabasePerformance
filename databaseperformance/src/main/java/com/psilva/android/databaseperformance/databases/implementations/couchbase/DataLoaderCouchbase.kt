package com.psilva.android.databaseperformance.databases.implementations.couchbase

import android.content.Context
import com.couchbase.lite.*
import com.couchbase.lite.DataSource.database
import com.psilva.android.databaseperformance.databases.BaseLoader
import com.psilva.android.databaseperformance.databases.Timings
import com.psilva.android.databaseperformance.databases.implementations.couchbase.entities.DataCouchbase
import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestResultListener
import java.util.*


class DataLoaderCouchbase(context: Context, databasePerformanceTestResultListener: IPerformanceTestResultListener) : BaseLoader<DataCouchbase>() {

    private var quantityData: Long = 0
    private lateinit var data: MutableList<Result>
    private lateinit var couchdatabase: Database
    private var context: Context = context


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

    public override suspend fun execute(databaseOperationTypeEnum: DatabaseOperationTypeEnum, size: Long) {
        val list: MutableList<MutableDocument> = ArrayList(size.toInt())
        quantityData = size

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
        CouchbaseLite.init(context)

        couchdatabase = Database("couchbase_db", DatabaseConfiguration())
    }

    private fun generateData(index : Long) : MutableDocument {
        return DataCouchbase(index + 1, generateString(), generateInt(), generateLong()).toMutableDocument()
    }

    private fun createData(list: MutableList<MutableDocument>) {
        CREATE_DATA.startTiming()

        try {
            for (item in list) {
                couchdatabase.save(item)
            }
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE, ex)
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE, quantityData)
    }

    private fun readData() {
        READ_DATA.startTiming()

        try {
            val query: Query = QueryBuilder.select(SelectResult.all())
                .from(database(couchdatabase))
            val result: ResultSet = query.execute()
            data = result.allResults()
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.READ, ex)
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.READ, quantityData)
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
                couchdatabase.save(entity)
            }
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE, ex)
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE, quantityData)
    }

    private fun deleteData() {
        DELETE_DATA.startTiming()

        try {
            couchdatabase.delete()
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, ex)
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, quantityData)
    }

    private fun closeCouchbase() {
        couchdatabase.close()
    }
}