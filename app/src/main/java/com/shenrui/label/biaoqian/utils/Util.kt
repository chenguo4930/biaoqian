package com.shenrui.label.biaoqian.utils

import android.content.Context

class Util {
    companion object {
        /**
         * dp转px
         */
        fun dip2px(context: Context, dpValue: Int) = (dpValue * context.resources.displayMetrics.density + 0.5f).toInt()
    }
}