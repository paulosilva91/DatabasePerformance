package com.psilva.apptest.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.psilva.android.databaseperformance.databases.implementations.couchbase.DataLoaderCouchbase
import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum
import com.psilva.android.databaseperformance.databases.export.csv.CSVFile
import com.psilva.android.databaseperformance.databases.implementations.greendao.DataLoaderGreenDao
import com.psilva.android.databaseperformance.databases.implementations.objectbox.DataLoaderObjectbox
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestListener
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestResultListener
import com.psilva.android.databaseperformance.databases.implementations.ormlite.DataLoaderOrmLite
import com.psilva.android.databaseperformance.databases.implementations.realm.DataLoaderRealm
import com.psilva.android.databaseperformance.databases.implementations.room.DataLoaderRoom
import com.psilva.android.databaseperformance.databases.implementations.sqlite.DataLoaderSqlite
import com.psilva.apptest.main.state.PerformanceTestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*
import kotlin.collections.LinkedHashMap

class MainViewModel : MainBaseObservableViewModel(), IPerformanceTestResultListener {

    private var performanceTestsState: PerformanceTestState = PerformanceTestState()
    private var databaseQueue: Queue<DatabaseEnum> = LinkedList()
    private lateinit var onPerformanceTestListener: IPerformanceTestListener
    private var resultDataMap = LinkedHashMap<DatabaseEnum, ResultDataModel>()
    private val data = MutableLiveData<MutableCollection<ResultDataModel>> ()

    init {
        setData()
    }

    override fun onResultTimeSuccess(databaseEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, quantityData: Long, time: Long) {
        onProcessResult(databaseEnum, databaseOperationEnum, quantityData, time)
    }

    override fun onResultError(currentDbEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, stopTiming: Long, exception: Exception) {
        onProcessResultError(currentDbEnum, databaseOperationEnum, stopTiming, exception)
    }




    private fun setData() {
        data.value = resultDataMap.values
    }

    fun fetchData(context: Context) {
        viewModelScope.launch {
            try {
                processPerformanceTests(context)
            }
            catch (e: Exception) {
                //TODO
                //error
            }
        }
    }

    fun setPerformanceTestListener(performanceTestListener: IPerformanceTestListener) {
        onPerformanceTestListener = performanceTestListener
    }

    fun submitParameters(quantityTestData: Long, testType: String, databaseList: MutableList<DatabaseEnum>) {

        val testTypeSelected : DatabaseOperationTypeEnum = enumValueOf(testType)

        performanceTestsState.setQuantityTestToRun(quantityTestData)
        performanceTestsState.setDatabaseOperationType(testTypeSelected)

        databaseList.forEach {
            if(!resultDataMap.contains(it)) {
                resultDataMap[it] =
                    ResultDataModel(it, null, 0,0,0,0, testTypeSelected)
            }
        }
        setData()
    }

    fun getQuantityTestData(): Long {
        return performanceTestsState.getQuantityTestToRun()
    }

    fun getDatabaseListToTest(): LinkedHashMap<DatabaseEnum, ResultDataModel> {
        return resultDataMap
    }

    fun getDatabaseTestTypeList(): ArrayList<String> {
        val typeList = arrayListOf<String>()
        enumValues<DatabaseOperationTypeEnum>().forEach { typeList.add(it.name) }
        return typeList;
    }

    fun getData() : LiveData<MutableCollection<ResultDataModel>> {
        return data
    }

    fun onExportCSVClicked() {
        CSVFile.export(performanceTestsState.getDatabasePerformanceTest())
    }



    private fun onProcessResult(databaseEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, quantityData: Long, time: Long) {
        val result = resultDataMap[databaseEnum]

        performanceTestsState.addDatabaseTest(databaseOperationEnum, result, quantityData, time)

        data.postValue(resultDataMap.values)
    }

    private fun onProcessResultError(databaseEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, time: Long, exception: Exception) {
        onPerformanceTestListener.onPerformanceTestError(databaseEnum, databaseOperationEnum, exception)
    }

    private suspend fun processPerformanceTests(context: Context) = withContext(Dispatchers.IO){

        clearDatabases(context)

        resultDataMap.keys.forEach { databaseQueue.add(it) }

        withContext(Dispatchers.Main) { onPerformanceTestListener.onPerformanceTestStart() }

        val queueIterator = databaseQueue.iterator()
        while (queueIterator.hasNext()) {
            val database = queueIterator.next()
            if(database != null) {
                when (database) {
                    DatabaseEnum.ROOM -> { executeRoomTest(context) }
                    DatabaseEnum.REALM -> { executeRealmTest(context) }
                    DatabaseEnum.ORMLITE -> { executeOrmLiteTest(context) }
                    DatabaseEnum.COUCHBASE -> { executeCouchbaseTest(context) }
                    DatabaseEnum.SQLITE -> { executeSQLiteTest(context) }
                    DatabaseEnum.GREEN_DAO -> { executeGreenDAOTest(context) }
                    DatabaseEnum.OBJECT_BOX -> { executeObjectBox(context) }
                }
            }
            queueIterator.remove()
        }

        withContext(Dispatchers.Main) { onPerformanceTestListener.onPerformanceTestEnd() }
    }

    private suspend fun executeRoomTest(context: Context) {
        DataLoaderRoom(context, this).execute(performanceTestsState.getDatabaseOperationType(), performanceTestsState.getQuantityTestToRun())
    }

    private suspend fun executeRealmTest(context: Context) {
        DataLoaderRealm(context, this).execute(performanceTestsState.getDatabaseOperationType(), performanceTestsState.getQuantityTestToRun())
    }

    private suspend fun executeOrmLiteTest(context: Context) {
        DataLoaderOrmLite(context, this).execute(performanceTestsState.getDatabaseOperationType(), performanceTestsState.getQuantityTestToRun())
    }

    private suspend fun executeCouchbaseTest(context: Context) {
        DataLoaderCouchbase(context, this).execute(performanceTestsState.getDatabaseOperationType(), performanceTestsState.getQuantityTestToRun())
    }

    private suspend fun executeSQLiteTest(context: Context) {
        DataLoaderSqlite(context, this).execute(performanceTestsState.getDatabaseOperationType(), performanceTestsState.getQuantityTestToRun())
    }

    private suspend fun executeObjectBox(context: Context) {
        DataLoaderObjectbox(context, this).execute(performanceTestsState.getDatabaseOperationType(), performanceTestsState.getQuantityTestToRun())
    }

    private suspend fun executeGreenDAOTest(context: Context) {
        DataLoaderGreenDao(context, this).execute(performanceTestsState.getDatabaseOperationType(), performanceTestsState.getQuantityTestToRun())
    }

    fun clearDatabases(context: Context) {
        var room = context.deleteFile("room.db")
        var ormLite = context.deleteFile("ormLite.db")
        var couchbase = context.deleteFile("couchbase_db")
        var sqlite = context.deleteFile("sqlite.db")
        var greenDAO = context.deleteFile("green_dao.db")
    }
}