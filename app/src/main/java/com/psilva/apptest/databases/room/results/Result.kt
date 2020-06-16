package com.psilva.apptest.databases.room.results

import java.util.ArrayList
import java.util.HashMap

class Result(val type: String) {

    val times = HashMap<String, MutableList<Long>>()

    fun addTime(key: String, time: Long) {
        if (times.containsKey(key)) {
            times[key]!!.add(time)
        } else {
            times[key] = ArrayList<Long>().apply {
                add(time)
            }
        }
    }
}