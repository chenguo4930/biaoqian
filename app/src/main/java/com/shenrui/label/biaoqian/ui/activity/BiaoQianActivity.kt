package com.shenrui.label.biaoqian.ui.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.luckongo.tthd.mvp.model.bean.*
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.constrant.AllSubStation.Companion.subStation
import com.shenrui.label.biaoqian.extension.logE
import com.shenrui.label.biaoqian.mvp.base.BaseActivity
import com.shenrui.label.biaoqian.mvp.contract.BiaoQianContract
import com.shenrui.label.biaoqian.mvp.model.bean.GLConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.PanelBean
import com.shenrui.label.biaoqian.mvp.model.bean.TXConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.WLConnectionBean
import com.shenrui.label.biaoqian.mvp.presenter.BiaoQianPresenter
import com.shenrui.label.biaoqian.ui.fragment.*
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import com.xys.libzxing.zxing.activity.CaptureActivity
import kotlinx.android.synthetic.main.activity_biao_qian.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem
import org.jetbrains.anko.toast
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class BiaoQianActivity : BaseActivity<BiaoQianContract.View,
        BiaoQianPresenter<BiaoQianContract.View>>(),
        BiaoQianContract.View {

    companion object {
        private const val REQUEST_CODE = 100
    }

    private var mHomeFragment: HomeFragment? = null
    private var mSettingFragment: SettingFragment? = null
    private var mScanFragment: ScanFragment? = null
    private var mDbPath: String? = null // 数据库路径

    override fun <E> onError(e: E?) {
    }

    override fun <D> onSuccess(d: D?) {
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
    }

    override fun layoutId() = R.layout.activity_biao_qian

    override fun createPresenter(): BiaoQianPresenter<BiaoQianContract.View>? {
        return BiaoQianPresenter()
    }

    override fun initData() {
        if (mHomeFragment == null) {
            mHomeFragment = HomeFragment()
        }
        supportFragmentManager.beginTransaction().replace(R.id.content_frame, mHomeFragment).commit()
        setHomePressed()
    }

    override fun initListener() {
        home_img.setOnClickListener {
            if (mHomeFragment == null) {
                mHomeFragment = HomeFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.content_frame, mHomeFragment).commit()
            setHomePressed()
        }

        setting_img.setOnClickListener {
            if (mSettingFragment == null) {
                mSettingFragment = SettingFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.content_frame, mSettingFragment).commit()
            setSettingPressed()
        }

        scan_img.setOnClickListener {
            //            if (mScanFragment == null){
//                mScanFragment = ScanFragment()
//            }
//            supportFragmentManager.beginTransaction().replace(R.id.content_frame,mScanFragment).commit()
//            setScanPressed()
            val permissionItems = ArrayList<PermissionItem>()
            permissionItems.add(PermissionItem(Manifest.permission.CAMERA, "开启摄像头", R.drawable.permission_ic_camera))
            HiPermission.create(this@BiaoQianActivity)
                    .permissions(permissionItems)
                    .checkMutiPermission(object : PermissionCallback {
                        override fun onFinish() {
                            goToScanActivity()
                        }

                        override fun onDeny(permission: String?, position: Int) {
                            toast("拒绝摄像头权限，无法进行扫描，请开启摄像头权限")
                        }

                        override fun onGuarantee(permission: String?, position: Int) {
                            goToScanActivity()
                        }

                        override fun onClose() {

                        }

                    })

        }
    }

    /**
     * 首页被点击
     */
    private fun setHomePressed() {
        home_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_homepage_pressed))
        setting_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_set_nor))
        scan_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_scan_nor))
    }

    /**
     * 首页被点击
     */
    private fun setSettingPressed() {
        home_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_homepage_nor))
        setting_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_set_pressed))
        scan_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_scan_nor))
        //关掉当前Activity，回到主页
        finish()
    }

    /**
     * 首页被点击
     */
    private fun setScanPressed() {
        home_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_homepage_nor))
        setting_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_set_nor))
        scan_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_scan_pressed))
    }

    /**
     * 打开默认二维码扫描界面
     */
    private fun goToScanActivity() {
        val intent = Intent(this@BiaoQianActivity, CaptureActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
//        Handler().postDelayed({
//            /**
//             * 只有当摄像头打开后才能，打开闪光灯
//             */
//            CodeUtils.isLightEnable(true)
//        }, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                val bundle: Bundle? = data.extras ?: return
                val result = bundle?.getString("result")
                analysisResult(result)
                toast("解析结果:$result")
            }
        }
    }

    /**
     * 解析扫描二维码的结果
     * result ：光缆和尾缆二维码结构 ： JSNJ22TSB/GL1101/2N
     * result ：尾缆的纤芯二维码结构 ： JSNJ22TSB/WL1101-2/2N/3n/10/BTx
     * result ：跳纤缆二维码结构 ：     JSNJ22TSB/2N-TX-01/2N/3n/7/ATx
     */
    private fun analysisResult(result: String?) {
        if (result == null) {
            toast("扫描数据为空，请检查二维码是否有效。")
            return
        }
        val resultArray = result.split("/")
        if (resultArray.size == 3 || resultArray.size == 6) {
            var subStationName: String = ""
            //变电站电压等级编号表JSNJ22TSB 22后面的TSB是变电站的简称，要把它解析出来
            when {
                resultArray[0].indexOf("75") != -1 -> {
                    subStationName = resultArray[0].substring(resultArray[0].indexOf("75") + 2)
                }
                resultArray[0].indexOf("50") != -1 -> {
                    subStationName = resultArray[0].substring(resultArray[0].indexOf("50") + 2)
                }
                resultArray[0].indexOf("33") != -1 -> {
                    subStationName = resultArray[0].substring(resultArray[0].indexOf("33") + 2)
                }
                resultArray[0].indexOf("22") != -1 -> {
                    subStationName = resultArray[0].substring(resultArray[0].indexOf("22") + 2)
                }
                resultArray[0].indexOf("11") != -1 -> {
                    subStationName = resultArray[0].substring(resultArray[0].indexOf("11") + 2)
                }
                resultArray[0].indexOf("66") != -1 -> {
                    subStationName = resultArray[0].substring(resultArray[0].indexOf("66") + 2)
                }
                resultArray[0].indexOf("35") != -1 -> {
                    subStationName = resultArray[0].substring(resultArray[0].indexOf("35") + 2)
                }
                resultArray[0].indexOf("10") != -1 -> {
                    subStationName = resultArray[0].substring(resultArray[0].indexOf("10") + 2)
                }
            }
            logE("----------变电站缩写subStationName = $subStationName")
            if (subStationName == "") {
                toast("二维码解析出变电站缩写为空，请检查二维码是否正确")
                return
            }

            //通过变电站缩写，从变电站集合中找出变电站，并获取到该变电站的数据库路径
            for (it in subStation!!) {
                if (it.sub_short_name == subStationName) {
                    mDbPath = it.db_path
                    break
                }
            }
            //根据panel编号来查找panel
            val panelList = DataBaseUtil.getPanelByCode(mDbPath!!, resultArray[2])
            if (panelList.isEmpty()) {
                toast("数据库中没有找到对应的屏柜,请检查二维码是否正确")
                return
            }
            logE("------------根据panel编号来查找panel=${panelList[0]}-------")
            val panelId = panelList[0].panel_id

            if (resultArray.size == 3) {
                //解析长度为3说明是光缆和尾缆的二维码   JSNJ22TSB/GL1101/2N
                if (resultArray[1].startsWith("WL")) {
                    //如果是尾缆
                    searchWLData(resultArray[1], panelId)
                } else if (resultArray[1].startsWith("GL")) {
                    //如果是光缆
                    searchGLData(resultArray[1], panelId)
                }
            } else if (resultArray.size == 6) {
                if (resultArray[1].startsWith("WL")) {
                    //解析长度为6说明是尾缆的纤芯和跳纤二维码  JSNJ22TSB/WL1101-2/2N/3n/10/BTx
                    // 二维码详情： No:WL1101-2  From: 2N/3n/10/BTx To:3N/4-2n/10/BRx

                } else {
                    //跳纤缆二维码结构 ：     JSNJ22TSB/2N-TX-01/2N/3n/7/ATx
                    // 二维码详情： No:2N-Tx-01  From: 3n/7/ATx  To:1n/1/1Rx

                }
            }
        } else {
            toast("二维码数据格式有误")
        }
    }

    /**
     * 查找尾缆
     */
    private fun searchWLData(connectionName: String, panelId: Int) {
        val progressDialog = ProgressDialog.show(this, null, "正在查询数据...", false, false)
        progressDialog.show()

        Observable.create(Observable.OnSubscribe<ArrayList<WLConnectionBean>> {
            //该屏柜的所有尾缆连接集合
            val wlConnectionList = ArrayList<WLConnectionBean>()

            //得到数据库中所有的屏柜
            val panelDataList = DataBaseUtil.getPanel(mDbPath!!)
            //获取所有尾缆
            val tailFiberDataList = DataBaseUtil.getTailFiber(mDbPath!!)
            //刷选出设备相关的设备连接情况
            val deviceDateList = DataBaseUtil.getDevice(mDbPath!!)
            //得到数据库中所有的设备连接
            val deviceDataConnectionList = DataBaseUtil.getDeviceConnection(mDbPath!!)
            //交换机
            val switchDateList = DataBaseUtil.getSwitch(mDbPath!!)
            val switchDataConnection = DataBaseUtil.getSwitchConnection(mDbPath!!)

            //----------找出屏柜中的所有设备和交换机------------
            //设备
            val deviceList = deviceDateList.filter {
                it.panel_id == panelId
            }
            val deviceConnection = ArrayList<DeviceConnection>()

            //交换机
            val switchList = switchDateList.filter {
                it.panel_id == panelId
            }
            val switchConnection = ArrayList<SwitchConnection>()

            //解析WeiLan数据和跳纤数据------------------start-----------------------

            //筛选出当前屏柜中所有设备的连接情况
            deviceList.forEach { item ->
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
                    val inDevice = deviceList.filter { item ->
                        it.from_id == item.device_id
                    }

                    //如果to设备的panelId等于当前屏柜的id，说明这条deviceConnection是跳纤，如果不是就是尾缆（WL）
                    if (toDevice[0].panel_id == panelId) {

                        val tailFiberTx = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRx = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }
//                        mTXConnectionList.add(TXConnectionBean(inDevice!![0].device_desc, it.from_port + "/Tx",
//                                tailFiberTx[0].tail_cable_number, it.to_port.toString() + "/Rx",
//                                toDevice[0].device_desc, tailFiberTx[0].tail_fiber_desc))
//                        mTXConnectionList.add(TXConnectionBean(inDevice[0].device_desc, it.from_port + "/Rx",
//                                tailFiberRx[0].tail_cable_number, it.to_port.toString() + "/Tx",
//                                toDevice[0].device_desc, tailFiberRx[0].tail_fiber_desc))

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
                                inDevice[0], it, null, null, toDevice[0], null)
                        val wlRxBean = WLConnectionBean("Rx", tailFiberRxWL[0], panel[0].panel_name,
                                inDevice[0], it, null, null, toDevice[0], null)
                        wlConnectionList.add(wlTxBean)
                        wlConnectionList.add(wlRxBean)
                    }
                } else if (it.to_dev_type == "1000") {
                    //帅选出这条连线的to设备
                    val toSwitch = switchDateList.filter { item ->
                        item.switch_id == it.to_id
                    }
                    val inDevice = deviceList.filter { item ->
                        it.from_id == item.device_id
                    }

                    //如果to设备的panelId等于当前屏柜的id，说明这条deviceConnection是跳纤，如果不是就是尾缆（WL）
                    if (toSwitch[0].panel_id == panelId) {
                        val tailFiberTx = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRx = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }

//                        mTXConnectionList.add(TXConnectionBean(inDevice!![0].device_desc, it.from_port + "/Tx",
//                                tailFiberTx[0].tail_cable_number, it.to_port.toString() + "/Rx",
//                                toSwitch[0].switch_name, tailFiberTx[0].tail_fiber_desc))
//                        mTXConnectionList.add(TXConnectionBean(inDevice[0].device_desc, it.from_port + "/Rx",
//                                tailFiberRx[0].tail_cable_number, it.to_port.toString() + "/Tx",
//                                toSwitch[0].switch_name, tailFiberRx[0].tail_fiber_desc))

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
                                inDevice[0], it, null, null, null, toSwitch[0])
                        val wlRxBean = WLConnectionBean("Rx", tailFiberRxWL[0], panel[0].panel_name,
                                inDevice[0], it, null, null, null, toSwitch[0])

                        wlConnectionList.add(wlTxBean)
                        wlConnectionList.add(wlRxBean)
                    }
                }
            }

            //帅选出当前屏柜中所有设备的连接情况
            switchList.forEach { item ->
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
                    val inSwitch = switchList.filter { item ->
                        it.from_id == item.switch_id
                    }

                    //如果to设备的panelId等于当前屏柜的id，说明这条deviceConnection是跳纤，如果不是就是尾缆（WL）
                    if (toSwitch[0].panel_id == panelId) {

                        val tailFiberTx = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRx = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }
//                        mTXConnectionList.add(TXConnectionBean(inSwitch!![0].switch_name, it.from_port + "/Tx",
//                                tailFiberTx[0].tail_cable_number, it.to_port + "/Rx",
//                                toSwitch[0].switch_name, tailFiberTx[0].tail_fiber_desc))
//                        mTXConnectionList.add(TXConnectionBean(inSwitch[0].switch_name, it.from_port + "/Rx",
//                                tailFiberRx[0].tail_cable_number, it.to_port + "/Tx",
//                                toSwitch[0].switch_name, tailFiberRx[0].tail_fiber_desc))

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
                                null, null, inSwitch[0], it, null, toSwitch[0])
                        val wlRxBean = WLConnectionBean("Rx", tailFiberRxWL[0], panel[0].panel_name,
                                null, null, inSwitch[0], it, null, toSwitch[0])
                        wlConnectionList.add(wlTxBean)
                        wlConnectionList.add(wlRxBean)
                    }
                } else if (it.to_dev_type == "1001") { //如果连接到的设备是装置

                    //帅选出这条连线的to设备
                    val toDevice = deviceDateList.filter { item ->
                        item.device_id == it.to_id
                    }
                    val inSwitch = switchList.filter { item ->
                        it.from_id == item.switch_id
                    }
                    //如果to设备的panelId等于当前屏柜的id，说明这条deviceConnection是跳纤，如果不是就是尾缆（WL）
                    if (toDevice[0].panel_id == panelId) {
                        val tailFiberTx = tailFiberDataList.filter { item ->
                            it.tail_fiber_tx_id == item.tail_fiber_id
                        }
                        val tailFiberRx = tailFiberDataList.filter { item ->
                            it.tail_fiber_rx_id == item.tail_fiber_id
                        }
//                        mTXConnectionList.add(TXConnectionBean(inSwitch!![0].switch_name, it.from_port + "/Tx",
//                                tailFiberTx[0].tail_cable_number, it.to_port + "/Rx",
//                                toDevice[0].device_desc, tailFiberTx[0].tail_fiber_desc))
//                        mTXConnectionList.add(TXConnectionBean(inSwitch[0].switch_name, it.from_port + "/Rx",
//                                tailFiberRx[0].tail_cable_number, it.to_port + "/Tx",
//                                toDevice[0].device_desc, tailFiberRx[0].tail_fiber_desc))


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
                                null, null, inSwitch[0], it, toDevice[0], null)
                        wlConnectionList.add(wlTxBean)
                        val wlRxBean = WLConnectionBean("Rx", tailFiberRxWL[0], panel[0].panel_name,
                                null, null, inSwitch[0], it, toDevice[0], null)
                        wlConnectionList.add(wlRxBean)
                    }
                }
            }
            //解析WeiLan数据和跳纤数据------------------end-----------------------

            val wlList = wlConnectionList.filter {
                it.wlTailFiber.tail_cable_number == connectionName
            }
            logE("---------尾缆-wlList.size() = ${wlList.size}-")
            it.onNext(wlList as ArrayList<WLConnectionBean>)
            it.onCompleted()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<WLConnectionBean>>() {
                    override fun onCompleted() {
//                        toast("成功读取数据库")
                        progressDialog.dismiss()
                    }

                    override fun onError(e: Throwable) {
                        toast("读取数据库失败，请检查数据库是否存在")
                        progressDialog.dismiss()
                    }

                    override fun onNext(dataList: ArrayList<WLConnectionBean>) {
                        supportFragmentManager?.beginTransaction()
                                ?.add(R.id.content_frame, WLConnectionFragment.newInstance(mDbPath!!, dataList))
                                ?.addToBackStack("WLConnectionFragment")
                                ?.commit()
                    }
                })
    }

    /**
     * 查找光缆
     */
    private fun searchGLData(connectionName: String, panelId: Int) {
        val progressDialog = ProgressDialog.show(this, null, "正在查询数据...", false, false)
        progressDialog.show()

        Observable.create(Observable.OnSubscribe<ArrayList<GLConnectionBean>> {

            //得到数据库中所有的屏柜
            val panelDataList = DataBaseUtil.getPanel(mDbPath!!)
            //获取数据库中所有设备和交换机
            val deviceDateList = DataBaseUtil.getDevice(mDbPath!!)
            val switchDateList = DataBaseUtil.getSwitch(mDbPath!!)

            //解析光缆数据-------------------------start-----------------------
            val odfDataList = DataBaseUtil.getODF(mDbPath!!)
            val odfConnectionDataList = DataBaseUtil.getODFConnection(mDbPath!!)
            //筛选出屏柜中的所有odf
            val odfList = odfDataList.filter {
                it.panel_id == panelId
            }

            val gLConnectionList = ArrayList<GLConnectionBean>()
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

                        gLConnectionList.add(GLConnectionBean(inDeviceName, inDeviceId, inDeviceCode,
                                outDeviceName, outDeviceId, outDeviceCode, outPanelName,
                                it, item, outODF!!, outODFConnection!!))
                        return@out
                    }
                }
            }
            logE("---------光缆-odfDataList.size() = ${gLConnectionList.size}-")
            val glList = gLConnectionList.filter { it.odfConnection.optical_cable_number == connectionName }
            logE("---------光缆-glList.size() = ${glList.size}-")
            it.onNext(glList as ArrayList<GLConnectionBean>)
            it.onCompleted()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<GLConnectionBean>>() {
                    override fun onCompleted() {
                        progressDialog.dismiss()
                    }

                    override fun onError(e: Throwable) {
                        toast("读取数据库失败，请检查数据库是否存在")
                        progressDialog.dismiss()
                    }

                    override fun onNext(dataList: ArrayList<GLConnectionBean>) {
                        supportFragmentManager?.beginTransaction()
                                ?.add(R.id.content_frame, GLConnectionFragment.newInstance(mDbPath!!, dataList))
                                ?.addToBackStack("DeviceFragment")
                                ?.commit()
                    }
                })

    }

    override fun onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed()
        }
    }
}
