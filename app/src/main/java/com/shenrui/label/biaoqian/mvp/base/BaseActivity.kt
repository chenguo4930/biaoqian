package com.shenrui.label.biaoqian.mvp.base

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.hazz.kotlinmvp.base.BasePresenter
import com.luckongo.tthd.mvp.base.IBaseContract

/**
 * @author chengguo
 * created: 2017/12/27
 * desc:BaseActivity基类
 */
abstract class BaseActivity<V : IBaseContract.IBaseView, T : BasePresenter<V>> : AppCompatActivity() {
    protected var mPresenter: T? = null

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        mPresenter = createPresenter()
        mPresenter?.attachView(this as V)
        initData()
        initListener()
    }

    /**
     *  加载布局
     */
    abstract fun layoutId(): Int

    /**
     * 子类实现具体的构建过程
     * @return
     */
    abstract fun createPresenter(): T?

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 初始化监听器
     */
    abstract fun initListener()

    override fun onDestroy() {
        mPresenter?.detachView()
        super.onDestroy()
    }

    /**
     * 打卡软键盘
     */
    fun openKeyBord(mEditText: EditText, mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    /**
     * 关闭软键盘
     */
    fun closeKeyBord(mEditText: EditText, mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mEditText.windowToken, 0)
    }
}


