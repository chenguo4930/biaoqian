package com.shenrui.label.biaoqian.ui.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.mvp.model.bean.WLConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.WLConnectionDetailBean
import com.shenrui.label.biaoqian.ui.adapter.WLConnectionListItemRecyclerAdapter
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import kotlinx.android.synthetic.main.fragment_wl_connection.*
import kotlinx.android.synthetic.main.title_layout.*
import org.jetbrains.anko.support.v4.toast
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
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
//        val progressDialog = ProgressDialog.show(activity, null, "正在查询数据...", false, false)
//        progressDialog.show()
//
//        Observable.create(Observable.OnSubscribe<String> {
//            //得到数据库中所有的屏柜
////            val panelDataList = DataBaseUtil.getPanel(mPath!!)
//
//            //刷选出设备相关的设备连接情况
//            val deviceDateList = DataBaseUtil.getDevice(mPath!!)
//            //得到数据库中所有的设备连接
//            val deviceDataConnectionList = DataBaseUtil.getDeviceConnection(mPath!!)
//
//            //交换机
//            val switchDateList = DataBaseUtil.getSwitch(mPath!!)
//            val switchDataConnectionList = DataBaseUtil.getSwitchConnection(mPath!!)
//
//            //数据库中的尾缆
//            val tailFiberDataList = DataBaseUtil.getTailFiber(mPath!!)
//            //筛选出尾缆mWLName
//            val wlList = tailFiberDataList.filter {
//                it.tail_cable_number == mWLName
//            }
//
//            wlList.forEach { item ->
//
//                val deviceConnectionList = deviceDataConnectionList.filter {
//                    it.tail_fiber_tx_id == item.tail_fiber_id
//                }
//                //如果设备连接中没有数据，说明这条WL连接数据在交换机中
//                if (deviceConnectionList.isNotEmpty()) {
//                    val inDevice = deviceDateList.filter {
//                        it.device_id == deviceConnectionList[0].from_id
//                    }
//                    if (deviceConnectionList[0].to_dev_type == "1001") {
//                        //如果连接的设备是装置，就到装置数据库中查找
//                        val toDevice = deviceDateList.filter {
//                            it.device_id == deviceConnectionList[0].to_id
//                        }
//                        mWLConnectionList.add(WLConnectionDetailBean(inDevice[0].device_desc,
//                                deviceConnectionList[0].from_port + "/Tx", item.tail_fiber_number,
//                                deviceConnectionList[0].to_port.toString() + "/Rx",
//                                toDevice[0].device_desc, item.tail_fiber_desc))
//                        //先是Tx,然后再是Rx
////                        mWLConnectionList.add(WLConnectionDetailBean(inDevice[0].device_desc,
////                                deviceConnectionList[0].from_port + "/Rx", item.tail_fiber_number,
////                                deviceConnectionList[0].to_port.toString() + "/Tx",
////                                toDevice[0].device_desc, item.tail_fiber_desc))
//                    } else if (deviceConnectionList[0].to_dev_type == "1000") {
//                        //如果连接的设备是交换机，就到交换机数据库中查找
//                        val toSwitch = switchDateList.filter {
//                            it.switch_id == deviceConnectionList[0].to_id
//                        }
//                        mWLConnectionList.add(WLConnectionDetailBean(inDevice[0].device_desc,
//                                deviceConnectionList[0].from_port + "/Tx", item.tail_fiber_number,
//                                deviceConnectionList[0].to_port.toString() + "/Rx",
//                                toSwitch[0].switch_name, item.tail_fiber_desc))
//                    }
//                } else {
//                    val switchConnectionList = switchDataConnectionList.filter {
//                        it.tail_fiber_tx_id == item.tail_fiber_id
//                    }
//
//                    val inSwitch = switchDateList.filter {
//                        it.switch_id == switchConnectionList[0].from_id
//                    }
//                    if (switchConnectionList[0].to_dev_type == "1001") {
//                        //如果连接的设备是装置，就到装置数据库中查找
//                        val toDevice = deviceDateList.filter {
//                            it.device_id == deviceConnectionList[0].to_id
//                        }
//                        mWLConnectionList.add(WLConnectionDetailBean(inSwitch[0].switch_name,
//                                switchConnectionList[0].from_port + "/Tx", item.tail_fiber_number,
//                                switchConnectionList[0].to_port.toString() + "/Rx",
//                                toDevice[0].device_desc, item.tail_fiber_desc))
//
//                    } else if (deviceConnectionList[0].to_dev_type == "1000") {
//                        //如果连接的设备是交换机，就到交换机数据库中查找
//                        val toSwitch = switchDateList.filter {
//                            it.switch_id == deviceConnectionList[0].to_id
//                        }
//
//                        mWLConnectionList.add(WLConnectionDetailBean(inSwitch[0].switch_name,
//                                switchConnectionList[0].from_port + "/Tx", item.tail_fiber_number,
//                                switchConnectionList[0].to_port.toString() + "/Rx",
//                                toSwitch[0].switch_name, item.tail_fiber_desc))
//                    }
//                }
//            }
////            it.onNext(panelList)
//            it.onCompleted()
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : Subscriber<String>() {
//                    override fun onCompleted() {
////                        toast("成功读取数据库")
//                        progressDialog.dismiss()
//                        initRecycleView()
//                    }
//
//                    override fun onError(e: Throwable) {
//                        toast("读取数据库失败，请检查数据库是否存在")
//                        progressDialog.dismiss()
//                    }
//
//                    override fun onNext(str: String) {
//
//                    }
//                })
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
        val txAdapter = WLConnectionListItemRecyclerAdapter(activity!!, mWLConnectionList!!)
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
