package com.psilva.android.databaseperformance.databases

import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestResultListener
import com.psilva.android.databaseperformance.databases.utils.RandomFactory

abstract class BaseLoader<T> {

    protected var timings = createTimingLogger()

    protected abstract fun create(id: Long?, stringValue: String, intValue: Int?, longValue: Long?): T

    protected abstract fun createTimingLogger(): Timings

    protected abstract suspend fun execute(databaseOperationTypeEnum: DatabaseOperationTypeEnum, size: Long)

    lateinit var _databasePerformanceTestResultListener: IPerformanceTestResultListener

    companion object {
        const val CREATE_DATA = "Create"
        const val UPDATE_DATA = "Update"
        const val READ_DATA = "Read"
        const val DELETE_DATA = "Delete"
    }



    protected fun generateString(): String {
        return RandomFactory.getRandomString()
    }

    protected fun generateInt() : Int {
        return RandomFactory.getRandomInt()
    }

    protected fun generateLong() : Long {
        return RandomFactory.getRandomLong()
    }

    protected fun setDatabasePerformanceTestResultListener(performanceTestResultListener: IPerformanceTestResultListener) {
        _databasePerformanceTestResultListener = performanceTestResultListener
    }

    protected fun onProcessSuccess(databaseTypeEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum) {
        _databasePerformanceTestResultListener.onResultTimeSuccess(
            databaseTypeEnum,
            databaseOperationEnum,
            stopTiming()
        )
    }

    protected fun onProcessError(databaseTypeEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, exception: Exception) {
        _databasePerformanceTestResultListener.onResultError(
            databaseTypeEnum,
            databaseOperationEnum,
            stopTiming(),
            exception
        )
    }



    fun String.startTiming() {
        timings.operation = this
        timings.start()
    }

    fun stopTiming() : Long {
        return timings.finish()
    }
}