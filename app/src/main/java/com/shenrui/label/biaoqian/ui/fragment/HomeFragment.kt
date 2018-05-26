package com.shenrui.label.biaoqian.ui.fragment

import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import com.luckongo.tthd.mvp.model.bean.SubStation
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.constrant.AllSubStation
import com.shenrui.label.biaoqian.database.SubStationDatabase
import com.shenrui.label.biaoqian.database.SubStationTable
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.ui.adapter.HomeGridListAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.toast
import java.io.File


class HomeFragment : BaseFragment() {

    private lateinit var mGridManager: GridLayoutManager
    private lateinit var mAdapter: HomeGridListAdapter

    override fun getLayoutId() = R.layout.fragment_home

    override fun initView() {

    }

    override fun onResume() {
        super.onResume()
        img_menu.setOnClickListener {
            if (AllSubStation.subStation == null || AllSubStation.subStation!!.isEmpty()) {
                toast("当前没有数据")
            } else {
                showMenu()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        img_menu.setOnClickListener(null)
    }

    override fun lazyLoad() {

        if (AllSubStation.subStation == null || AllSubStation.subStation!!.isEmpty()) {
            toast("当前没有数据")
            tv_empty.visibility = View.VISIBLE
        } else {
            mGridManager = GridLayoutManager(activity, HomeGridListAdapter.SPAN_COUNT_FOUR)
            mAdapter = HomeGridListAdapter(AllSubStation.subStation!!, mGridManager, object : HomeGridListAdapter.StationClickListener {
                override fun onDeleteItemClick(item: SubStation) {
                    AlertDialog.Builder(activity!!)
                            .setTitle("提示！")
                            .setMessage("你确定要删除该数据库吗？")
                            .setPositiveButton("确定") { _, _ -> onDeleteItem(item) }
                            .setNegativeButton("取消", null)
                            .create()
                            .show()
                }

                override fun onStationItemClick(item: SubStation) {
                    activity?.supportFragmentManager?.beginTransaction()
                            ?.add(R.id.content_frame, TestFragment.newInstance(item.db_path, item.sub_name))
                            ?.addToBackStack("TestFragment")
                            ?.commit()
                }
            })

            converting_station_rv.run {
                layoutManager = mGridManager
                adapter = mAdapter
            }

            tv_empty.visibility = View.GONE
        }
    }

    /**
     * 删除数据库
     */
    private fun onDeleteItem(item: SubStation) {
        if (File(item.db_path).exists()) {
            //删除本地文件
            File(item.db_path).delete()
            //从数据库中删除
            activity?.SubStationDatabase!!.use {
                delete(SubStationTable.TABLE_NAME, "db_path == ?", arrayOf(item.db_path))
                Log.e("---------", "------------删除数据库成功")
            }
            //从全局数据集合中删除，更新UI
            (AllSubStation.subStation as ArrayList).remove(item)
            lazyLoad()
        }else{
            toast("该文件不存在")
        }
    }

    /**
     * 显示菜单弹窗
     */
    private fun showMenu() {

        val inflate = LayoutInflater.from(activity)
        val view = inflate.inflate(R.layout.pop_home_menu, null)

        val mAppBasePopupWindow = PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mAppBasePopupWindow.isTouchable = true // 设置PopupWindow可触摸
        mAppBasePopupWindow.isOutsideTouchable = true // 设置PopupWindow外部区域是否可触摸
//        // 设置之后点击返回键 popwindow 会消失
//        mAppBasePopupWindow.setAnimationStyle(R.style.popuStyle)
//        mAppBasePopupWindow.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(App.mApplication, R.color.color_fafafa)))
        mAppBasePopupWindow.isFocusable = true

        // 监听点击事件，点击其他位置，popupwindow小窗口消失
        view.setOnTouchListener({ _, _ ->
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

}