package com.psilva.android.databaseperformance.databases.implementations.realm

import android.content.Context
import com.psilva.android.databaseperformance.databases.BaseLoader
import com.psilva.android.databaseperformance.databases.Timings
import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestResultListener
import com.psilva.android.databaseperformance.databases.implementations.realm.entities.DataRealm
import io.realm.Realm
import io.realm.RealmResults
import java.util.*
import io.realm.kotlin.*
import java.lang.Exception

class DataLoaderRealm(context: Context, databasePerformanceTestResultListener: IPerformanceTestResultListener) : BaseLoader<DataRealm>() {

    private var _context: Context = context
    private lateinit var _results: RealmResults<DataRealm>
    private lateinit var _realm: Realm



    companion object {
        const val TAG = "realm"
        val CURRENT_DB_ENUM = DatabaseEnum.REALM
    }


    init {
        initRealm()
        setDatabasePerformanceTestResultListener(databasePerformanceTestResultListener)
    }

    private fun initRealm() {
        Realm.init(_context)
        restoreRealmDatabase()
        _realm = Realm.getDefaultInstance()
    }

    private fun closeRealm() {
        _realm.close()
    }

    private fun restoreRealmDatabase() {
        _realm = Realm.getDefaultInstance()
        closeRealm()
        val configuration = _realm.configuration
        Realm.deleteRealm(configuration)
    }


    override fun create(id: Long?, stringValue: String, intValue: Int?, longValue: Long?): DataRealm {
        return DataRealm(
            id!!,
            stringValue,
            intValue!!,
            longValue!!
        )
    }

    override fun createTimingLogger(): Timings {
        return Timings(TAG)
    }

    public override suspend fun execute(size: Long) {
        var list: MutableList<DataRealm> = ArrayList(size.toInt())

        for (i in 0 until size) {
            list.add(generateData(i))
        }

        createData(list)
        readData()
        updateData(list)
        deleteData()

        closeRealm()
    }

    private suspend fun createData(list: MutableList<DataRealm>) {
        CREATE_DATA.startTiming()

        try {
            _realm.beginTransaction()
            _realm.insert(list)
            _realm.commitTransaction()
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
            _results = _realm.where<DataRealm>().findAll()

            for (entity in _results) {
                entity.id
                entity.intValue
                entity.longValue
                entity.stringValue
            }
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.READ, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.READ)
    }

    private suspend fun updateData(list: MutableList<DataRealm>) {
        for (entity in list) {
            entity.stringValue = generateString()
            entity.intValue = generateInt()
            entity.longValue = generateLong()
        }
        UPDATE_DATA.startTiming()

        try {
            _realm.beginTransaction()
            _realm.insertOrUpdate(list)
            _realm.commitTransaction()
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
            _realm.beginTransaction()
            _results.deleteAllFromRealm()
            _realm.commitTransaction()
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE)
    }

    private fun generateData(index : Long) : DataRealm {
        return DataRealm(
            index + 1,
            generateString(),
            generateInt(),
            generateLong()
        )
    }
}