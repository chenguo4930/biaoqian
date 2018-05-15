package com.example.lib.baselib.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.*
import com.example.lib.baselib.utils.ViewUtils

/**
 * @Author huangyue
 * @Date 2018/05/04 15:48
 * @Description
 */
abstract class BaseDialogFragment: DialogFragment() {

    lateinit var mRootView: View

    private var mOnDismissListener: DialogInterface.OnDismissListener? = null

    abstract fun getLayoutResId(): Int

    abstract fun initView()

    fun <T : View> findView(id: Int): T {
        return mRootView.findViewById(id)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return when (ViewUtils.isPort(context)) {
            true -> inflater.inflate(getLayoutResId(), container, false)
            false -> inflater.inflate(getLandLayoutResId(), container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mRootView = view
        initView()
        initEvent()
        initData()
    }

    override fun onStart() {
        super.onStart()
        initWindow()
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)
        dialog?.setCanceledOnTouchOutside(cancelableOnTouchOutside())
    }

    private fun initWindow() {
        try {
            (mRootView.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)

            val window = dialog.window
            // 去除对话框的边距
            window.setBackgroundDrawableResource(android.R.color.transparent)
            window.decorView.setPadding(0, 0, 0, 0)
            window.addFlags(Window.FEATURE_NO_TITLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            val params = window.attributes
            params.width = getBaseWindowWidth()
            params.height = getBaseWindowHeight()
            // 未覆盖面积的阴影程度
            params.dimAmount = getDimBehind()
            window.attributes = params

            // 对话框显示位置
            when (ViewUtils.isPort(context)) {
                true -> {
                    window.setGravity(getShowGravity())
                    window.setWindowAnimations(getWindowAnimations())
                }
                false -> {
                    window.setGravity(getLandShowGravity())
                    window.setWindowAnimations(getWindowLandAnimations())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 父类窗体宽度
     */
    private fun getBaseWindowWidth(): Int {
        return if (ViewUtils.isPort(context))
            getWindowWidth()
        else
            getLandWindowWidth()
    }

    /**
     * 父类窗体高度
     */
    private fun getBaseWindowHeight(): Int {
        return if (ViewUtils.isPort(context))
            getWindowHeight()
        else
            getLandWindowHeight()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        if (mOnDismissListener != null) {
            (mOnDismissListener as DialogInterface.OnDismissListener).onDismiss(dialog)
        }
    }

    /**
     * 竖屏宽度
     */
    open fun getWindowWidth(): Int {
        return matchParent()
    }

    /**
     * 竖屏高度
     */
    open fun getWindowHeight(): Int {
        return wrapContent()
    }

    /**
     * 横屏的窗体宽度
     */
    open fun getLandWindowWidth(): Int {
        return matchParent()
    }

    /**
     * 横屏的窗体高度
     */
    open fun getLandWindowHeight(): Int {
        return wrapContent()
    }

    open fun initEvent() {}

    open fun initData() {}

    open fun getLandLayoutResId(): Int {
        return getLayoutResId()
    }

    open fun cancelable(): Boolean {
        return true
    }

    open fun cancelableOnTouchOutside(): Boolean {
        return true
    }

    /**
     * 未覆盖区域的阴影程度
     * [0~1]:0为透明，1为全黑
     */
    open fun getDimBehind(): Float{
        return 0f
    }

    /**
     * 默认竖屏显示在底部
     */
    open fun getShowGravity(): Int {
        return if (ViewUtils.isPort(context))
            Gravity.CENTER
        else
            getLandShowGravity()
    }

    /**
     * 默认横屏显示在右侧
     */
    open fun getLandShowGravity(): Int {
        return Gravity.CENTER
    }

    /**
     * 竖屏时的动画
     */
    open fun getWindowAnimations(): Int {
//        return R.style.BottomDialogAnim
        return 0
    }

    /**
     * 横屏时的动画
     */
    open fun getWindowLandAnimations(): Int {
//        return R.style.RightDialogAnim
        return 0
    }

    /**
     * 显示对话框
     */
    override fun show(manager: FragmentManager, tag: String?) {
        if (this.isAdded) {
            dismiss()
        } else {
            super.show(manager, tag)
        }
    }

    /**
     * 显示对话框
     */
    fun show(manager: FragmentManager) {
        show(manager, tag)
    }

    override fun dismiss() {
        super.dismissAllowingStateLoss()
    }

    fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener) {
        mOnDismissListener = onDismissListener
    }

    fun wrapContent(): Int {
        return WindowManager.LayoutParams.WRAP_CONTENT
    }

    fun matchParent(): Int {
        return WindowManager.LayoutParams.MATCH_PARENT
    }

}