package com.psilva.android.databaseperformance.databases.implementations.objectbox

import android.content.Context
import com.psilva.android.databaseperformance.databases.BaseLoader
import com.psilva.android.databaseperformance.databases.Timings
import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum
import com.psilva.android.databaseperformance.databases.implementations.objectbox.entities.DataObjectBox
import com.psilva.android.databaseperformance.databases.implementations.objectbox.entities.MyObjectBox
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestResultListener
import io.objectbox.Box
import io.objectbox.BoxStore


class DataLoaderObjectbox(context: Context, databasePerformanceTestResultListener: IPerformanceTestResultListener) : BaseLoader<DataObjectBox>() {

    lateinit var _boxStore: BoxStore
    lateinit var _objectBoxHelper: Box<DataObjectBox>
    private var _context: Context = context


    companion object {
        const val TAG = "ObjectBox"
        val CURRENT_DB_ENUM = DatabaseEnum.OBJECT_BOX
    }


    init {
        initObjectBox()
        setDatabasePerformanceTestResultListener(databasePerformanceTestResultListener)
    }


    override fun create( id: Long?, stringValue: String, intValue: Int?, longValue: Long?): DataObjectBox {
        return DataObjectBox(id!!, stringValue, intValue!!, longValue!!)
    }

    override fun createTimingLogger(): Timings {
        return Timings(TAG)
    }

    public override suspend fun execute(databaseOperationTypeEnum: DatabaseOperationTypeEnum, size: Long) {
        val list: MutableList<DataObjectBox> = mutableListOf()
        for (i in 0 until size) {
            list.add(generateData(i))
        }

        createData(list)
        readData()
        updateData(list)
        deleteData()


        close()
    }

    private fun createData(list: MutableList<DataObjectBox>) {
        CREATE_DATA.startTiming()

        try {
            _objectBoxHelper.put(list)
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE, ex)
            return
        }
        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.CREATE)
    }

    private fun readData() {
        READ_DATA.startTiming()

        try {
            val result = _objectBoxHelper.all
            val bla = 1
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.READ, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.READ)
    }

    private fun updateData(list: MutableList<DataObjectBox>) {
        for (entity in list) {
            entity.stringValue = generateString()
            entity.intValue = generateInt()
            entity.longValue = generateLong()
        }
        UPDATE_DATA.startTiming()

        try {
            _objectBoxHelper.put(list)
        }
        catch (ex: java.lang.Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.UPDATE)
    }

    private fun deleteData() {
        DELETE_DATA.startTiming()

        try {
            _objectBoxHelper.removeAll()
        }
        catch (ex: Exception) {
            onProcessError(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE, ex)
            return
        }

        onProcessSuccess(CURRENT_DB_ENUM, DatabaseOperationEnum.DELETE)
    }

    private fun initObjectBox() {
        _boxStore = MyObjectBox.builder().androidContext(_context).build()
        _objectBoxHelper = _boxStore.boxFor(DataObjectBox::class.java)
    }

    private fun generateData(index : Long) : DataObjectBox {
        return DataObjectBox(0, generateString(), generateInt(), generateLong())
    }

    private fun close() {
        _boxStore.close()
        _boxStore.deleteAllFiles()
    }
}