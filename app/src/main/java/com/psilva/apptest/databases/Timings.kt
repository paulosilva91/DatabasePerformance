package com.psilva.apptest.databases

import android.util.Log
import com.psilva.apptest.databases.room.results.Result

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