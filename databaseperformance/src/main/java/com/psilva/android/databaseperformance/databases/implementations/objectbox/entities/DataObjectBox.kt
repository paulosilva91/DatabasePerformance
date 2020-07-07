package com.psilva.android.databaseperformance.databases.implementations.objectbox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class DataObjectBox(@Id var id: Long, var stringValue: String, var intValue: Int, var longValue: Long)