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
import com.shenrui.label.biaoqian.mvp.model.bean.DLConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.GLConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.TXConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.WLConnectionBean
import com.shenrui.label.biaoqian.mvp.presenter.BiaoQianPresenter
import com.shenrui.label.biaoqian.ui.fragment.*
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import com.uuzuche.lib_zxing.activity.CodeUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_biao_qian.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem
import org.jetbrains.anko.toast


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
        home_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_homepage_pressed, null))
        setting_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_set_nor, null))
        scan_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_scan_nor, null))
    }

    /**
     * 首页被点击
     */
    private fun setSettingPressed() {
        home_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_homepage_nor, null))
        setting_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_set_pressed, null))
        scan_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_scan_nor, null))
        //关掉当前Activity，回到主页
        finish()
    }

    /**
     * 首页被点击
     */
    private fun setScanPressed() {
        home_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_homepage_nor, null))
        setting_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_set_nor, null))
        scan_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_scan_pressed, null))
    }

    /**
     * 打开默认二维码扫描界面
     */
    private fun goToScanActivity() {
        val intent = Intent(this@BiaoQianActivity, MyCaptureActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
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
                val result = bundle?.getString(CodeUtils.RESULT_STRING)
                toast("解析结果:$result")
                logE("---------解析结果:$result---")
                analysisResult(result)
            }
        }
    }

    /**
     * 解析扫描二维码的结果
     * result ：光缆和尾缆,电缆二维码结构 ： JSNJ22TSB/GL1101/2N
     * result ：尾缆的纤芯二维码结构 ： JSNJ22TSB/WL1101-2/2N/3n/10/BTx
     * result ：跳纤缆二维码结构 ：     JSNJ22TSB/2N-TX-01/2N/3n/7/ATx
     *
     *  ---------- 下面为最新格式，上面的格式废弃了 ----------
     *
     * 光缆编号、尾缆编号、电缆编号：JSNT50RDB/GL2201/1A
     * 尾缆纤芯编号：JSNT50RDB/WL2201-1/1A/1n/10/FTx
     * 跳纤编号：JSNT50RDB/1A-TX-01/1n/10/ETx
     */
    private fun analysisResult(result: String?) {
        if (result == null) {
            toast("扫描数据为空，请检查二维码是否有效。")
            return
        }
        val resultArray = result.split("/")
        if (resultArray.size == 3 || resultArray.size == 6 || resultArray.size == 5) {
            var subStationName = ""
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
            if (mDbPath == null) {
                toast("数据库中没有找到对应的数据库文件")
                return
            }

            //根据panel编号来查找panel
            val panelList = if (resultArray.size == 5) {
                DataBaseUtil.getPanelByCode(mDbPath!!, resultArray[1].split("-")[0])
            } else {
                DataBaseUtil.getPanelByCode(mDbPath!!, resultArray[2])
            }

            if (panelList.isEmpty()) {
                toast("数据库中没有找到对应的屏柜,请检查二维码是否正确")
                return
            }
            logE("------------根据panel编号来查找panel=${panelList[0]}-------")
            val panelId = panelList[0].panel_id

            when (resultArray.size) {
                3 -> //解析长度为3说明是光缆和尾缆,电缆的二维码   JSNJ22TSB/GL1101/2N
                    when {
                        DataBaseUtil.searchTailFiber(mDbPath!!, resultArray[1]) -> //如果是尾缆
                            searchWLData(resultArray[1], panelId)
                        DataBaseUtil.searchODFConnection(mDbPath!!, resultArray[1]) -> //如果是光缆
                            searchGLData(resultArray[1], panelId)
                        DataBaseUtil.searchTerminalPort(mDbPath!!, resultArray[1]) -> { //如果是电缆
                            searchDLData(resultArray[1], panelId)
                        }
                    }
                6 -> //                if (resultArray[1].contains("-TX-").not()) {
                    // 解析长度为6说明是尾缆的纤芯和跳纤二维码  JSNJ22TSB/WL1101-2/2N/3n/10/BTx
                    // 二维码详情： No:WL1101-2  From: 2N/3n/10/BTx To:3N/4-2n/10/BRx JSNT50FHB/4E-WL-4132A-1/34E1/1-40n/1/BTx
                    searchWLXXData(resultArray[1], panelId)
                5 -> {
                    // 跳纤缆二维码结构 ：     JSNT50RDB/1A-TX-01/1n/10/ETx   1A：屏柜Code  TX-01:跳纤
                    // 二维码详情： No:2N-Tx-01  From: 3n/7/ATx  To:1n/1/1Rx
                    val txValue = resultArray[1].split("-")
                    if (txValue.size != 3) {
                        toast("跳纤二维码的跳纤格式不正确，应为：2N-TX-01 这种格式")
                        return
                    }
                    val txName = txValue[1] + "-" + txValue[2]
                    searchTXXXData(txName, panelId)
                }
            }
        } else {
            toast("二维码数据格式有误")
        }
    }

    /**
     * 获取跳纤纤芯数据 TX-01
     */
    private fun searchTXXXData(txName: String, panelId: Int) {
        logE("-----------跳纤的名称txName=$txName----屏柜panelId=$panelId")
        val progressDialog = ProgressDialog.show(this, null, "正在查询数据...", false, false)
        progressDialog.show()

        Observable.create(ObservableOnSubscribe<TXConnectionBean> {
            //该屏柜的所有跳纤连接集合
            val txConnectionList = ArrayList<TXConnectionBean>()

            //得到数据库中所有的屏柜
//            val panelDataList = DataBaseUtil.getPanel(mDbPath!!)
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

            //解析跳纤数据------------------start-----------------------

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
                        if (txConnectionList.none { it.tailCableNumber == tailFiberTx[0].tail_cable_number }) {
                            txConnectionList.add(TXConnectionBean(inDevice[0].device_desc, inDevice[0].device_id,
                                    inDevice[0].device_iedname, toDevice[0].device_desc, toDevice[0].device_id,
                                    toDevice[0].device_iedname, "Tx", it.from_port, it.to_port,
                                    tailFiberTx[0].tail_cable_number, tailFiberTx[0].tail_fiber_desc,
                                    "1001",
                                    "1001"))
                        }
                        if (txConnectionList.none { it.tailCableNumber == tailFiberRx[0].tail_cable_number }) {
                            txConnectionList.add(TXConnectionBean(inDevice[0].device_desc, inDevice[0].device_id,
                                    inDevice[0].device_iedname, toDevice[0].device_desc, toDevice[0].device_id,
                                    toDevice[0].device_iedname, "Rx", it.from_port, it.to_port,
                                    tailFiberRx[0].tail_cable_number, tailFiberRx[0].tail_fiber_desc,
                                    "1001",
                                    "1001"))
                        }
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
                        if (txConnectionList.none { it.tailCableNumber == tailFiberTx[0].tail_cable_number }) {
                            txConnectionList.add(TXConnectionBean(inDevice[0].device_desc, inDevice[0].device_id,
                                    inDevice[0].device_iedname, toSwitch[0].switch_name, toSwitch[0].switch_id,
                                    toSwitch[0].switch_code, "Tx", it.from_port, it.to_port,
                                    tailFiberTx[0].tail_cable_number, tailFiberTx[0].tail_fiber_desc,
                                    "1001",
                                    "1000"))
                        }
                        if (txConnectionList.none { it.tailCableNumber == tailFiberRx[0].tail_cable_number }) {
                            txConnectionList.add(TXConnectionBean(inDevice[0].device_desc, inDevice[0].device_id,
                                    inDevice[0].device_iedname, toSwitch[0].switch_name, toSwitch[0].switch_id,
                                    toSwitch[0].switch_code, "Rx", it.from_port, it.to_port,
                                    tailFiberRx[0].tail_cable_number, tailFiberRx[0].tail_fiber_desc,
                                    "1001",
                                    "1000"))
                        }
                    }
                }
            }

            //帅选出当前屏柜中所有设备的连接情况
            switchList.forEach { item ->
                switchDataConnection.forEach {
                    if (it.from_id == item.switch_id) {
                        switchConnection.add(it)
                        logE("-----DeviceConnection=$it")
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
                        if (txConnectionList.none { it.tailCableNumber == tailFiberTx[0].tail_cable_number }) {
                            txConnectionList.add(TXConnectionBean(inSwitch[0].switch_name, inSwitch[0].switch_id,
                                    inSwitch[0].switch_code, toSwitch[0].switch_name, toSwitch[0].switch_id,
                                    toSwitch[0].switch_code, "Tx", it.from_port, it.to_port,
                                    tailFiberTx[0].tail_cable_number, tailFiberTx[0].tail_fiber_desc,
                                    "1000",
                                    "1000"))
                        }
                        if (txConnectionList.none { it.tailCableNumber == tailFiberRx[0].tail_cable_number }) {
                            txConnectionList.add(TXConnectionBean(inSwitch[0].switch_name, inSwitch[0].switch_id,
                                    inSwitch[0].switch_code, toSwitch[0].switch_name, toSwitch[0].switch_id,
                                    toSwitch[0].switch_code, "Rx", it.from_port, it.to_port,
                                    tailFiberRx[0].tail_cable_number, tailFiberRx[0].tail_fiber_desc,
                                    "1000",
                                    "1000"))
                        }
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
                        if (txConnectionList.none { it.tailCableNumber == tailFiberTx[0].tail_cable_number }) {
                            txConnectionList.add(TXConnectionBean(inSwitch[0].switch_name, inSwitch[0].switch_id,
                                    inSwitch[0].switch_code, toDevice[0].device_desc, toDevice[0].device_id,
                                    toDevice[0].device_iedname, "Tx", it.from_port, it.to_port,
                                    tailFiberTx[0].tail_cable_number, tailFiberTx[0].tail_fiber_desc,
                                    "1000",
                                    "1001"))
                        }
                        if (txConnectionList.none { it.tailCableNumber == tailFiberRx[0].tail_cable_number }) {
                            txConnectionList.add(TXConnectionBean(inSwitch[0].switch_name, inSwitch[0].switch_id,
                                    inSwitch[0].switch_code, toDevice[0].device_desc, toDevice[0].device_id,
                                    toDevice[0].device_iedname, "Rx", it.from_port, it.to_port,
                                    tailFiberRx[0].tail_cable_number, tailFiberRx[0].tail_fiber_desc,
                                    "1000",
                                    "1001"))
                        }
                    }
                }
            }
            //解析跳纤数据------------------end-----------------------
            var txList: List<TXConnectionBean>? = null
            if (txConnectionList.isNotEmpty()) {
                txList = txConnectionList!!.filter {
                    it.tailCableNumber == txName
                }
            }

            if (txList != null && txList.isNotEmpty()) {
                logE("---------跳纤的纤芯数据-txList[0] = ${txList[0]}-")
                it.onNext(txList[0])
            } else {
                //如果没有找到直连的跳纤
                val odfList = DataBaseUtil.getODFByPanelId(mDbPath!!, panelId)
                val odfConnectionList = ArrayList<ODFConnection>()

                DataBaseUtil.getODFConnection(mDbPath!!).forEach {
                    odfList.forEach { item ->
                        if (item.odf_id == it.odf_id) {
                            odfConnectionList.add(it)
                        }
                    }
                }
                val odfConnectionBeanFrom = odfConnectionList.filter { it.internal_optical_fiber_number == txName }[0]
                val odfConnectionBeanTo = DataBaseUtil.getODFConnection(mDbPath!!).filter { it.odf_id == odfConnectionBeanFrom.external_odf_id }[0]
                val deviceFrom = if (odfConnectionBeanFrom.internal_device_type == 1001) {
                    deviceDateList.filter { it.device_id == odfConnectionBeanFrom.internal_device_id }[0]
                } else {
                    switchDateList.filter { it.switch_id == odfConnectionBeanFrom.internal_device_id }[0]
                }
                val deviceTo = if (odfConnectionBeanTo.internal_device_type == 1001) {
                    deviceDateList.filter { it.device_id == odfConnectionBeanTo.internal_device_id }[0]
                } else {
                    switchDateList.filter { it.switch_id == odfConnectionBeanTo.internal_device_id }[0]
                }

                val inputType = if (odfConnectionBeanFrom.internal_rt_type == 1) {
                    "Tx"
                } else {
                    "Rx"
                }
                val inType = if (odfConnectionBeanFrom.internal_device_type == 1001) {
                    "1001"
                } else {
                    "1000"
                }
                val toType = if (odfConnectionBeanTo.internal_device_type == 1001) {
                    "1001"
                } else {
                    "1000"
                }
                when {
                    deviceFrom is Device && deviceTo is Device -> {
                        it.onNext(TXConnectionBean(deviceFrom.device_desc, deviceFrom.device_id, deviceFrom.device_code,
                                deviceTo.device_desc, deviceTo.device_id, deviceTo.device_code,
                                inputType, odfConnectionBeanFrom.internal_device_port!!, odfConnectionBeanTo.internal_device_port!!,
                                null, null, inType, toType))
                    }
                    deviceFrom is Device && deviceTo is Switch -> {
                        it.onNext(TXConnectionBean(deviceFrom.device_desc, deviceFrom.device_id, deviceFrom.device_code,
                                deviceTo.switch_name, deviceTo.switch_id, deviceTo.switch_code,
                                inputType, odfConnectionBeanFrom.internal_device_port!!, odfConnectionBeanTo.internal_device_port!!,
                                null, null, inType, toType))
                    }
                    deviceFrom is Switch && deviceTo is Device -> {
                        it.onNext(TXConnectionBean(deviceFrom.switch_name, deviceFrom.switch_id, deviceFrom.switch_code,
                                deviceTo.device_desc, deviceTo.device_id, deviceTo.device_code,
                                inputType, odfConnectionBeanFrom.internal_device_port!!, odfConnectionBeanTo.internal_device_port!!,
                                null, null, inType, toType))
                    }
                    deviceFrom is Switch && deviceTo is Switch -> {
                        it.onNext(TXConnectionBean(deviceFrom.switch_name, deviceFrom.switch_id, deviceFrom.switch_code,
                                deviceTo.switch_name, deviceTo.switch_id, deviceTo.switch_code,
                                inputType, odfConnectionBeanFrom.internal_device_port!!, odfConnectionBeanTo.internal_device_port!!,
                                null, null, inType, toType))
                    }
                }

            }
            it.onComplete()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<TXConnectionBean> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onComplete() {
                        progressDialog.dismiss()
                    }

                    override fun onError(e: Throwable) {
                        toast("读取标签的二维码失败，请检查二维码配置是否正确")
                        progressDialog.dismiss()
                    }

                    override fun onNext(txBean: TXConnectionBean) {
                        supportFragmentManager?.beginTransaction()
                                ?.add(R.id.content_frame, ConnectionFragment.newInstance(mDbPath!!, null, null, txBean))
                                ?.addToBackStack("ConnectionFragment")
                                ?.commit()
                    }
                })
    }

    /**
     * 获取尾缆的纤芯二维码对应的数据
     *  connectioniName : WL1101-2
     */
    private fun searchWLXXData(connectionName: String, panelId: Int) {

        val progressDialog = ProgressDialog.show(this, null, "正在查询数据...", false, false)
        progressDialog.show()

        Observable.create(ObservableOnSubscribe<WLConnectionBean> {
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

            //解析WeiLan数据数据------------------start-----------------------

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
                    if (toDevice[0].panel_id != panelId) {

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
                    if (toSwitch[0].panel_id != panelId) {
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
                    if (toSwitch[0].panel_id != panelId) {
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
                    if (toDevice[0].panel_id != panelId) {
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
            //解析WeiLan数据数据------------------end-----------------------

            val connectionNameArray = connectionName.split("-")
            connectionNameArray.forEach {
                logE("-----------------connectionNameArray.is = $it-----")
            }
            val size = connectionNameArray.size
            var tailCableNumber = ""
            var tailFiberNumber = ""
            if (size == 2) {
                tailCableNumber = connectionNameArray[0]
                tailFiberNumber = connectionNameArray[1]
            } else {
                tailFiberNumber = connectionNameArray[size - 1]

                for (i in 0 until size - 1) {
                    tailCableNumber += connectionNameArray[i]
                    tailCableNumber += "-"
                }
                tailCableNumber = tailCableNumber.substring(0, tailCableNumber.length - 1)
            }

            logE("---------------tailCableNumber=$tailCableNumber")

            val wlList = wlConnectionList.filter {
                it.wlTailFiber.tail_cable_number == tailCableNumber
                        && it.wlTailFiber.tail_fiber_number.toString() == tailFiberNumber
            }
            logE("---------尾缆的纤芯数据-wlList[0] = ${wlList[0]}-")
            it.onNext(wlList[0])
            it.onComplete()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<WLConnectionBean> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onComplete() {
//                        toast("成功读取数据库")
                        progressDialog.dismiss()
                    }

                    override fun onError(e: Throwable) {
                        toast("读取标签的二维码失败，请检查二维码配置是否正确")
                        progressDialog.dismiss()
                    }

                    override fun onNext(wlBean: WLConnectionBean) {
                        supportFragmentManager?.beginTransaction()
                                ?.add(R.id.content_frame, ConnectionFragment.newInstance(mDbPath!!, wlBean, null, null))
                                ?.addToBackStack("ConnectionFragment")
                                ?.commit()
                    }
                })
    }

    /**
     * 查找尾缆
     */
    private fun searchWLData(connectionName: String, panelId: Int) {
        val progressDialog = ProgressDialog.show(this, null, "正在查询数据...", false, false)
        progressDialog.show()

        Observable.create(ObservableOnSubscribe<ArrayList<WLConnectionBean>> {
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
                    if (toDevice[0].panel_id != panelId) {
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
                    if (toSwitch[0].panel_id != panelId) {
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
                    if (toSwitch[0].panel_id != panelId) {
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
                    if (toDevice[0].panel_id != panelId) {

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
            it.onComplete()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ArrayList<WLConnectionBean>> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onComplete() {
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

        Observable.create(ObservableOnSubscribe<ArrayList<GLConnectionBean>> {

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
            it.onComplete()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ArrayList<GLConnectionBean>> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onComplete() {
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

    /**
     * 获取电缆链接数据
     */
    private fun searchDLData(dlName: String, panelId: Int) {
        val progressDialog = ProgressDialog.show(this, null, "正在查询数据...", false, false)
        progressDialog.show()

        Observable.create(ObservableOnSubscribe<ArrayList<DLConnectionBean>> {
            //得到数据库中所有的屏柜
            val panelDataList = DataBaseUtil.getPanel(mDbPath!!)
            val dLConnectionList = ArrayList<DLConnectionBean>()

            //解析电缆数据-------------------------start-----------------------
            val terminalPortDataList = DataBaseUtil.getTerminalPort(mDbPath!!).filter { it.cable_no != "" }
            val terminalPortList = terminalPortDataList.filter { it.panel_id == panelId }
            terminalPortList.forEach { item ->
                val terminalToBean = terminalPortDataList.filter { it.id == item.external_terminal_port_id }
                if (terminalToBean.isEmpty()) {
                    return@forEach
                }
                val fromPanel = panelDataList.filter { it.panel_id == item.panel_id }
                val toPanel = panelDataList.filter { it.panel_id == terminalToBean[0].panel_id }
                val fromDevice = DataBaseUtil.getDeviceByPanelByDeviceId(mDbPath!!, fromPanel[0].panel_id, item.internal_device_id)
                val toDevice = DataBaseUtil.getDeviceByPanelByDeviceId(mDbPath!!, toPanel[0].panel_id, terminalToBean[0].internal_device_id)
                val fromPortType = if (item.internal_port_type == 0) "Rx" else "Tx"
                val toPortType = if (terminalToBean[0].internal_port_type == 0) "Rx" else "Tx"

                dLConnectionList.add(DLConnectionBean(
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
            logE("---------电缆-dLConnectionList.size() = ${dLConnectionList.size}-")
            val dlList = dLConnectionList.filter { it.cableNo == dlName } as ArrayList<DLConnectionBean>
            logE("---------电缆-dlList.size() = ${dlList.size}-")
            it.onNext(dlList)
            it.onComplete()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ArrayList<DLConnectionBean>> {

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onComplete() {
                        progressDialog.dismiss()

                    }

                    override fun onError(e: Throwable) {
                        toast("读取数据库失败，请检查数据库是否存在")
                        progressDialog.dismiss()
                    }

                    override fun onNext(dataList: ArrayList<DLConnectionBean>) {
                        supportFragmentManager?.beginTransaction()
                                ?.add(R.id.content_frame, DLConnectionFragment.newInstance(mDbPath!!, dataList))
                                ?.addToBackStack("DLConnectionFragment")
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
