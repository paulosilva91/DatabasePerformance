package com.psilva.android.databaseperformance.databases.implementations.realm

import android.content.Context
import com.psilva.android.databaseperformance.databases.BaseLoader
import com.psilva.android.databaseperformance.databases.Timings
import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestResultListener
import com.psilva.android.databaseperformance.databases.implementations.realm.entities.DataRealm
import io.realm.Realm
import io.realm.RealmResults
import java.util.*
import io.realm.kotlin.*
import java.lang.Exception

class DataLoaderRealm(private var context: Context, databasePerformanceTestResultListener: IPerformanceTestResultListener) : BaseLoader<DataRealm>() {

    private var quantityData: Long = 0
    private lateinit var results: RealmResults<DataRealm>
    private lateinit var realm: Realm


    companion object {
        const val TAG = "realm"
        val CURRENT_DB_ENUM = DatabaseEnum.REALM
    }


    init {
        initRealm()
        setDatabasePerformanceTestResultListener(databasePerformanceTestResultListener)
    }

    private fun initRealm() {
        Realm.init(context)
        restoreRealmDatabase()
        realm = Realm.getDefaultInstance()
    }

    private fun closeRealm() {
        realm.close()
    }

    private fun restoreRealmDatabase() {
        realm = Realm.getDefaultInstance()
        closeRealm()
        val configuration = realm.configuration
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

    public override suspend fun execute(databaseOperationTypeEnum: DatabaseOperationTypeEnum, size: Long) {
        var list: MutableList<DataRealm> = ArrayList(size.toInt())

        quantityData = size

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
            realm.beginTransaction()
            realm.insert(list)
            realm.commitTransaction()
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
            results = realm.where<DataRealm>().findAll()

            for (entity in results) {
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

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.READ, quantityData)
    }

    private suspend fun updateData(list: MutableList<DataRealm>) {
        for (entity in list) {
            entity.stringValue = generateString()
            entity.intValue = generateInt()
            entity.longValue = generateLong()
        }
        UPDATE_DATA.startTiming()

        try {
            realm.beginTransaction()
            realm.insertOrUpdate(list)
            realm.commitTransaction()
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
            realm.beginTransaction()
            results.deleteAllFromRealm()
            realm.commitTransaction()
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, quantityData)
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