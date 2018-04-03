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
import com.shenrui.label.biaoqian.mvp.model.bean.PanelBean
import com.shenrui.label.biaoqian.mvp.model.bean.TXConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.WLConnectionBean
import com.shenrui.label.biaoqian.ui.adapter.PanelGLConnectionListItemRecyclerAdapter
import com.shenrui.label.biaoqian.ui.adapter.PanelTXConnectionListItemRecyclerAdapter
import com.shenrui.label.biaoqian.ui.adapter.PanelWLConnectionListItemRecyclerAdapter
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import kotlinx.android.synthetic.main.fragment_panel.*
import kotlinx.android.synthetic.main.title_layout.*
import org.jetbrains.anko.support.v4.toast
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class PanelFragment : BaseFragment(), FragmentBackHandler {

    private var mPath: String? = null
    private var mPanelBean: PanelBean? = null

    private val mWLConnectionList: ArrayList<WLConnectionBean> by lazy {
        ArrayList<WLConnectionBean>()
    }
    private val mGLConnectionList: ArrayList<GLConnectionBean> by lazy {
        ArrayList<GLConnectionBean>()
    }
    private val mTXConnectionList: ArrayList<TXConnectionBean> by lazy {
        ArrayList<TXConnectionBean>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mPath = arguments!!.getString(ARG_PARAM1)
            mPanelBean = arguments!!.getParcelable(ARG_PARAM2)
        }
    }

    override fun getLayoutId() = R.layout.fragment_panel

    @SuppressLint("SetTextI18n")
    override fun initView() {
        tv_title.text = mPanelBean?.panel_name + "(" + mPanelBean?.panel_code + ")"

        tv_panel_name.text = mPanelBean?.panel_name + "(" + mPanelBean?.panel_code + ")"

        img_back.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun lazyLoad() {
        val progressDialog = ProgressDialog.show(activity, null, "正在查询数据...", false, false)
        progressDialog.show()

        Observable.create(Observable.OnSubscribe<ArrayList<TXConnectionBean>> {
            //得到数据库中所有的屏柜
            val panelDataList = DataBaseUtil.getPanel(mPath!!)
            //获取所有尾缆
            val tailFiberDataList = DataBaseUtil.getTailFiber(mPath!!)

            val deviceList = mPanelBean?.device
            val deviceConnection = ArrayList<DeviceConnection>()
            //刷选出设备相关的设备连接情况
            val deviceDateList = DataBaseUtil.getDevice(mPath!!)
            //得到数据库中所有的设备连接
            val deviceDataConnectionList = DataBaseUtil.getDeviceConnection(mPath!!)

            //交换机
            val switchList = mPanelBean?.switch
            val switchDataConnection = DataBaseUtil.getSwitchConnection(mPath!!)
            val switchConnection = ArrayList<SwitchConnection>()
            val switchDateList = DataBaseUtil.getSwitch(mPath!!)

            //筛选出当前屏柜中所有设备的连接情况
            deviceList?.forEach { item ->
                deviceDataConnectionList.forEach {
                    if (it.from_id == item.device_id) {
                        deviceConnection.add(it)
                        Log.e("-----", "-----DeviceConnection=$it")
                    }
                }
            }
            //根据每条连接线判断是否是WL还是跳纤TX
            deviceConnection.forEach {
                //如果是连接的是设备
                if (it.to_dev_type == "1001") {
                    //帅选出这条连线的to设备
                    val device = deviceDateList.filter { item ->
                        item.device_id == it.to_id
                    }
                    //如果to设备的panelId等于当前屏柜的id，说明这条deviceConnection是跳纤，如果不是就是尾缆（WL）
                    if (device[0].panel_id == mPanelBean?.panel_id) {
                        val inDevice = deviceList?.filter { item ->
                            it.from_id == item.device_id
                        }
                        val tailFiberTx = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRx = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }
                        mTXConnectionList.add(TXConnectionBean(inDevice!![0].device_iedname, it.from_port + "/Tx",
                                tailFiberTx[0].tail_cable_number, it.to_port.toString() + "/Rx",
                                device[0].device_desc, tailFiberTx[0].tail_fiber_desc))
                        mTXConnectionList.add(TXConnectionBean(inDevice[0].device_iedname, it.from_port + "/Rx",
                                tailFiberRx[0].tail_cable_number, it.to_port.toString() + "/Tx",
                                device[0].device_desc, tailFiberRx[0].tail_fiber_desc))

                    } else {
                        //找到这条连线连接的外部屏柜panel
                        val panel = panelDataList.filter {
                            it.panel_id == device[0].panel_id
                        }
                        val tailFiberWL = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        //找到尾缆
                        mWLConnectionList.add(WLConnectionBean(panel[0].panel_id.toString(), tailFiberWL[0].tail_cable_number, panel[0].panel_name))
                    }
                } else if (it.to_dev_type == "1000") {
                    //帅选出这条连线的to设备
                    val switch = switchDateList.filter { item ->
                        item.switch_id == it.to_id
                    }
                    //如果to设备的panelId等于当前屏柜的id，说明这条deviceConnection是跳纤，如果不是就是尾缆（WL）
                    if (switch[0].panel_id == mPanelBean?.panel_id) {
                        val inDevice = deviceList?.filter { item ->
                            it.from_id == item.device_id
                        }
                        val tailFiberTx = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRx = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }

                        mTXConnectionList.add(TXConnectionBean(inDevice!![0].device_desc, it.from_port + "/Tx",
                                tailFiberTx[0].tail_cable_number, it.to_port.toString() + "/Rx",
                                switch[0].switch_name, tailFiberTx[0].tail_fiber_desc))
                        mTXConnectionList.add(TXConnectionBean(inDevice[0].device_desc, it.from_port + "/Rx",
                                tailFiberRx[0].tail_cable_number, it.to_port.toString() + "/Tx",
                                switch[0].switch_name, tailFiberRx[0].tail_fiber_desc))

                    } else {
                        //找到这条连线连接的外部屏柜panel
                        val panel = panelDataList.filter {
                            it.panel_id == switch[0].panel_id
                        }
                        val tailFiberWL = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        //找到尾缆
                        mWLConnectionList.add(WLConnectionBean(panel[0].panel_id.toString(), tailFiberWL[0].tail_cable_number, panel[0].panel_name))
                    }
                }
            }

            //帅选出当前屏柜中所有设备的连接情况
            switchList?.forEach { item ->
                switchDataConnection.forEach {
                    if (it.from_id == item.switch_id) {
                        switchConnection.add(it)
                        Log.e("-----", "-----DeviceConnection=$it")
                    }
                }
            }
            //根据每条连接线判断是否是WL还是跳纤TX
            switchConnection.forEach {
                //如果连接到的设备是交换机
                if (it.to_dev_type == "1000") {
                    //帅选出这条连线的to设备
                    val switch = switchDateList.filter { item ->
                        item.switch_id == it.to_id
                    }
                    //如果to设备的panelId等于当前屏柜的id，说明这条deviceConnection是跳纤，如果不是就是尾缆（WL）
                    if (switch[0].panel_id == mPanelBean?.panel_id) {
                        val inSwitch = switchList?.filter { item ->
                            it.from_id == item.switch_id
                        }
                        val tailFiberTx = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRx = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }
                        mTXConnectionList.add(TXConnectionBean(inSwitch!![0].switch_name, it.from_port + "/Tx",
                                tailFiberTx[0].tail_cable_number, it.to_port + "/Rx",
                                switch[0].switch_name, tailFiberTx[0].tail_fiber_desc))
                        mTXConnectionList.add(TXConnectionBean(inSwitch[0].switch_name, it.from_port + "/Rx",
                                tailFiberRx[0].tail_cable_number, it.to_port + "/Tx",
                                switch[0].switch_name, tailFiberRx[0].tail_fiber_desc))

                    } else {
                        //找到这条连线连接的外部屏柜panel
                        val panel = panelDataList.filter {
                            it.panel_id == switch[0].panel_id
                        }
                        val tailFiberWL = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        //找到尾缆
                        mWLConnectionList.add(WLConnectionBean(panel[0].panel_id.toString(), tailFiberWL[0].tail_cable_number, panel[0].panel_name))
                    }
                } else if (it.to_dev_type == "1001") { //如果连接到的设备是装置

                    //帅选出这条连线的to设备
                    val device = deviceDateList.filter { item ->
                        item.device_id == it.to_id
                    }
                    //如果to设备的panelId等于当前屏柜的id，说明这条deviceConnection是跳纤，如果不是就是尾缆（WL）
                    if (device[0].panel_id == mPanelBean?.panel_id) {
                        val inSwitch = switchList?.filter { item ->
                            it.from_id == item.switch_id
                        }
                        val tailFiberTx = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRx = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }
                        mTXConnectionList.add(TXConnectionBean(inSwitch!![0].switch_name, it.from_port + "/Tx",
                                tailFiberTx[0].tail_cable_number, it.to_port + "/Rx",
                                device[0].device_desc, tailFiberTx[0].tail_fiber_desc))
                        mTXConnectionList.add(TXConnectionBean(inSwitch[0].switch_name, it.from_port + "/Rx",
                                tailFiberRx[0].tail_cable_number, it.to_port + "/Tx",
                                device[0].device_desc, tailFiberRx[0].tail_fiber_desc))

                    } else {
                        //找到这条连线连接的外部屏柜panel
                        val panel = panelDataList.filter {
                            it.panel_id == device[0].panel_id
                        }
                        val tailFiberWL = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        //找到尾缆
                        mWLConnectionList.add(WLConnectionBean(panel[0].panel_id.toString(), tailFiberWL[0].tail_cable_number, panel[0].panel_name))
                    }
                }
            }

//            it.onNext(panelList)
            it.onCompleted()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<TXConnectionBean>>() {
                    override fun onCompleted() {
//                        toast("成功读取数据库")
                        progressDialog.dismiss()
                        initRecycleView()
                    }

                    override fun onError(e: Throwable) {
                        toast("读取数据库失败，请检查数据库是否存在")
                        progressDialog.dismiss()
                    }

                    override fun onNext(dataList: ArrayList<TXConnectionBean>) {
                        dataList.forEach {
                            Log.e("-----", "-----PanelBean=$it")
                        }
                    }
                })

    }

    /**
     * 获取到数据后初始化RecyclerView
     */
    private fun initRecycleView() {
        if (activity == null) {
            return
        }

        //尾缆Recycleriew
        val wlAdapter = PanelWLConnectionListItemRecyclerAdapter(activity!!, mWLConnectionList,
                object : PanelWLConnectionListItemRecyclerAdapter.WLConnectionClickListener {
                    override fun onWLConnectionItemClick(item: WLConnectionBean) {
                        activity?.supportFragmentManager?.beginTransaction()?.add(R.id.content_frame, WLConnectionFragment.newInstance(mPath!!, item.wlName))?.addToBackStack("WLConnectionFragment")?.commit()
                    }
                })
        rv_panel_wl.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = wlAdapter
        }

        //光缆Recycleriew
        val glAdapter = PanelGLConnectionListItemRecyclerAdapter(activity!!, mGLConnectionList,
                object : PanelGLConnectionListItemRecyclerAdapter.GLConnectionClickListener {
                    override fun onGLConnectionItemClick(item: GLConnectionBean) {
                        activity?.supportFragmentManager?.beginTransaction()?.add(R.id.content_frame, GLConnectionFragment.newInstance(mPath!!, item.glName))?.addToBackStack("DeviceFragment")?.commit()
                    }
                })
        rv_panel_gl.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = glAdapter
        }

        //TX链接图
        val txAdapter = PanelTXConnectionListItemRecyclerAdapter(activity!!, mTXConnectionList)
        rv_tx_connection.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = txAdapter
        }

    }

    override fun onBackPressed() = BackHandlerHelper.handleBackPress(this)

    companion object {
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        fun newInstance(param1: String, param2: PanelBean): PanelFragment {
            val fragment = PanelFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putParcelable(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
