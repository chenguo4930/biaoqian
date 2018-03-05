package com.shenrui.label.biaoqian.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


/**
 * Created by cheng on 2018/3/4.
 */
class BookSqliteOpenHelper(private val myContext: Context) : SQLiteOpenHelper(myContext, "book.db", null, 1) {
    //The Android's default system path of your application database.
    private val DB_PATH = android.os.Environment.getExternalStorageDirectory().absolutePath + "/booksql/"
    private val myDataBase: SQLiteDatabase? = null

    @Throws(IOException::class)
    fun createDataBase() {
        val dbExist = checkDataBase()

        if (!dbExist) {
            try {
                val dir = File(DB_PATH)
                if (!dir.exists()) {
                    dir.mkdir()
                }
                val dbf = File(DB_PATH + DB_NAME)
                if (dbf.exists()) {
                    dbf.delete()
                }
                SQLiteDatabase.openOrCreateDatabase(dbf, null)
                copyDataBase()
            } catch (e: IOException) {
                throw Error("数据库创建失败")
            }

        }
    }

    @Throws(IOException::class)
    private fun copyDataBase() {
            var myInput: InputStream? = null
            //        try {
            myInput = myContext.assets.open(ASSETS_NAME)
            val outFileName = DB_PATH + DB_NAME
            val myOutput = FileOutputStream(outFileName)
            val buffer = ByteArray(1024)
            var length: Int
//            while ((length = myInput!!.read(buffer)) > 0) {
//                myOutput.write(buffer, 0, length)
//            }
            myOutput.flush()
            myOutput.close()
            myInput!!.close()
    }

    private fun checkDataBase(): Boolean {
        val myPath = DB_PATH + DB_NAME
        val file = File(myPath)
        return file.exists()
    }

    @Synchronized override fun close() {
        myDataBase?.close()
        super.close()
    }

    override fun onCreate(db: SQLiteDatabase) {

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    companion object {
        private val DB_NAME = "book.db"
        private val ASSETS_NAME = "book.db"
    }
}