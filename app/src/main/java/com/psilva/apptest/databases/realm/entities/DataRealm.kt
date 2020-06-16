package com.psilva.apptest.databases.realm.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class DataRealm(@PrimaryKey var id: Long = 0, var stringValue: String = "", var intValue: Int = 0, var longValue: Long = 0) : RealmObject()