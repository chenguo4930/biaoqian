package com.shenrui.label.biaoqian.ui.fragment

import android.os.Bundle
import android.util.Log
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.mvp.model.bean.RegionBean
import com.shenrui.label.biaoqian.ui.adapter.RegionListAdapter
import kotlinx.android.synthetic.main.title_layout.*
import org.jetbrains.anko.support.v4.toast
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * 纤芯点击后，显示设备间具体的连接信息
 */
class ConnectionFragment : BaseFragment(), FragmentBackHandler {

    companion object {
        private const val PARAM_1 = "param1"
        private const val PARAM_2 = "param2"

        fun newInstance(param1: String, param2: String): ConnectionFragment {
            val fragment = ConnectionFragment()
            val args = Bundle()
            args.putString(PARAM_1, param1)
            args.putString(PARAM_2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    private var mDbPath: String? = null
    private var mSubStationName = ""

    private lateinit var mAdapter: RegionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mDbPath = arguments!!.getString(PARAM_1)
            mSubStationName = arguments!!.getString(PARAM_2)
        }
    }

    override fun getLayoutId() = R.layout.fragment_gx_connection

    override fun initView() {

    }

    override fun lazyLoad() {
        img_back.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        tv_back_title.text = mSubStationName
        if (mDbPath.isNullOrEmpty()) {
            toast("数据库路径为空")
        } else {
            Log.e("----", "---------mDbPath:$mDbPath")
            initData()
        }
    }

    private fun initData() {
        Observable.create(Observable.OnSubscribe<ArrayList<RegionBean>> {

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<RegionBean>>() {
                    override fun onCompleted() {
//                        toast("成功读取数据库")
                    }

                    override fun onError(e: Throwable) {
                        toast("读取数据库失败，请检查数据库是否存在")
                    }

                    override fun onNext(dataList: ArrayList<RegionBean>) {
                        initRecycler()
                    }
                })
    }

    /**
     * 初始化数据库数据
     */
    private fun initRecycler() {
        if (activity == null) {
            return
        }

//        mAdapter = RegionListAdapter(activity!!, mRegionBeanList, object : RegionListAdapter.RegionClickListener {
//            override fun onRegionItemClick(item: RegionBean) {
//                activity?.supportFragmentManager?.beginTransaction()?.add(R.id.content_frame, DeviceFragment.newInstance(mDbPath!!, item))?.addToBackStack("DeviceFragment")?.commit()
//            }
//
//        })
//        rv_region.run {
//            layoutManager = LinearLayoutManager(activity)
//            adapter = mAdapter
//        }
    }

    override fun onBackPressed() = BackHandlerHelper.handleBackPress(this)

}
