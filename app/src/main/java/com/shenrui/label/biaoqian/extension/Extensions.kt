package com.shenrui.label.biaoqian.extension

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.shenrui.label.biaoqian.BuildConfig
import com.shenrui.label.biaoqian.app.App

/**
 * 扩展类
 */
fun Fragment.showToast(content: String): Toast {
    val toast = Toast.makeText(App.mApplication, content, Toast.LENGTH_SHORT)
    toast.show()
    return toast
}

fun Context.showToast(content: String): Toast {
    val toast = Toast.makeText(App.mApplication, content, Toast.LENGTH_SHORT)
    toast.show()
    return toast
}


fun View.dip2px(dipValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}

fun View.px2dip(pxValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

fun durationFormat(duration: Long?): String {
    val minute = duration!! / 60
    val second = duration % 60
    return if (minute <= 9) {
        if (second <= 9) {
            "0$minute' 0$second''"
        } else {
            "0$minute' $second''"
        }
    } else {
        if (second <= 9) {
            "$minute' 0$second''"
        } else {
            "$minute' $second''"
        }
    }
}

/**
 * 数据流量格式化
 */
fun Context.dataFormat(total: Long): String {
    var result: String
    var speedReal: Int = (total / (1024)).toInt()
    result = if (speedReal < 512) {
        speedReal.toString() + " KB"
    } else {
        val mSpeed = speedReal / 1024.0
        (Math.round(mSpeed * 100) / 100.0).toString() + " MB"
    }
    return result
}

/**
 * id:资源id，args:字符串资源需要进行格式化时传入
 *@author Chris
 *created at 2017/2/9 009
 */
fun TextView.text(id: Int = -1, vararg args: Any) {
    if (id > 0) {
        if (args.isNotEmpty()) {
            text = context.getString(id, *args)
        } else {
            text = context.getString(id)
        }
    }
}

fun EditText.text(id: Int = -1, vararg args: Any) {
    if (id > 0) {
        if (args.isNotEmpty()) {
            setText(context.getString(id, *args))
        } else {
            setText(context.getString(id))
        }
    }
}

/**
 *  ttfPath 格式 : "fonts/XXX.ttf"
 */
fun TextView.setTypeface(ttfPath: String? = null) {
    if (ttfPath == null) {
//        this.typeface = Typeface.createFromAsset(this.context.assets, "fonts/kai.ttf")
    } else {
        this.typeface = Typeface.createFromAsset(this.context.assets, ttfPath)
    }
}

/**
 * 设置view可见，如果设置返回true，不需要设置返回false-->本身就是可见的
 *@author Chris
 *created at 2016/10/24 024
 */
fun View.visible() =
        if (visibility != View.VISIBLE) {
            visibility = View.VISIBLE
            true
        } else {
            false
        }


/**
 * 设置view不可见，如果设置返回true，不需要设置返回false-->本身就是不可见的
 *@author Chris
 *created at 2016/10/24 024
 */
fun View.invisible(): Boolean {
    return if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
        true
    } else {
        false
    }
}

/**
 * 设置view不可见，如果设置返回true，不需要设置返回false-->本身就是不可见的
 *@author Chris
 *created at 2016/10/24 024
 */
fun View.gone(): Boolean {
    return if (visibility != View.GONE) {
        visibility = View.GONE
        true
    } else {
        false
    }
}

fun Activity.logE(msg: String) {
    if (BuildConfig.DEBUG){
        Log.e("--------", msg)
    }
}






