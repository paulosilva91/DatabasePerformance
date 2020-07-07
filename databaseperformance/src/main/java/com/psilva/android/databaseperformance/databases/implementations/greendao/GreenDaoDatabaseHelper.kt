package com.psilva.android.databaseperformance.databases.implementations.greendao

import android.content.Context
import com.psilva.android.databaseperformance.databases.implementations.greendao.entities.DaoMaster
import com.psilva.android.databaseperformance.databases.implementations.greendao.entities.DaoSession
import com.psilva.android.databaseperformance.databases.implementations.greendao.entities.DataGreenDao
import com.psilva.android.databaseperformance.databases.implementations.greendao.entities.DataGreenDaoDao
import org.greenrobot.greendao.database.DatabaseOpenHelper

class GreenDaoDatabaseHelper(context: Context) : DatabaseOpenHelper(context, DATABASE_NAME, DATABASE_VERSION) {

    var daoSession: DaoSession private set
    var dataGreenDaoDao: DataGreenDaoDao

    companion object {
        private const val DATABASE_NAME = "green_dao.db"
        private const val DATABASE_VERSION = 1
    }

    init {
        val daoMaster = DaoMaster(writableDb)
        daoSession = daoMaster.newSession()
        DaoMaster.createAllTables(daoMaster.database, true)
        dataGreenDaoDao = daoSession.dataGreenDaoDao
    }

    fun getAll() : List<DataGreenDao> {
        return dataGreenDaoDao.loadAll()
    }

    fun insertAll(data: List<DataGreenDao>) {
        dataGreenDaoDao.insertInTx(data)
    }

    fun updateAll(data: List<DataGreenDao>) {
        dataGreenDaoDao.updateInTx(data)
    }

    fun deleteAll() {
        dataGreenDaoDao.deleteAll()
    }
}