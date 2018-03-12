package com.shenrui.label.biaoqian.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * 变电站数据库
 * Created by chengguo on 18-3-9.
 */
class SubStationDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "SubStationDatabase", null, 1) {
    companion object {
        private var instance: SubStationDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): SubStationDatabaseOpenHelper {
            if (instance == null) {
                instance = SubStationDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable(SubStationTable.TABLE_NAME, true,
                SubStationTable.SUB_NAME to TEXT + PRIMARY_KEY + UNIQUE,
                SubStationTable.VOLLEVEL_ID to INTEGER,
                SubStationTable.PROVINCE_ID to INTEGER,
                SubStationTable.CITY_ID to INTEGER,
                SubStationTable.SUB_SHORT_NAME to TEXT,
                SubStationTable.DB_PATH to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("User", true)
    }
}

// Access property for Context
val Context.SubStationDatabase: SubStationDatabaseOpenHelper
    get() = SubStationDatabaseOpenHelper.getInstance(applicationContext)