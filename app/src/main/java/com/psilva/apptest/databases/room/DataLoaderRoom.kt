package com.psilva.apptest.databases.room

import android.content.Context
import androidx.room.Room
import com.psilva.apptest.databases.BaseLoader
import com.psilva.apptest.databases.Timings
import com.psilva.apptest.databases.room.entities.DataRoom
import com.psilva.apptest.databases.enums.DatabaseEnum
import com.psilva.apptest.databases.enums.DatabaseOperationEnum
import com.psilva.apptest.databases.interfaces.IPerformanceTestResultListener

class DataLoaderRoom(context: Context, databasePerformanceTestResultListener: IPerformanceTestResultListener) : BaseLoader<DataRoom>() {

    var _room: RoomDatabaseHelper private set
    private var _databasePerformanceTestResultListener: IPerformanceTestResultListener
    private lateinit var _data: Array<DataRoom>


    companion object {
        const val TAG = "room"
        val CURRENT_DB_ENUM = DatabaseEnum.ROOM
    }

    init {
        _room = Room.databaseBuilder(context, RoomDatabaseHelper::class.java, "room").build()
        _databasePerformanceTestResultListener = databasePerformanceTestResultListener
    }

    override fun create(id: Long?, stringValue: String, intValue: Int?, longValue: Long?): DataRoom {
        return DataRoom(id!!, stringValue, intValue!!, longValue!!)
    }

    override fun createTimingLogger(): Timings {
        return Timings(TAG)
    }

    public override suspend fun execute(size: Long) {
        val list: MutableList<DataRoom> = mutableListOf<DataRoom>()
        for (i in 0 until size) {
            list.add(generateData(i))
        }

        _room.getDataRoomDao().deleteAll()//TODO

        createData(list)
        readData()
        updateData()
        deleteData()
    }



    private suspend fun createData(list: MutableList<DataRoom>) {
        CREATE_DATA.startTiming()

        try {
            _room.getDataRoomDao().insertAll(list)
        }
        catch (ex: Exception) {
            onProcessError(DatabaseOperationEnum.CREATE, ex)
        }
        onProcessSuccess(DatabaseOperationEnum.CREATE)
    }

    private suspend fun readData() {
        READ_DATA.startTiming()

        try {
            _data = _room.getDataRoomDao().getAll()
        }
        catch (ex: Exception) {
            onProcessError(DatabaseOperationEnum.READ, ex)
        }

        onProcessSuccess(DatabaseOperationEnum.READ)
    }

    private suspend fun updateData() {
        for (i in _data.indices) {
            _data[i].stringValue = i.toString()
            _data[i].intValue = generateInt()
            _data[i].longValue = generateLong()
        }

        UPDATE_DATA.startTiming()

        try {
            _room.getDataRoomDao().update(_data.asList())
        }
        catch (ex: Exception) {
            onProcessError(DatabaseOperationEnum.UPDATE, ex)
        }

        onProcessSuccess(DatabaseOperationEnum.UPDATE)
    }

    private suspend fun deleteData() {
        DELETE_DATA.startTiming()

        try {
            _room.getDataRoomDao().deleteAll()
        }
        catch (ex: Exception) {
            onProcessError(DatabaseOperationEnum.DELETE, ex)
        }

        onProcessSuccess(DatabaseOperationEnum.DELETE)
    }

    private fun generateData(index : Long) : DataRoom {
        return DataRoom(index + 1, generateString(), generateInt(), generateLong())
    }

    private fun onProcessSuccess(databaseOperationEnum: DatabaseOperationEnum) {
        _databasePerformanceTestResultListener.onResultTimeSuccess(CURRENT_DB_ENUM, databaseOperationEnum, stopTiming())
    }

    private fun onProcessError(databaseOperationEnum: DatabaseOperationEnum, exception: Exception) {
        _databasePerformanceTestResultListener.onResultError(CURRENT_DB_ENUM, databaseOperationEnum, stopTiming(), exception)
    }
}