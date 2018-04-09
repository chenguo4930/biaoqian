package com.shenrui.label.biaoqian.ui.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.luckongo.tthd.mvp.model.bean.DeviceConnection
import com.luckongo.tthd.mvp.model.bean.SwitchConnection
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.mvp.model.bean.GLConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.TXConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.WLConnectionBean
import com.shenrui.label.biaoqian.ui.adapter.GLConnectionListItemRecyclerAdapter
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import kotlinx.android.synthetic.main.fragment_gl_connection.*
import kotlinx.android.synthetic.main.title_layout.*
import org.jetbrains.anko.support.v4.toast
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class GLConnectionFragment : BaseFragment(), FragmentBackHandler {

    private var mPath: String? = null
    private var mGLList: ArrayList<GLConnectionBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mPath = arguments!!.getString(ARG_PARAM1)
            mGLList = arguments!!.getParcelableArrayList<GLConnectionBean>(ARG_PARAM2)
        }
    }

    override fun getLayoutId() = R.layout.fragment_gl_connection

    @SuppressLint("SetTextI18n")
    override fun initView() {
        tv_back_title.text = mGLList!![0].odfConnection.optical_cable_number

        tv_title.text = "纤芯信息"

        img_back.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun lazyLoad() {
        initRecycleView()
    }

    /**
     * 获取到数据后初始化RecyclerView
     */
    private fun initRecycleView() {
        if (activity == null) {
            return
        }

        //TX链接图
        val txAdapter = GLConnectionListItemRecyclerAdapter(activity!!, mGLList!!)
        rv_gl_connection.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = txAdapter
        }

    }

    override fun onBackPressed() = BackHandlerHelper.handleBackPress(this)

    companion object {
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        fun newInstance(param1: String, param2: ArrayList<GLConnectionBean>): GLConnectionFragment {
            val fragment = GLConnectionFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putParcelableArrayList(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
