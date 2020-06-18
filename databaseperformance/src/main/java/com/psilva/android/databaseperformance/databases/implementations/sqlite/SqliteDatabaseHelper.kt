package com.psilva.android.databaseperformance.databases.implementations.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.psilva.android.databaseperformance.databases.implementations.sqlite.entities.DataSqlite

class SqliteDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "sqlite.db"
        private const val DATABASE_VERSION = 1
    }


    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(Queries.SQL_DELETE_TABLE)
        database.execSQL(Queries.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) { }


    fun getAll() : List<DataSqlite> {
        val data = ArrayList<DataSqlite>()

        val cursor = readableDatabase.query(Queries.TABLE_NAME, null, null, null, null, null, null)
        cursor.moveToPosition(-1)
        while (cursor.moveToNext()) {
            data.add(DataSqlite(cursor.getInt(0).toLong(), cursor.getString(1), cursor.getInt(2), cursor.getLong(3)))
        }

        cursor.close()
        readableDatabase.close()

        return data
    }

    fun insertAll(data: List<DataSqlite>) {
        writableDatabase.beginTransaction()

        data.forEach { insert(it) }

        writableDatabase.setTransactionSuccessful()
        writableDatabase.endTransaction()
        writableDatabase.close()
    }

    fun insert(item: DataSqlite) {
        writableDatabase.insert(Queries.TABLE_NAME, null, getValues(item))
    }

    fun updateAll(data: List<DataSqlite>) {
        writableDatabase.beginTransaction()

        data.forEach { update(it) }

        writableDatabase.setTransactionSuccessful()
        writableDatabase.endTransaction()
        writableDatabase.close()
    }

    fun update(item: DataSqlite) {
        writableDatabase.update(Queries.TABLE_NAME, getValues(item), Queries.ID + "=" + item.id, null)
    }

    fun delete() {
        writableDatabase.delete(Queries.TABLE_NAME, null, null)
        writableDatabase.close()
    }

    private fun getValues(item: DataSqlite): ContentValues? {
        val values = ContentValues()
        values.put(Queries.ID, item.id)
        values.put(Queries.STRING_VALUE, item.stringValue)
        values.put(Queries.INT_VALUE, item.intValue)
        values.put(Queries.LONG_VALUE, item.longValue)

        return values
    }

    private object Queries {
        const val TABLE_NAME = "DATA"

        const val ID = "ID"
        const val STRING_VALUE = "STRING_VALUE"
        const val INT_VALUE = "INT_VALUE"
        const val LONG_VALUE = "LONG_VALUE"

        const val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
                "$ID INTEGER PRIMARY KEY, " +
                "$STRING_VALUE TEXT, " +
                "$INT_VALUE INTEGER, " +
                "$LONG_VALUE INTEGER)"

        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

        const val SQL_DELETE_DATA = "DELETE FROM $TABLE_NAME"
    }
}