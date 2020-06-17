package com.psilva.apptest.databases.couchbase.entities

import com.couchbase.lite.MutableDocument

class DataCouchbase (var id: Long = 0, var stringValue: String = "", var intValue: Int = 0, var longValue: Long = 0) {

    fun toMutableDocument() : MutableDocument {
        return MutableDocument()
            .setLong("id", id)
            .setString("stringValue", stringValue)
            .setInt("intValue", intValue)
            .setLong("longValue", longValue)
    }
}