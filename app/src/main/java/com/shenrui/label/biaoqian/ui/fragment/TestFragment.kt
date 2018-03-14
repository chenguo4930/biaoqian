package com.shenrui.label.biaoqian.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.luckongo.tthd.mvp.model.bean.Panel
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.mvp.model.bean.RegionBean
import com.shenrui.label.biaoqian.ui.adapter.RegionListAdapter
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import kotlinx.android.synthetic.main.fragment_test.*
import kotlinx.android.synthetic.main.title_layout.*
import org.jetbrains.anko.support.v4.toast
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class TestFragment : BaseFragment(), FragmentBackHandler {

    companion object {
        private const val PARAM_1 = "param1"
        private const val PARAM_2 = "param2"

        fun newInstance(param1: String, param2: String): TestFragment {
            val fragment = TestFragment()
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
    private lateinit var mRegionBeanList: ArrayList<RegionBean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mDbPath = arguments!!.getString(PARAM_1)
            mSubStationName = arguments!!.getString(PARAM_2)
        }
    }

    override fun getLayoutId() = R.layout.fragment_test

    override fun initView() {

    }

    override fun lazyLoad() {
        img_back.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        tv_back_title.text = "测试数据库"
        tv_title.text = mSubStationName
        if (mDbPath.isNullOrEmpty()) {
            toast("数据库路径为空")
        } else {
            Log.e("----", "---------mDbPath:$mDbPath")
            initData()
        }
    }

    private fun initData() {
        Observable.create(Observable.OnSubscribe<ArrayList<RegionBean>> {
            val regionList = DataBaseUtil.getRegioin(mDbPath!!)
            val panelList = DataBaseUtil.getPanel(mDbPath!!)
            val regionBeanList = ArrayList<RegionBean>()
            regionList.forEach { item ->
                val panelBeanList = ArrayList<Panel>()
                panelList.forEach {
                    if (it.region_id == item.region_id) {
                        panelBeanList.add(it)
                    }
                }
                regionBeanList.add(RegionBean(item.region_id, item.region_name, item.region_code, panelBeanList))
            }
            it.onNext(regionBeanList)
            it.onCompleted()
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
                        dataList.forEach {
                            Log.e("-----", "-----RegionBean=$it")
                        }
                        mRegionBeanList = dataList
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

        mAdapter = RegionListAdapter(activity!!, mRegionBeanList, object : RegionListAdapter.RegionClickListener {
            override fun onRegionItemClick(item: RegionBean) {
                activity?.supportFragmentManager?.beginTransaction()?.
                        add(R.id.content_frame, DeviceFragment.newInstance(mDbPath!!,item))?.
                        addToBackStack("DeviceFragment")?.
                        commit()
            }

        })
        rv_region.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
    }

    override fun onBackPressed() = BackHandlerHelper.handleBackPress(this)

}
