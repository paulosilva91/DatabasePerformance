package com.psilva.apptest.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.psilva.android.databaseperformance.databases.implementations.couchbase.DataLoaderCouchbase
import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestListener
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestResultListener
import com.psilva.android.databaseperformance.databases.implementations.ormlite.DataLoaderOrmLite
import com.psilva.android.databaseperformance.databases.implementations.realm.DataLoaderRealm
import com.psilva.android.databaseperformance.databases.implementations.room.DataLoaderRoom
import com.psilva.android.databaseperformance.databases.implementations.sqlite.DataLoaderSqlite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*
import kotlin.collections.LinkedHashMap

class MainViewModel : MainBaseObservableViewModel(),
    IPerformanceTestResultListener {

    private lateinit var _testTypeSelected: DatabaseOperationTypeEnum
    private var _quantityTest: Long = 1
    private var _quantityTestData: Long = 100
    private var _databaseQeue: Queue<DatabaseEnum> = LinkedList()
    private lateinit var _onPerformanceTestListener: IPerformanceTestListener
    private var _resultDataMap = LinkedHashMap<DatabaseEnum, ResultDataModel>()
    private val data = MutableLiveData<MutableCollection<ResultDataModel>> ()

    init {
        setData()
    }

    override fun onResultTimeSuccess(databaseEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, time: Long) {
        onProcessResult(databaseEnum, databaseOperationEnum, time)
    }

    override fun onResultError(currentDbEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, stopTiming: Long, exception: Exception) {
        onProcessResultError(currentDbEnum, databaseOperationEnum, stopTiming, exception)
    }




    fun setData() {
        data.value = _resultDataMap.values
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
        _onPerformanceTestListener = performanceTestListener
    }

    suspend fun processPerformanceTests(context: Context) = withContext(Dispatchers.IO){

        _resultDataMap.keys.forEach { _databaseQeue.add(it) }

        withContext(Dispatchers.Main) { _onPerformanceTestListener.onPerformanceTestStart() }

        val queueIterator = _databaseQeue.iterator()
        while (queueIterator.hasNext()) {
            val database = queueIterator.next()
            if(database != null) {
                when (database) {
                    DatabaseEnum.ROOM -> { executeRoomTest(context) }
                    DatabaseEnum.REALM -> { executeRealmTest(context) }
                    DatabaseEnum.ORMLITE -> { executeOrmLiteTest(context) }
                    DatabaseEnum.COUCHBASE -> { executeCouchbaseTest(context) }
                    DatabaseEnum.SQLITE -> { executeSQLiteTest(context) }
                }
            }
            queueIterator.remove()
        }

        withContext(Dispatchers.Main) { _onPerformanceTestListener.onPerformanceTestEnd() }
    }

    fun submitParameters(quantityTest: Long, quantityTestData: Long, testType: String, databaseList: MutableList<DatabaseEnum>) {
        _quantityTest = quantityTest
        _quantityTestData = quantityTestData

        _testTypeSelected = enumValueOf(testType)

        databaseList.forEach {
            if(!_resultDataMap.contains(it)) {
                _resultDataMap.put(it, ResultDataModel(it, Calendar.getInstance().time, 0,0,0,0))
            }
        }
        setData()
    }

    fun getQuantityTest(): Long {
        return _quantityTest
    }

    fun getQuantityTestData(): Long {
        return _quantityTestData
    }

    fun getDatabaseListToTest(): LinkedHashMap<DatabaseEnum, ResultDataModel> {
        return _resultDataMap
    }

    fun getDatabaseTestTypeList(): ArrayList<String> {
        val typeList = arrayListOf<String>()
        enumValues<DatabaseOperationTypeEnum>().forEach { typeList.add(it.name) }
        return typeList;
    }

    fun getData() : LiveData<MutableCollection<ResultDataModel>> {
        return data
    }



    private fun onProcessResult(databaseEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, time: Long) {
        var result = _resultDataMap.get(databaseEnum)

        if(result != null) {
            when (databaseOperationEnum) {
                DatabaseOperationEnum.CREATE -> result.databaseLastRunDurationCreate = time
                DatabaseOperationEnum.READ -> result.databaseLastRunDurationRead = time
                DatabaseOperationEnum.UPDATE -> result.databaseLastRunDurationUpdate = time
                DatabaseOperationEnum.DELETE -> result.databaseLastRunDurationDelete = time
            }
            result.databaseLastRun = Calendar.getInstance().time
        }

        data.postValue(_resultDataMap.values)
    }

    private fun onProcessResultError(databaseEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, time: Long, exception: Exception) {
        //TODO
    }



    private suspend fun executeRoomTest(context: Context) {
        DataLoaderRoom(context, this).execute(_quantityTestData)
    }

    private suspend fun executeRealmTest(context: Context) {
        DataLoaderRealm(context, this).execute(_quantityTestData)
    }

    private suspend fun executeOrmLiteTest(context: Context) {
        DataLoaderOrmLite(context, this).execute(_quantityTestData)
    }

    private suspend fun executeCouchbaseTest(context: Context) {
        DataLoaderCouchbase(context, this).execute(_quantityTestData)
    }

    private suspend fun executeSQLiteTest(context: Context) {
        DataLoaderSqlite(context, this).execute(_quantityTestData)
    }

}