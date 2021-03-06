package com.shenrui.label.biaoqian.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.*


/**
 * 数据库拷贝辅助类
 * Created by cheng on 2018/3/4.
 */
class BookSqliteOpenHelper(private val mContext: Context,
                           private val mFilePath: String,
                           private val mDbName: String)
    : SQLiteOpenHelper(mContext, mDbName, null, 1) {
    //The Android's default system path of your application database.
    private val DB_PATH = android.os.Environment.getExternalStorageDirectory().absolutePath + "/biaoqiansql/"

    private val myDataBase: SQLiteDatabase? = null

    @Throws(IOException::class)
    fun createDataBase(): String {
        val dbExist = checkDataBase()
        Log.e("-------", "----createDataBase---dbExist=$dbExist---")
        if (!dbExist) {
            Log.e("-------", "------onError----1--")
            try {
                val dir = File(DB_PATH)
                Log.e("-------", "------onError---2---")
                if (!dir.exists()) {
                    dir.mkdir()
                }
                Log.e("-------", "------onError---3---")
                val dbf = File(DB_PATH + mDbName)
                if (dbf.exists()) {
                    dbf.delete()
                }
                Log.e("-------", "------onError---4---")
                SQLiteDatabase.openOrCreateDatabase(dbf, null)
                Log.e("-------", "------onError---5---")
                copyDataBase()
                Log.e("-------", "------onError---6---")
                return DB_PATH + mDbName
            } catch (e: IOException) {
                throw Error("数据库创建失败")
                Log.e("-------", "------onError----7--")
                return "null"
            }
        }
        return DB_PATH + mDbName
    }

    @Throws(IOException::class)
    private fun copyDataBase() {
        var myInput: InputStream? = null
        myInput = FileInputStream(mFilePath)
        val outFileName = DB_PATH + mDbName
        val myOutput = FileOutputStream(outFileName)
        val buffer = ByteArray(1024)
        var length = myInput.read(buffer)
        while (length > 0) {
            myOutput.write(buffer, 0, length)
            length = myInput.read(buffer)
        }
        myOutput.flush()
        myOutput.close()
        myInput.close()
    }

    private fun checkDataBase(): Boolean {
        val myPath = DB_PATH + mDbName
        val file = File(myPath)
        return file.exists()
    }

    @Synchronized
    override fun close() {
        myDataBase?.close()
        super.close()
    }

    override fun onCreate(db: SQLiteDatabase) {

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}