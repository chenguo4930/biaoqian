package com.shenrui.label.biaoqian.ui.fragment

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import com.luckongo.tthd.mvp.model.bean.SubStation
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.ui.adapter.HomeGridListAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.toast


class HomeFragment : BaseFragment() {


    private var mParam1: String? = null
    private var mParam2: String? = null

    private lateinit var mGridManager: GridLayoutManager
    private lateinit var mDataList: ArrayList<SubStation>
    private lateinit var mAdapter: HomeGridListAdapter

    companion object {

        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        fun newInstance(param1: String, param2: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun getLayoutId() = R.layout.fragment_home

    override fun initView() {
    }

    override fun lazyLoad() {
        getData()
        mGridManager = GridLayoutManager(activity, HomeGridListAdapter.SPAN_COUNT_FOUR)
        mAdapter = HomeGridListAdapter(mDataList, mGridManager, object : HomeGridListAdapter.StationClickListener {
            override fun onDeleteItemClick(item: SubStation) {
                toast("删除变电站${item.sub_name}")
            }

            override fun onStationItemClick(name: String) {
                toast("变电站名称$name")
                activity?.supportFragmentManager?.beginTransaction()?.
                        add(R.id.content_frame, TestFragment())?.
                        addToBackStack("TestFragment")?.
                        commit()
            }
        })

        converting_station_rv.run {
            layoutManager = mGridManager
            adapter = mAdapter
        }

        img_menu.setOnClickListener {
            showMenu()
        }

    }

    /**
     * 显示菜单弹窗
     */
    private fun showMenu() {

        val inflate = LayoutInflater.from(activity)
        val view = inflate.inflate(R.layout.pop_home_menu, null)

//        val mAppBasePopupWindow = AppBasePopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val mAppBasePopupWindow = PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        val mAppBasePopupWindow = PopupWindow(view)
        mAppBasePopupWindow.isTouchable = true // 设置PopupWindow可触摸
        mAppBasePopupWindow.isOutsideTouchable = true // 设置PopupWindow外部区域是否可触摸
//        // 设置之后点击返回键 popwindow 会消失
//        mAppBasePopupWindow.setAnimationStyle(R.style.popuStyle)
//        mAppBasePopupWindow.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(App.mApplication, R.color.color_fafafa)))
        mAppBasePopupWindow.isFocusable = true

        // 监听点击事件，点击其他位置，popupwindow小窗口消失
        view.setOnTouchListener({ v, event ->
            if (!mAppBasePopupWindow.isShowing) {
                mAppBasePopupWindow.dismiss()
            }
            true
        })

        view.find<TextView>(R.id.tv_grid).setOnClickListener {
            if (mGridManager.spanCount == HomeGridListAdapter.SPAN_COUNT_ONE) {
                mGridManager.spanCount = HomeGridListAdapter.SPAN_COUNT_FOUR
                mAdapter.notifyItemRangeChanged(0, mAdapter.itemCount)
            }
            mAppBasePopupWindow.dismiss()
        }
        view.find<TextView>(R.id.tv_list).setOnClickListener {
            if (mGridManager.spanCount == HomeGridListAdapter.SPAN_COUNT_FOUR) {
                mGridManager.spanCount = HomeGridListAdapter.SPAN_COUNT_ONE
                mAdapter.notifyItemRangeChanged(0, mAdapter.itemCount)
            }
            mAppBasePopupWindow.dismiss()
        }
        mAppBasePopupWindow.showAsDropDown(img_menu)
    }

    private fun getData() {
        var i = 0
        mDataList = ArrayList()
        while (i < 30) {
            i++
            mDataList.add(SubStation("变电站$i", 1, 831,
                    1, "SCBDZ",""))
        }
    }

}