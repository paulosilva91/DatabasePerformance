package com.psilva.apptest.databases.realm

import android.content.Context
import com.psilva.apptest.databases.BaseLoader
import com.psilva.apptest.databases.Timings
import com.psilva.apptest.databases.enums.DatabaseEnum
import com.psilva.apptest.databases.enums.DatabaseOperationEnum
import com.psilva.apptest.databases.interfaces.IPerformanceTestResultListener
import com.psilva.apptest.databases.realm.entities.DataRealm
import io.realm.Realm
import io.realm.RealmResults
import java.util.*
import io.realm.kotlin.*

class DataLoaderRealm(context: Context, databasePerformanceTestResultListener: IPerformanceTestResultListener) : BaseLoader<DataRealm>() {

    private var _context: Context = context
    private lateinit var _results: RealmResults<DataRealm>
    private lateinit var _realm: Realm
    private var _databasePerformanceTestResultListener: IPerformanceTestResultListener



    companion object {
        const val TAG = "realm"
        val CURRENT_DB_ENUM = DatabaseEnum.REALM
    }


    init {
        initRealm()
        _databasePerformanceTestResultListener = databasePerformanceTestResultListener
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

    //separate by threads
    suspend fun execute(size: Long) {
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

    suspend fun createData(list: MutableList<DataRealm>) {
        CREATE_DATA.startTiming()

        _realm.beginTransaction()
        _realm.insert(list)
        _realm.commitTransaction()

        onProcessSuccess(DatabaseOperationEnum.CREATE)
    }

    suspend fun readData() {
        READ_DATA.startTiming()

        _results = _realm.where<DataRealm>().findAll()

        for (entity in _results) {
            entity.id
            entity.intValue
            entity.longValue
            entity.stringValue
        }

        onProcessSuccess(DatabaseOperationEnum.READ)
    }

    suspend fun updateData(list: MutableList<DataRealm>) {
        for (entity in list) {
            entity.stringValue = generateString()
            entity.intValue = generateInt()
            entity.longValue = generateLong()
        }

        UPDATE_DATA.startTiming()

        _realm.beginTransaction()
        _realm.insertOrUpdate(list)
        _realm.commitTransaction()

        onProcessSuccess(DatabaseOperationEnum.UPDATE)
    }

    suspend fun deleteData() {
        DELETE_DATA.startTiming()

        _realm.beginTransaction()
        _results.deleteAllFromRealm()
        _realm.commitTransaction()

        onProcessSuccess(DatabaseOperationEnum.DELETE)
    }


    fun generateData(index : Long) : DataRealm {
        return DataRealm(
            index + 1,
            generateString(),
            generateInt(),
            generateLong()
        )
    }

    private fun onProcessSuccess(databaseOperationEnum: DatabaseOperationEnum) {
        _databasePerformanceTestResultListener.onResultTimeSuccess(CURRENT_DB_ENUM, databaseOperationEnum, stopTiming())
    }

}