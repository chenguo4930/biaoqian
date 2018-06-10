package com.shenrui.label.biaoqian.ui.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.luckongo.tthd.mvp.model.bean.DeviceConnection
import com.luckongo.tthd.mvp.model.bean.ODF
import com.luckongo.tthd.mvp.model.bean.ODFConnection
import com.luckongo.tthd.mvp.model.bean.SwitchConnection
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.constrant.AllSubStation
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.mvp.model.bean.*
import com.shenrui.label.biaoqian.ui.adapter.PanelDLConnectionListItemRecyclerAdapter
import com.shenrui.label.biaoqian.ui.adapter.PanelGLConnectionListItemRecyclerAdapter
import com.shenrui.label.biaoqian.ui.adapter.PanelTXConnectionListItemRecyclerAdapter
import com.shenrui.label.biaoqian.ui.adapter.PanelWLConnectionListItemRecyclerAdapter
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import com.shenrui.label.biaoqian.utils.Util
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_panel.*
import kotlinx.android.synthetic.main.title_layout.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import kotlin.math.max


class PanelFragment : BaseFragment(), FragmentBackHandler {

    private var mPath: String? = null
    private var mPanelBean: PanelBean? = null
    private var mWLAdapterSize = 0
    private var mGLAdapterSize = 0
    private var mDLAdapterSize = 0

    private val mWLConnectionList: ArrayList<WLConnectionBean> by lazy {
        ArrayList<WLConnectionBean>()
    }
    private val mGLConnectionList: ArrayList<GLConnectionBean> by lazy {
        ArrayList<GLConnectionBean>()
    }
    private val mTXConnectionList: ArrayList<TXConnectionBean> by lazy {
        ArrayList<TXConnectionBean>()
    }

    private val mDLConnectionList: ArrayList<DLConnectionBean> by lazy {
        ArrayList<DLConnectionBean>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mPath = arguments!!.getString(AllSubStation.PARAM_1)
            mPanelBean = arguments!!.getParcelable(AllSubStation.PARAM_2)
        }
    }

    override fun getLayoutId() = R.layout.fragment_panel

    @SuppressLint("SetTextI18n")
    override fun initView() {
        tv_back_title.text = mPanelBean?.panel_name + "(" + mPanelBean?.panel_code + ")"

        tv_panel_name.text = mPanelBean?.panel_name + "(" + mPanelBean?.panel_code + ")"

        tv_right.run {
            text = "切换到电缆连接图"
            visibility = View.VISIBLE
            onClick {
                if (text == "切换到电缆连接图") {
                    text = "切换到尾缆、光缆、跳纤连接图"
                    hintWLLayout()
                } else {
                    text = "切换到电缆连接图"
                    hintDLLayout()
                }
            }
        }

        img_back.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    /**
     * 显示电缆的连接图
     */
    private fun hintWLLayout() {
        cv_tx_connect_layout.visibility = View.GONE
        rv_panel_wl.visibility = View.INVISIBLE
        rv_panel_gl.visibility = View.GONE
        rv_panel_dl.visibility = View.VISIBLE
        setDLPanelHeight()
    }

    /**
     * 显示尾缆 光缆的连接图
     */
    private fun hintDLLayout() {
        cv_tx_connect_layout.visibility = View.VISIBLE
        rv_panel_wl.visibility = View.VISIBLE
        rv_panel_gl.visibility = View.VISIBLE
        rv_panel_dl.visibility = View.GONE
        setWLGLPanelHeight()
    }

    override fun lazyLoad() {
        val progressDialog = ProgressDialog.show(activity, null, "正在查询数据...", false, false)
        progressDialog.show()

        Observable.create(ObservableOnSubscribe<String> {
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

            //解析WeiLan数据和跳纤数据------------------start-----------------------

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
                    val toDevice = deviceDateList.filter { item ->
                        item.device_id == it.to_id
                    }
                    val inDevice = deviceList?.filter { item ->
                        it.from_id == item.device_id
                    }

                    //如果to设备的panelId等于当前屏柜的id，说明这条deviceConnection是跳纤，如果不是就是尾缆（WL）
                    if (toDevice[0].panel_id == mPanelBean?.panel_id) {

                        val tailFiberTx = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRx = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }
                        if (mTXConnectionList.none { it.tailCableNumber == tailFiberTx[0].tail_cable_number }) {
                            mTXConnectionList.add(TXConnectionBean(inDevice!![0].device_desc, inDevice[0].device_id,
                                    inDevice[0].device_iedname, toDevice[0].device_desc, toDevice[0].device_id,
                                    toDevice[0].device_iedname, "Tx", it.from_port, it.to_port,
                                    tailFiberTx[0].tail_cable_number, tailFiberTx[0].tail_fiber_desc,
                                    "1001",
                                    "1001"))
                        }
                        if (mTXConnectionList.none { it.tailCableNumber == tailFiberRx[0].tail_cable_number }) {
                            mTXConnectionList.add(TXConnectionBean(inDevice!![0].device_desc, inDevice[0].device_id,
                                    inDevice[0].device_iedname, toDevice[0].device_desc, toDevice[0].device_id,
                                    toDevice[0].device_iedname, "Rx", it.from_port, it.to_port,
                                    tailFiberRx[0].tail_cable_number, tailFiberRx[0].tail_fiber_desc,
                                    "1001",
                                    "1001"))
                        }
                    } else {
                        //找到这条连线连接的外部屏柜panel
                        val panel = panelDataList.filter {
                            it.panel_id == toDevice[0].panel_id
                        }
                        val tailFiberTxWL = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRxWL = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }
                        //找到尾缆
                        val wlTxBean = WLConnectionBean("Tx", tailFiberTxWL[0], panel[0].panel_name,
                                inDevice!![0], it, null, null, toDevice[0], null)
                        val wlRxBean = WLConnectionBean("Rx", tailFiberRxWL[0], panel[0].panel_name,
                                inDevice[0], it, null, null, toDevice[0], null)
                        mWLConnectionList.add(wlTxBean)
                        mWLConnectionList.add(wlRxBean)
                    }
                } else if (it.to_dev_type == "1000") {
                    //帅选出这条连线的to设备
                    val toSwitch = switchDateList.filter { item ->
                        item.switch_id == it.to_id
                    }
                    val inDevice = deviceList?.filter { item ->
                        it.from_id == item.device_id
                    }

                    //如果to设备的panelId等于当前屏柜的id，说明这条deviceConnection是跳纤，如果不是就是尾缆（WL）
                    if (toSwitch[0].panel_id == mPanelBean?.panel_id) {
                        val tailFiberTx = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRx = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }
                        if (mTXConnectionList.none { it.tailCableNumber == tailFiberTx[0].tail_cable_number }) {
                            mTXConnectionList.add(TXConnectionBean(inDevice!![0].device_desc, inDevice[0].device_id,
                                    inDevice[0].device_iedname, toSwitch[0].switch_name, toSwitch[0].switch_id,
                                    toSwitch[0].switch_code, "Tx", it.from_port, it.to_port,
                                    tailFiberTx[0].tail_cable_number, tailFiberTx[0].tail_fiber_desc,
                                    "1001",
                                    "1000"))
                        }
                        if (mTXConnectionList.none { it.tailCableNumber == tailFiberRx[0].tail_cable_number }) {
                            mTXConnectionList.add(TXConnectionBean(inDevice!![0].device_desc, inDevice[0].device_id,
                                    inDevice[0].device_iedname, toSwitch[0].switch_name, toSwitch[0].switch_id,
                                    toSwitch[0].switch_code, "Rx", it.from_port, it.to_port,
                                    tailFiberRx[0].tail_cable_number, tailFiberRx[0].tail_fiber_desc,
                                    "1001",
                                    "1000"))
                        }
                    } else {
                        //找到这条连线连接的外部屏柜panel
                        val panel = panelDataList.filter {
                            it.panel_id == toSwitch[0].panel_id
                        }
                        val tailFiberTxWL = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRxWL = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }

                        //找到尾缆
                        val wlTxBean = WLConnectionBean("Tx", tailFiberTxWL[0], panel[0].panel_name,
                                inDevice!![0], it, null, null, null, toSwitch[0])
                        val wlRxBean = WLConnectionBean("Rx", tailFiberRxWL[0], panel[0].panel_name,
                                inDevice[0], it, null, null, null, toSwitch[0])

                        mWLConnectionList.add(wlTxBean)
                        mWLConnectionList.add(wlRxBean)
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
                    val toSwitch = switchDateList.filter { item ->
                        item.switch_id == it.to_id
                    }
                    val inSwitch = switchList?.filter { item ->
                        it.from_id == item.switch_id
                    }

                    //如果to设备的panelId等于当前屏柜的id，说明这条deviceConnection是跳纤，如果不是就是尾缆（WL）
                    if (toSwitch[0].panel_id == mPanelBean?.panel_id) {

                        val tailFiberTx = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRx = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }
                        if (mTXConnectionList.none { it.tailCableNumber == tailFiberTx[0].tail_cable_number }) {
                            mTXConnectionList.add(TXConnectionBean(inSwitch!![0].switch_name, inSwitch[0].switch_id,
                                    inSwitch[0].switch_code, toSwitch[0].switch_name, toSwitch[0].switch_id,
                                    toSwitch[0].switch_code, "Tx", it.from_port, it.to_port,
                                    tailFiberTx[0].tail_cable_number, tailFiberTx[0].tail_fiber_desc,
                                    "1000",
                                    "1000"))
                        }
                        if (mTXConnectionList.none { it.tailCableNumber == tailFiberRx[0].tail_cable_number }) {
                            mTXConnectionList.add(TXConnectionBean(inSwitch!![0].switch_name, inSwitch[0].switch_id,
                                    inSwitch[0].switch_code, toSwitch[0].switch_name, toSwitch[0].switch_id,
                                    toSwitch[0].switch_code, "Rx", it.from_port, it.to_port,
                                    tailFiberRx[0].tail_cable_number, tailFiberRx[0].tail_fiber_desc,
                                    "1000",
                                    "1000"))
                        }
                    } else {
                        //找到这条连线连接的外部屏柜panel
                        val panel = panelDataList.filter {
                            it.panel_id == toSwitch[0].panel_id
                        }
                        val tailFiberTxWL = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRxWL = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }
                        //找到尾缆
                        val wlTxBean = WLConnectionBean("Tx", tailFiberTxWL[0], panel[0].panel_name,
                                null, null, inSwitch!![0], it, null, toSwitch[0])
                        val wlRxBean = WLConnectionBean("Rx", tailFiberRxWL[0], panel[0].panel_name,
                                null, null, inSwitch[0], it, null, toSwitch[0])
                        mWLConnectionList.add(wlTxBean)
                        mWLConnectionList.add(wlRxBean)
                    }
                } else if (it.to_dev_type == "1001") { //如果连接到的设备是装置

                    //帅选出这条连线的to设备
                    val toDevice = deviceDateList.filter { item ->
                        item.device_id == it.to_id
                    }
                    val inSwitch = switchList?.filter { item ->
                        it.from_id == item.switch_id
                    }
                    //如果to设备的panelId等于当前屏柜的id，说明这条deviceConnection是跳纤，如果不是就是尾缆（WL）
                    if (toDevice[0].panel_id == mPanelBean?.panel_id) {
                        val tailFiberTx = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRx = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }
                        if (mTXConnectionList.none { it.tailCableNumber == tailFiberTx[0].tail_cable_number }) {
                            mTXConnectionList.add(TXConnectionBean(inSwitch!![0].switch_name, inSwitch[0].switch_id,
                                    inSwitch[0].switch_code, toDevice[0].device_desc, toDevice[0].device_id,
                                    toDevice[0].device_iedname, "Tx", it.from_port, it.to_port,
                                    tailFiberTx[0].tail_cable_number, tailFiberTx[0].tail_fiber_desc,
                                    "1000",
                                    "1001"))
                        }
                        if (mTXConnectionList.none { it.tailCableNumber == tailFiberTx[0].tail_cable_number }) {
                            mTXConnectionList.add(TXConnectionBean(inSwitch!![0].switch_name, inSwitch[0].switch_id,
                                    inSwitch[0].switch_code, toDevice[0].device_desc, toDevice[0].device_id,
                                    toDevice[0].device_iedname, "Rx", it.from_port, it.to_port,
                                    tailFiberRx[0].tail_cable_number, tailFiberRx[0].tail_fiber_desc,
                                    "1000",
                                    "1001"))
                        }
                    } else {
                        //找到这条连线连接的外部屏柜panel
                        val panel = panelDataList.filter {
                            it.panel_id == toDevice[0].panel_id
                        }
                        val tailFiberTxWL = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRxWL = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }
                        //找到尾缆
                        val wlTxBean = WLConnectionBean("Tx", tailFiberTxWL[0], panel[0].panel_name,
                                null, null, inSwitch!![0], it, toDevice[0], null)
                        mWLConnectionList.add(wlTxBean)
                        val wlRxBean = WLConnectionBean("Rx", tailFiberRxWL[0], panel[0].panel_name,
                                null, null, inSwitch[0], it, toDevice[0], null)
                        mWLConnectionList.add(wlRxBean)
                    }
                }
            }
            //解析WeiLan数据和跳纤数据------------------end-----------------------

            //解析光缆数据-------------------------start-----------------------
            val odfDataList = DataBaseUtil.getODF(mPath!!)
            val odfConnectionDataList = DataBaseUtil.getODFConnection(mPath!!)
            //筛选出屏柜中的所有odf
            val odfList = ArrayList<ODF>()
            odfDataList.forEach {
                if (it.panel_id == mPanelBean?.panel_id) {
                    odfList.add(it)
                }
            }
            //帅选出所有odf的连接信息
            odfList.forEach out@{
                odfConnectionDataList.forEach { item ->
                    if (item.odf_id == it.odf_id) {
                        var inDeviceName = ""
                        var inDeviceId = ""
                        var inDeviceCode = ""
                        var outDeviceName = ""
                        var outDeviceId = ""
                        var outDeviceCode = ""
                        var outPanelName = ""
                        var outODF: ODF? = null
                        var outODFConnection: ODFConnection? = null

                        for (bean in odfConnectionDataList) {
                            if (bean.odf_id == item.external_odf_id) {
                                //获取外部连接的odf和odfConnection
                                outODFConnection = bean
                                for (odf in odfDataList) {
                                    if (odf.odf_id == item.external_odf_id) {
                                        outODF = odf
                                        break
                                    }
                                }

                                if (bean.internal_device_type == 1001) {
                                    //如果外部连接的设备是装置
                                    for (device in deviceDateList) {
                                        if (device.device_id == bean.internal_device_id) {
                                            outDeviceName = device.device_desc
                                            outDeviceId = device.device_id.toString()
                                            outDeviceCode = device.device_iedname
                                            for (panel in panelDataList) {
                                                if (panel.panel_id == device.panel_id) {
                                                    outPanelName = panel.panel_name
                                                    break
                                                }
                                            }
                                            break
                                        }
                                    }
                                } else if (bean.internal_device_type == 1000) {
                                    //如果外部连接的设备是交换机
                                    for (switch in switchDateList) {
                                        if (switch.switch_id == bean.internal_device_id) {
                                            outDeviceName = switch.switch_name
                                            outDeviceId = switch.switch_id.toString()
                                            outDeviceCode = switch.switch_code
                                            for (panel in panelDataList) {
                                                if (panel.panel_id == switch.panel_id) {
                                                    outPanelName = panel.panel_name
                                                    break
                                                }
                                            }
                                            break
                                        }
                                    }
                                } else {
                                    return@out
                                }
                                break
                            }
                        }

                        //获取in设备的名称
                        if (item.internal_device_type == 1001) {
                            //如果设备是装置
                            for (device in deviceDateList) {
                                if (device.device_id == item.internal_device_id) {
                                    inDeviceName = device.device_desc
                                    inDeviceId = device.device_id.toString()
                                    inDeviceCode = device.device_iedname
                                    break
                                }
                            }
                        } else if (item.internal_device_type == 1000) {
                            //如果设备是交换机
                            for (switch in switchDateList) {
                                if (switch.switch_id == item.internal_device_id) {
                                    inDeviceName = switch.switch_name
                                    inDeviceId = switch.switch_id.toString()
                                    inDeviceCode = switch.switch_code
                                    break
                                }
                            }
                        } else {
                            return@out
                        }

                        mGLConnectionList.add(GLConnectionBean(inDeviceName, inDeviceId, inDeviceCode,
                                outDeviceName, outDeviceId, outDeviceCode, outPanelName,
                                it, item, outODF!!, outODFConnection!!))
                        return@out
                    }
                }
            }
            Log.e("--------", "----------odfDataList.size() = ${mGLConnectionList.size}-")
            //解析光缆数据-------------------------end-----------------------

            //解析电缆数据-------------------------start-----------------------
            val terminalPortDataList = DataBaseUtil.getTerminalPort(mPath!!).filter { it.cable_no != "" }
            val terminalPortList = terminalPortDataList.filter { it.panel_id == mPanelBean!!.panel_id }
            terminalPortList.forEach { item ->
                val terminalToBean = terminalPortDataList.filter { it.id == item.external_terminal_port_id }
                if (terminalToBean.isEmpty()) {
                    return@forEach
                }
                val fromPanel = panelDataList.filter { it.panel_id == item.panel_id }
                val toPanel = panelDataList.filter { it.panel_id == terminalToBean[0].panel_id }
                val fromDevice = DataBaseUtil.getDeviceByPanelByDeviceId(mPath!!, fromPanel[0].panel_id, item.internal_device_id)
                val toDevice = DataBaseUtil.getDeviceByPanelByDeviceId(mPath!!, toPanel[0].panel_id, terminalToBean[0].internal_device_id)
                val fromPortType = if (item.internal_port_type == 0) "Rx" else "Tx"
                val toPortType = if (terminalToBean[0].internal_port_type == 0) "Rx" else "Tx"

                mDLConnectionList.add(DLConnectionBean(
                        fromPanel[0].panel_name,
                        fromDevice[0].device_desc,
                        item.internal_signal_description,
                        item.internal_device_port + "/" + fromPortType,
                        item.port_no.toString() + "-" + item.cable_no,
                        toPanel[0].panel_name,
                        toDevice[0].device_desc,
                        terminalToBean[0].internal_signal_description,
                        terminalToBean[0].internal_device_port + "/" + toPortType,
                        terminalToBean[0].port_no.toString() + "-" + terminalToBean[0].cable_no,
                        item.cable_no,
                        item.cable_core_no,
                        item.internal_port_type))
            }

            //解析电缆数据-------------------------end-----------------------
            it.onComplete()

        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onComplete() {
//                        toast("成功读取数据库")
                        progressDialog.dismiss()
                        initRecycleView()
                    }

                    override fun onError(e: Throwable) {
                        toast("读取数据库失败，请检查数据库是否存在")
                        progressDialog.dismiss()
                    }

                    override fun onNext(dataList: String) {

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

        //----------------尾缆Recycleriew--------------
        //去除重复的尾缆
        val wlConnectionList = ArrayList<WLConnectionBean>()
        mWLConnectionList.forEach {
            for (bean in wlConnectionList) {
                if (bean.wlTailFiber.tail_cable_number == it.wlTailFiber.tail_cable_number) {
                    return@forEach
                }
            }
            wlConnectionList.add(it)
        }
        mWLAdapterSize = wlConnectionList.size
        val wlAdapter = PanelWLConnectionListItemRecyclerAdapter(activity!!, wlConnectionList,
                object : PanelWLConnectionListItemRecyclerAdapter.WLConnectionClickListener {
                    override fun onWLConnectionItemClick(item: WLConnectionBean) {
                        val wlList = mWLConnectionList.filter {
                            it.wlTailFiber.tail_cable_number == item.wlTailFiber.tail_cable_number
                        }

                        activity?.supportFragmentManager?.beginTransaction()
                                ?.add(R.id.content_frame, WLConnectionFragment.newInstance(mPath!!, wlList as ArrayList<WLConnectionBean>))
                                ?.addToBackStack("WLConnectionFragment")
                                ?.commit()
                    }
                })
        rv_panel_wl.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = wlAdapter
        }

        //--------------光缆Recycleriew----------------
        //去除光缆重复的
        val glConnectionList = ArrayList<GLConnectionBean>()
        mGLConnectionList.forEach {
            for (bean in glConnectionList) {
                if (bean.odfConnection.optical_cable_number == it.odfConnection.optical_cable_number) {
                    return@forEach
                }
            }
            glConnectionList.add(it)
        }
        mGLAdapterSize = glConnectionList.size
        val glAdapter = PanelGLConnectionListItemRecyclerAdapter(activity!!, glConnectionList,
                object : PanelGLConnectionListItemRecyclerAdapter.GLConnectionClickListener {
                    override fun onGLConnectionItemClick(item: GLConnectionBean) {
                        val glList = mGLConnectionList.filter {
                            it.odfConnection.optical_cable_number == item.odfConnection.optical_cable_number
                        }
                        activity?.supportFragmentManager?.beginTransaction()
                                ?.add(R.id.content_frame, GLConnectionFragment.newInstance(mPath!!, glList as ArrayList<GLConnectionBean>))
                                ?.addToBackStack("DeviceFragment")
                                ?.commit()
                    }
                })
        rv_panel_gl.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = glAdapter
        }

        //--------------TX链接图------------------
        val txAdapter = PanelTXConnectionListItemRecyclerAdapter(activity!!, mTXConnectionList,
                object : PanelTXConnectionListItemRecyclerAdapter.TXConnectionClickListener {
                    override fun onTXConnectionItemClick(item: TXConnectionBean) {
                        activity?.supportFragmentManager?.beginTransaction()
                                ?.add(R.id.content_frame, ConnectionFragment.newInstance(mPath!!, null, null, item))
                                ?.addToBackStack("ConnectionFragment")
                                ?.commit()
                    }
                })
        rv_tx_connection.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = txAdapter
        }

        //--------------DL链接图------------------
        //去除电缆重复的
        val dlConnectionList = ArrayList<DLConnectionBean>()
        mDLConnectionList.forEach {
            for (bean in dlConnectionList) {
                if (bean.cableNo == it.cableNo) {
                    return@forEach
                }
            }
            dlConnectionList.add(it)
        }
        mDLAdapterSize = dlConnectionList.size
        val dlAdapter = PanelDLConnectionListItemRecyclerAdapter(activity!!, dlConnectionList,
                object : PanelDLConnectionListItemRecyclerAdapter.DLConnectionClickListener {
                    override fun onDLConnectionItemClick(item: DLConnectionBean) {
                        val dlList = mDLConnectionList.filter { item.cableNo == it.cableNo }

                        activity?.supportFragmentManager?.beginTransaction()
                                ?.add(R.id.content_frame, DLConnectionFragment.newInstance(mPath!!, dlList as ArrayList<DLConnectionBean>))
                                ?.addToBackStack("ConnectionFragment")
                                ?.commit()
                    }
                })
        rv_panel_dl.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = dlAdapter
        }

        setWLGLPanelHeight()
    }

    /**
     * 当光缆和尾缆其中某个数据长度大于了4条，就动态增加中间屏屏柜的高度
     */
    private fun setWLGLPanelHeight() {
        val maxSizeWLGL = max(mWLAdapterSize, mGLAdapterSize)
        if (maxSizeWLGL > 4) {
            cv_panel_name.apply {
                layoutParams.height = Util.dip2px(activity!!, 50 * maxSizeWLGL)
            }
        } else {
            cv_panel_name.apply {
                layoutParams.height = Util.dip2px(activity!!, 200)
            }
        }
    }

    /**
     * 当电缆其中某个数据长度大于了4条，就动态增加中间屏屏柜的高度
     */
    private fun setDLPanelHeight() {
        if (mDLAdapterSize > 4) {
            cv_panel_name.apply {
                layoutParams.height = Util.dip2px(activity!!, 50 * mDLAdapterSize)
            }
        } else {
            cv_panel_name.apply {
                layoutParams.height = Util.dip2px(activity!!, 200)
            }
        }
    }

    override fun onBackPressed() = BackHandlerHelper.handleBackPress(this)

    companion object {

        fun newInstance(param1: String, param2: PanelBean): PanelFragment {
            val fragment = PanelFragment()
            val args = Bundle()
            args.putString(AllSubStation.PARAM_1, param1)
            args.putParcelable(AllSubStation.PARAM_2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
