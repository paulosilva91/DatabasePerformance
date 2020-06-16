package com.psilva.apptest.databases.ormlite.entities

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "DataOrmLite")
class DataOrmLite {
    @DatabaseField(columnName = "id", id = true)
    var id: Long = 0
    @DatabaseField(columnName = "stringValue")
    var stringValue: String = ""
    @DatabaseField(columnName = "intValue")
    var intValue: Int = 0
    @DatabaseField(columnName = "longValue")
    var longValue: Long = 0

    constructor()
    constructor(id: Long, stringValue: String, intValue: Int, longValue: Long) {
        this.id = id
        this.stringValue = stringValue
        this.intValue = intValue
        this.longValue = longValue
    }
}