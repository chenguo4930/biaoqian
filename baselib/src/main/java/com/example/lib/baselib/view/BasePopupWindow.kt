package com.example.lib.baselib.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.example.lib.baselib.utils.ViewUtils

/**
 * @Author huangyue
 * @Date 2018/05/07 15:55
 * @Description
 */
abstract class BasePopupWindow(mContext: Context): PopupWindow(mContext) {

    var rootView: View
    var mContext = mContext

    init {
        rootView = LayoutInflater.from(mContext).inflate(this.getResourceId(), null)
        contentView = rootView
        width = this.getDialogWidth()
        height = this.getDialogHeight()
        this.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isOutsideTouchable = this.getIsOutsideTouchable()
        this.initView()
        this.initEvent()
    }

    abstract fun getResourceId(): Int

    abstract fun initView()

    open fun initEvent() {}

    open fun getDialogWidth(): Int {
        return WindowManager.LayoutParams.WRAP_CONTENT
    }

    open fun getDialogHeight(): Int {
        return WindowManager.LayoutParams.WRAP_CONTENT
    }

    open fun getIsOutsideTouchable(): Boolean {
        return true
    }

    /**
     * 展示在指定控件正下方
     */
    fun showInBottomCenter(v: View) {
        rootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = rootView.measuredWidth
        val location = IntArray(2)
        v.getLocationOnScreen(location)
        this.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + v.width / 2 - popupWidth / 2, location[1] + v.height)
    }

    /**
     * 展示在指定控件正上方
     */
    fun showInTopCenter(v: View) {
        rootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = rootView.measuredWidth
        val popupHeight = rootView.measuredHeight
        val location = IntArray(2)
        v.getLocationOnScreen(location)
        this.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + v.width / 2 - popupWidth / 2, location[1] - popupHeight)
    }

    /**
     * 与指定控件水平居中，默认展示在下方，如果下方位置不够则展示在上方
     */
    fun showInAutoCenter(v: View) {
        rootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = rootView.measuredWidth
        val popupHeight = rootView.measuredHeight
        val location = IntArray(2)
        v.getLocationOnScreen(location)
        // 这个20是保留距离，不能把空隙留得太狭窄
        if (ViewUtils.getScreenHeight(mContext) - location[1] - v.height - 20> popupHeight)
            this.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + v.width / 2 - popupWidth / 2, location[1] + v.height)
        else
            this.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + v.width / 2 - popupWidth / 2, location[1] - popupHeight)
    }
}