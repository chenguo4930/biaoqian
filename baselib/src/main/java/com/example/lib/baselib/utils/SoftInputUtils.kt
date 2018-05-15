package com.example.lib.baselib.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.inputmethod.InputMethodManager

/**
 * Author  ZYH
 * Date    2018/1/24
 * Des     软键盘工具类
 */
object SoftInputUtils {
    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    fun hideSoftInput(activity: Activity?) {
        try {
            if (activity == null) {
                return
            }
            val v = activity.window.decorView
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (v != null && v.windowToken != null) {
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            } else if (isSoftShowing(activity)) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        } catch (e: Exception) {
        }
    }

    /**
     * 显示软键盘
     *
     * @param activity
     */
    fun showSoftInput(activity: Activity?) {
        try {
            if (activity == null) {
                return
            }
            val m = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            m.toggleSoftInput(0, InputMethodManager.SHOW_FORCED)  //HIDE_NOT_ALWAYS
        } catch (e: Exception) {
        }
    }

    /**
     * 判断软键盘是否显示
     *
     * @param activity
     */
    private fun isSoftShowing(activity: Activity?): Boolean {
        if (activity == null) {
            return false
        }
        //获取View可见区域的bottom
        val rect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rect)

        val displayHight = rect.bottom - rect.top
        val height = activity.window.decorView.height //获取当前屏幕内容的高度

        return displayHight.toDouble() / height < 0.8
    }
}
