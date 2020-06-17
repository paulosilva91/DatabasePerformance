package com.psilva.apptest.databases

import com.psilva.apptest.utils.RandomFactory

abstract class BaseLoader<T> {

    protected var timings = createTimingLogger()

    protected abstract fun create(id: Long?, stringValue: String, intValue: Int?, longValue: Long?): T

    protected abstract fun createTimingLogger(): Timings

    protected abstract suspend fun execute(size: Long)

    companion object {
        val CREATE_DATA = "Create"
        val UPDATE_DATA = "Update"
        val READ_DATA = "Read"
        val DELETE_DATA = "Delete"
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

    fun String.startTiming() {
        timings.operation = this
        timings.start()
    }

    fun stopTiming() : Long {
        return timings.finish()
    }
}