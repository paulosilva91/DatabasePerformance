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

    //separate by threads
    suspend fun execute(size: Long) {
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

    suspend fun createData(list: MutableList<DataRoom>) {
        CREATE_DATA.startTiming()

        _room.getDataRoomDao().insertAll(list)

        onProcessSuccess(DatabaseOperationEnum.CREATE)
    }

    suspend fun readData() {
        READ_DATA.startTiming()

        _data = _room.getDataRoomDao().getAll()

        onProcessSuccess(DatabaseOperationEnum.READ)
    }

    suspend fun updateData() {
        for (i in _data.indices) {
            _data[i].stringValue = i.toString()
            _data[i].intValue = generateInt()
            _data[i].longValue = generateLong()
        }

        UPDATE_DATA.startTiming()

        _room.getDataRoomDao().update(_data.asList())

        onProcessSuccess(DatabaseOperationEnum.UPDATE)
    }

    suspend fun deleteData() {
        DELETE_DATA.startTiming()

        _room.getDataRoomDao().deleteAll()

        onProcessSuccess(DatabaseOperationEnum.DELETE)
    }

    fun generateData(index : Long) : DataRoom {
        return DataRoom(index + 1, generateString(), generateInt(), generateLong())
    }

    private fun onProcessSuccess(databaseOperationEnum: DatabaseOperationEnum) {
        _databasePerformanceTestResultListener.onResultTimeSuccess(CURRENT_DB_ENUM, databaseOperationEnum, stopTiming())
    }
}