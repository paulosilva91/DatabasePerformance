package com.psilva.apptest.databases.ormlite.entities

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource

class DataOrmLiteDao : BaseDaoImpl<DataOrmLite, Int> {
    constructor(connectionSource: ConnectionSource?) : super(DataOrmLite::class.java){
        setConnectionSource(connectionSource)
        initialize()
    }
}