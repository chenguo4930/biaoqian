package com.shenrui.label.biaoqian.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.mvp.model.bean.WLConnectionBean
import com.shenrui.label.biaoqian.ui.adapter.WLConnectionListItemRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_wl_connection.*
import kotlinx.android.synthetic.main.title_layout.*
import kotlin.collections.ArrayList


class WLConnectionFragment : BaseFragment(), FragmentBackHandler {

    private var mPath: String? = null
    private var mWLConnectionList: ArrayList<WLConnectionBean>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mPath = arguments!!.getString(ARG_PARAM1)
            mWLConnectionList = arguments!!.getParcelableArrayList(ARG_PARAM2)
        }
    }

    override fun getLayoutId() = R.layout.fragment_wl_connection

    @SuppressLint("SetTextI18n")
    override fun initView() {
        tv_back_title.text = mWLConnectionList!![0].wlTailFiber.tail_cable_number

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
//        mWLConnectionList.sort()
        //WL详细链接图
        val txAdapter = WLConnectionListItemRecyclerAdapter(activity!!, mWLConnectionList!!,
                object : WLConnectionListItemRecyclerAdapter.AddOnClickListener {
                    override fun onItemClick(item: WLConnectionBean) {
                        activity?.supportFragmentManager?.beginTransaction()?.
                                add(R.id.content_frame, ConnectionFragment.newInstance(mPath!!, item, null, null))?.
                                addToBackStack("ConnectionFragment")?.commit()
                    }
                })
        rv_wl_detail_connection.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = txAdapter
        }
    }

    override fun onBackPressed() = BackHandlerHelper.handleBackPress(this)

    companion object {
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        fun newInstance(param1: String, param2: ArrayList<WLConnectionBean>): WLConnectionFragment {
            val fragment = WLConnectionFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putParcelableArrayList(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
