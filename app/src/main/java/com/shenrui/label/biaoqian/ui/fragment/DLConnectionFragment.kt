package com.shenrui.label.biaoqian.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.constrant.AllSubStation
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.mvp.model.bean.DLConnectionBean
import com.shenrui.label.biaoqian.ui.adapter.DLConnectionListItemRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_gl_connection.*
import kotlinx.android.synthetic.main.title_layout.*

/**
 * 电缆纤芯链接图
 */
class DLConnectionFragment : BaseFragment(), FragmentBackHandler {

    private var mPath: String? = null
    private var mDLList: ArrayList<DLConnectionBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mPath = arguments!!.getString(AllSubStation.PARAM_1)
            mDLList = arguments!!.getParcelableArrayList<DLConnectionBean>(AllSubStation.PARAM_2)
        }
    }

    override fun getLayoutId() = R.layout.fragment_dl_connection

    @SuppressLint("SetTextI18n")
    override fun initView() {
        tv_back_title.text = mDLList!![0].cableNo

        tv_title.text = "电缆纤芯信息"

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

        //DL链接图
        val txAdapter = DLConnectionListItemRecyclerAdapter(activity!!, mDLList!!)

        rv_gl_connection.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = txAdapter
        }

    }

    override fun onBackPressed() = BackHandlerHelper.handleBackPress(this)

    companion object {

        fun newInstance(param1: String, param2: ArrayList<DLConnectionBean>): DLConnectionFragment {
            val fragment = DLConnectionFragment()
            val args = Bundle()
            args.putString(AllSubStation.PARAM_1, param1)
            args.putParcelableArrayList(AllSubStation.PARAM_2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
