package com.shenrui.label.biaoqian.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.mvp.model.bean.GLConnectionBean
import com.shenrui.label.biaoqian.ui.adapter.GLConnectionListItemRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_gl_connection.*
import kotlinx.android.synthetic.main.title_layout.*


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

        //GL链接图
        val txAdapter = GLConnectionListItemRecyclerAdapter(activity!!, mGLList!!, object : GLConnectionListItemRecyclerAdapter.AddOnClickListener {
            override fun onItemClick(item: GLConnectionBean) {
                activity?.supportFragmentManager?.beginTransaction()?.
                        add(R.id.content_frame, ConnectionFragment.newInstance(mPath!!, null, item, null))?.
                        addToBackStack("ConnectionFragment")?.
                        commit()
            }
        })

        rv_gl_connection.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = txAdapter
        }

    }

    override fun onBackPressed() = BackHandlerHelper.handleBackPress(this)

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

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
