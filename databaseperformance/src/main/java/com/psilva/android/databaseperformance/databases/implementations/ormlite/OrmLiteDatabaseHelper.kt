package com.psilva.android.databaseperformance.databases.implementations.ormlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.psilva.android.databaseperformance.databases.implementations.ormlite.entities.DataOrmLite

class OrmLiteDatabaseHelper : OrmLiteSqliteOpenHelper {

    companion object {
        private val db = "ormLite.db"
        private val version = 1
    }

    constructor(context: Context) : super(context, db,null, version)

    override fun onCreate(database: SQLiteDatabase?, connectionSource: ConnectionSource?) {
        TableUtils.createTable(connectionSource, DataOrmLite::class.java)
    }

    override fun onUpgrade(database: SQLiteDatabase?, connectionSource: ConnectionSource?, oldVersion: Int, newVersion: Int) {}

    override fun close() {
        super.close()
    }


}