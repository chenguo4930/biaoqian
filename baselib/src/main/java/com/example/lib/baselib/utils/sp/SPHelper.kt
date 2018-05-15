package com.example.lib.baselib.utils.sp

import android.annotation.SuppressLint
import android.content.Context

/**
 * Author  ZYH
 * Date    2018/1/24
 * Des     sharedPreferences工具类
 */
object SPHelper {

    @SuppressLint("ApplySharedPref")
    fun write(context: Context?, data: String, tagname: String): Boolean {
        if (context == null) {
            return false

        }

        val sharedPreferences = context.getSharedPreferences(SPKeys.PREF_FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(tagname, data)
        editor.commit()
        return true
    }

    @SuppressLint("ApplySharedPref")
    fun write(context: Context?, data: Boolean, tagname: String): Boolean {
        if (context == null) {
            return false
        }
        val sharedPreferences = context.getSharedPreferences(SPKeys.PREF_FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(tagname, data)
        editor.commit()
        return true
    }

    @SuppressLint("ApplySharedPref")
    fun write(context: Context?, length: Int, tagname: String): Boolean {
        if (context == null) {
            return false
        }
        val sharedPreferences = context.getSharedPreferences(SPKeys.PREF_FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(tagname, length)
        editor.commit()
        return true
    }

    @SuppressLint("ApplySharedPref")
    fun write(context: Context?, length: Float, tagname: String): Boolean {
        if (context == null) {
            return false
        }
        val sharedPreferences = context.getSharedPreferences(SPKeys.PREF_FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat(tagname, length)
        editor.commit()
        return true
    }

    @SuppressLint("ApplySharedPref")
    fun write(context: Context?, length: Long, tagname: String): Boolean {
        if (context == null) {
            return false
        }
        val sharedPreferences = context.getSharedPreferences(SPKeys.PREF_FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong(tagname, length)
        editor.commit()
        return true
    }

    @SuppressLint("ApplySharedPref")
            /**
             * 设置String类型的值
             */
    fun setString(context: Context?, tagname: String, data: String) {
        if (context == null) {
            return
        }

        val sharedPreferences = context.getSharedPreferences(SPKeys.PREF_FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(tagname, data)
        editor.commit()
    }

    fun read(context: Context, tagname: String): String {
        val sharedPreferences = context.getSharedPreferences(SPKeys.PREF_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(tagname, "")
    }

    fun readBoolean(context: Context?, tagname: String): Boolean {
        if (context == null) {
            return false
        }
        val sharedPreferences = context.getSharedPreferences(SPKeys.PREF_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(tagname, false)
    }

    fun readFloat(context: Context?, tagname: String): Float {
        if (context == null) {
            return 0f
        }
        val sharedPreferences = context.getSharedPreferences(SPKeys.PREF_FILE_NAME, Context.MODE_PRIVATE)

        var data = 0f
        try {
            data = sharedPreferences.getFloat(tagname, 0f)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return data
    }

    fun readBoolean(context: Context?, tagname: String, defValue: Boolean?): Boolean {
        if (context == null) {
            return false
        }
        val sharedPreferences = context.getSharedPreferences(SPKeys.PREF_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(tagname, defValue!!)
    }

    fun readInt(context: Context?, tagname: String): Int {
        if (context == null) {
            return 0
        }
        val sharedPreferences = context.getSharedPreferences(
                SPKeys.PREF_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(tagname, 0)
    }

    fun readInt(context: Context?, tagname: String, defaultValue: Int): Int {
        if (context == null) {
            return 0
        }
        val sharedPreferences = context.getSharedPreferences(
                SPKeys.PREF_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(tagname, defaultValue)
    }

    @JvmOverloads
    fun readLong(context: Context?, tagname: String, defaultValue: Long = 0): Long {
        if (context == null) {
            return 0
        }
        val sharedPreferences = context.getSharedPreferences(
                SPKeys.PREF_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getLong(tagname, defaultValue)
    }

    @SuppressLint("ApplySharedPref")
    fun remove(context: Context?, tagname: String): Boolean {
        if (context == null) {
            return false
        }
        val sharedPreferences = context.getSharedPreferences(
                SPKeys.PREF_FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(tagname)
        editor.commit()
        return true
    }
}
