package com.example.lib.baselib.utils

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.WindowManager


/**
 * @Author huangyue
 * @Date 2018/05/04 16:11
 * @Description
 */
object ViewUtils {
    /**
     * 当前手机是否竖屏
     */
    fun isPort(context: Context?): Boolean {
        return context == null || context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    /**
     * 当前手机是否横屏
     */
    fun isLand(context: Context?): Boolean {
        return context != null && context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    /**
     * 获取手机屏幕高度
     */
    fun getScreenHeight(context: Context?): Int {
        val dm = DisplayMetrics()
        val windowMgr: WindowManager? = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowMgr?.defaultDisplay?.getRealMetrics(dm)
        return dm.heightPixels
    }

    /**
     * 获取手机屏幕宽度
     */
    fun getScreenWidth(context: Context?): Int {
        val dm = DisplayMetrics()
        val windowMgr: WindowManager? = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowMgr?.defaultDisplay?.getRealMetrics(dm)
        return dm.widthPixels
    }
}