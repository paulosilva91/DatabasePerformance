package com.psilva.android.databaseperformance.databases

import android.util.Log

class Timings(private val tag: String) {

    private var start = 0L
    var operation: String = "<operation>"

    fun start() {
        start = System.currentTimeMillis()
    }

    fun finish(): Long {
        val time = System.currentTimeMillis() - start
        Log.d("###$tag - $operation -> ", "$time ms")
        return time
    }
}