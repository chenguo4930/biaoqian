package com.shenrui.label.biaoqian.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.luckongo.tthd.mvp.model.bean.Inputs
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.constrant.AllSubStation.Companion.PARAM_1
import com.shenrui.label.biaoqian.constrant.AllSubStation.Companion.PARAM_2
import com.shenrui.label.biaoqian.constrant.AllSubStation.Companion.PARAM_3
import com.shenrui.label.biaoqian.constrant.AllSubStation.Companion.PARAM_4
import com.shenrui.label.biaoqian.extension.logE
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.mvp.model.bean.ConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.GLConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.TXConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.WLConnectionBean
import com.shenrui.label.biaoqian.ui.adapter.ConnectionListItem2RecyclerAdapter
import com.shenrui.label.biaoqian.ui.adapter.ConnectionListItemRecyclerAdapter
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import com.shenrui.label.biaoqian.utils.Util
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_gx_connection.*
import kotlinx.android.synthetic.main.title_layout.*
import org.jetbrains.anko.support.v4.toast

/**
 * 纤芯点击后，显示设备间具体的连接信息
 */
class ConnectionFragment : BaseFragment(), FragmentBackHandler {

    companion object {

        fun newInstance(dbPath: String, wlBean: WLConnectionBean?, glBean: GLConnectionBean?, txBean: TXConnectionBean?): ConnectionFragment {
            val fragment = ConnectionFragment()
            val args = Bundle()
            args.putString(PARAM_1, dbPath)
            args.putParcelable(PARAM_2, wlBean)
            args.putParcelable(PARAM_3, glBean)
            args.putParcelable(PARAM_4, txBean)
            fragment.arguments = args
            return fragment
        }
    }

    private var mDbPath: String? = null
    private var mWLBean: WLConnectionBean? = null
    private var mGLBean: GLConnectionBean? = null
    private var mTXBean: TXConnectionBean? = null

    //连线的数据List
    private val mConnectionList = ArrayList<Inputs>()
    //跳纤链接是设备与交换机相连的情况，1对n（1对多）
    private val mConnectionList2 = ArrayList<ConnectionBean>()

    private lateinit var mAdapter: ConnectionListItemRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mDbPath = arguments!!.getString(PARAM_1)
            mWLBean = arguments!!.getParcelable(PARAM_2)
            mGLBean = arguments!!.getParcelable(PARAM_3)
            mTXBean = arguments!!.getParcelable(PARAM_4)
        }
    }

    override fun getLayoutId() = R.layout.fragment_gx_connection

    override fun initView() {

    }

    override fun lazyLoad() {
        img_back.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
//        tv_back_title.text = mSubStationName
        if (mDbPath.isNullOrEmpty()) {
            toast("数据库路径为空")
        } else {
            Log.e("----", "---------mDbPath:$mDbPath")
            initText()
            initData()
        }
    }

    /**
     * 初始化文本显示内容
     *  1、两边都为装置
     *  2、一边为装置、另一边为交换机
     *  3、两边都为交换机不处理
     */
    private fun initText() {
        tv_back_title.text = "虚端子连接情况"

        //----------如果是尾缆连线点击进来------------
        if (mWLBean != null) {
            logE("-----------ConnectionFragment---mWLBean = $mWLBean-")
            if (mWLBean!!.inDevice != null) {
                tvInDeviceName.text = mWLBean!!.inDevice!!.device_desc
                tvInModeTitle.text = mWLBean!!.inDevice!!.device_iedname
            } else {
                tvInDeviceName.text = mWLBean!!.inSwitch!!.switch_name
                tvInModeTitle.text = mWLBean!!.inSwitch!!.switch_code
            }
            if (mWLBean!!.toDevice != null) {
                tvOutDeviceName.text = mWLBean!!.toDevice!!.device_desc
                tvOutModeTitle.text = mWLBean!!.toDevice!!.device_iedname
            } else {
                tvOutDeviceName.text = mWLBean!!.toSwitch!!.switch_name
                tvOutModeTitle.text = mWLBean!!.toSwitch!!.switch_code
            }

            if (mWLBean!!.inDevice != null && mWLBean!!.toSwitch != null
                    || mWLBean!!.inSwitch != null && mWLBean!!.toDevice != null) {
                //其中一边为装置，另外一边为交换机
                connectionRV.visibility = View.GONE
                outDeviceCard.visibility = View.GONE
                connectionTXRV.visibility = View.VISIBLE
                if (mWLBean!!.inDevice != null && mWLBean!!.toSwitch != null) {
                    tvInDeviceName.text = mWLBean!!.inDevice!!.device_desc
                    tvInModeTitle.text = mWLBean!!.inDevice!!.device_iedname
                } else if (mWLBean!!.inSwitch != null && mWLBean!!.toDevice != null) {
                    tvInDeviceName.text = mWLBean!!.toDevice!!.device_desc
                    tvInModeTitle.text = mWLBean!!.toDevice!!.device_iedname
                }
            }
        }

        //----------如果是光缆连线点击进来------------
        if (mGLBean != null) {
            logE("-----------ConnectionFragment---mGLBean = $mGLBean-")
            tvInDeviceName.text = mGLBean!!.inDeviceName
            tvInModeTitle.text = mGLBean!!.inDeviceCode
            tvOutDeviceName.text = mGLBean!!.outDeviceName
            tvOutModeTitle.text = mGLBean!!.outDeviceCode
            if (mGLBean!!.odfConnection.internal_device_type == 1001 && mGLBean!!.odfOutConnection.internal_device_type == 1000
                    || mGLBean!!.odfConnection.internal_device_type == 1000 && mGLBean!!.odfOutConnection.internal_device_type == 1001) {
                //其中一边为装置，另外一边为交换机
                connectionRV.visibility = View.GONE
                outDeviceCard.visibility = View.GONE
                connectionTXRV.visibility = View.VISIBLE
                if (mGLBean!!.odfConnection.internal_device_type == 1001 && mGLBean!!.odfOutConnection.internal_device_type == 1000) {
                    tvInDeviceName.text = mGLBean!!.inDeviceName
                    tvInModeTitle.text = mGLBean!!.inDeviceCode
                } else if (mGLBean!!.odfConnection.internal_device_type == 1000 && mGLBean!!.odfOutConnection.internal_device_type == 1001) {
                    tvInDeviceName.text = mGLBean!!.outDeviceName
                    tvInModeTitle.text = mGLBean!!.outDeviceCode
                }
            }

        }

        //----------如果是跳纤连线点击进来------------
        if (mTXBean != null) {
            logE("-----------ConnectionFragment---mTXBean = $mTXBean-")
            tvInDeviceName.text = mTXBean!!.inDeviceName
            tvInModeTitle.text = mTXBean!!.inDeviceCode
            tvOutDeviceName.text = mTXBean!!.outDeviceName
            tvOutModeTitle.text = mTXBean!!.outDeviceCode
            if (mTXBean!!.inType == "1001" && mTXBean!!.toType == "1000"
                    || mTXBean!!.inType == "1000" && mTXBean!!.toType == "1001") {
                //其中一边为装置，另外一边为交换机
                connectionRV.visibility = View.GONE
                outDeviceCard.visibility = View.GONE
                connectionTXRV.visibility = View.VISIBLE
                if (mTXBean!!.inType == "1001") {
                    tvInDeviceName.text = mTXBean!!.inDeviceName
                    tvInModeTitle.text = mTXBean!!.inDeviceCode
                } else if (mTXBean!!.inType == "1000" && mTXBean!!.toType == "1001") {
                    tvInDeviceName.text = mTXBean!!.outDeviceName
                    tvInModeTitle.text = mTXBean!!.outDeviceCode
                }
            }
        }
    }

    private fun initData() {
        val progressDialog = ProgressDialog.show(activity, null, "正在查询数据...", false, false)
        progressDialog.show()

        Observable.create(ObservableOnSubscribe<String> {
            //数据库数据
            val switchDataConnection = DataBaseUtil.getSwitchConnection(mDbPath!!)
            val deviceDateList = DataBaseUtil.getDevice(mDbPath!!)
            val odfConnectionDataList = DataBaseUtil.getODFConnection(mDbPath!!)

            mConnectionList.clear()

            //--------------------尾缆连线点击进来-----start----------------------
            if (mWLBean != null) {
                if (mWLBean!!.inDevice != null && mWLBean!!.toDevice != null) {
                    //输出装置的id，输入装置的id
                    val inModelId = mWLBean!!.inDevice!!.device_id.toString()
                    //输出端口Tx
                    val inModelPort = mWLBean!!.inDeviceConnection!!.from_port
                    val toModelId = mWLBean!!.toDevice!!.device_id.toString()
                    //输入端口Rx
                    val toModelPort = mWLBean!!.inDeviceConnection!!.to_port

                    //从数据库中获取inModel 到outModel的连接信息
                    //从数据库中获取outModel 到intModel的连接信息
                    Log.e("----", "---------mWLBean:$mWLBean")

                    if (mWLBean!!.type == "Tx") {
                        // 发 Tx
                        val txList = DataBaseUtil.getInputsFilter(mDbPath!!, inModelId, toModelId, toModelPort)
                        mConnectionList.addAll(txList)
                        mConnectionList.forEach { it.isInput = true }
                    } else {
                        // 收 Rx
                        val rxList = DataBaseUtil.getInputsFilter(mDbPath!!, toModelId, inModelId, inModelPort)
                        mConnectionList.addAll(rxList)
                        mConnectionList.forEach { it.isInput = false }
                    }

                } else if (mWLBean!!.inDevice != null && mWLBean!!.toSwitch != null) {
                    if (mWLBean!!.type == "Tx") {
                        //发 Tx
                        DataBaseUtil.getInputsFilterFrom(mDbPath!!, mWLBean!!.inDevice!!.device_id.toString())
                                .forEach { item ->
                                    val toDevice = deviceDateList.filter { it.device_id == item.model_id_to }
                                    item.isInput = true

                                    if (switchDataConnection.none { item.port_to == it.to_port }.not()) {
                                        //如果在交换机连接里面找到了，说明是连的交换机
                                        if (mConnectionList2.none { toDevice[0].device_desc == it.outDeviceName }) {
                                            val inputList = ArrayList<Inputs>()
                                            inputList.add(item)
                                            mConnectionList2.add(ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList))
                                        } else {
                                            mConnectionList2.filter { toDevice[0].device_desc == it.outDeviceName }[0]
                                                    .inputList.add(item)
                                        }
                                    } else {
                                        //在交换机连接信息的表里面没有找到，就去odfConnection找
                                        val odfConnection = odfConnectionDataList.filter {
                                            it.internal_device_type == 1001 && it.internal_device_id == item.model_id_to
                                        }
                                        if (odfConnection.isNotEmpty()) {
                                            if (odfConnectionDataList.filter { it.odf_id == odfConnection[0].external_odf_id }[0].internal_device_type == 1000) {
                                                //odf外接的设备为交换机
                                                if (mConnectionList2.none { toDevice[0].device_desc == it.outDeviceName }) {
                                                    val inputList = ArrayList<Inputs>()
                                                    inputList.add(item)
                                                    mConnectionList2.add(ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList))
                                                } else {
                                                    mConnectionList2.filter { toDevice[0].device_desc == it.outDeviceName }[0]
                                                            .inputList.add(item)
                                                }
                                            }
                                        }
                                    }
                                }
                    } else {
                        //收 "Rx"
                        DataBaseUtil.getInputsFilterTo(mDbPath!!, mWLBean!!.inDevice!!.device_id.toString())
                                .forEach { item ->
                                    val fromDevice = deviceDateList.filter { it.device_id == item.model_id_from }
                                    item.isInput = false

                                    if (mConnectionList2.none { fromDevice[0].device_desc == it.outDeviceName }) {
                                        val inputList = ArrayList<Inputs>()
                                        inputList.add(item)
                                        mConnectionList2.add(ConnectionBean(fromDevice[0].device_desc, fromDevice[0].device_code, inputList))
                                    } else {
                                        mConnectionList2.filter { fromDevice[0].device_desc == it.outDeviceName }[0]
                                                .inputList.add(item)
                                    }
                                }
                    }
                } else if (mWLBean!!.inSwitch != null && mWLBean!!.toDevice != null) {
                    if (mWLBean!!.type == "Tx") {
                        //发 Tx
                        DataBaseUtil.getInputsFilterFrom(mDbPath!!, mWLBean!!.toDevice!!.device_id.toString())
                                .forEach { item ->
                                    val toDevice = deviceDateList.filter { it.device_id == item.model_id_to }
                                    item.isInput = true

                                    if (switchDataConnection.none { item.port_to == it.to_port }.not()) {
                                        //如果在交换机连接里面找到了，说明是连的交换机
                                        if (mConnectionList2.none { toDevice[0].device_desc == it.outDeviceName }) {
                                            val inputList = ArrayList<Inputs>()
                                            inputList.add(item)
                                            mConnectionList2.add(ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList))
                                        } else {
                                            mConnectionList2.filter { toDevice[0].device_desc == it.outDeviceName }[0]
                                                    .inputList.add(item)
                                        }
                                    } else {
                                        //在交换机连接信息的表里面没有找到，就去odfConnection找
                                        val odfConnection = odfConnectionDataList.filter {
                                            it.internal_device_type == 1001 && it.internal_device_id == item.model_id_to
                                        }
                                        if (odfConnection.isNotEmpty()) {
                                            if (odfConnectionDataList.filter { it.odf_id == odfConnection[0].external_odf_id }[0].internal_device_type == 1000) {
                                                //odf外接的设备为交换机
                                                if (mConnectionList2.none { toDevice[0].device_desc == it.outDeviceName }) {
                                                    val inputList = ArrayList<Inputs>()
                                                    inputList.add(item)
                                                    mConnectionList2.add(ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList))
                                                } else {
                                                    mConnectionList2.filter { toDevice[0].device_desc == it.outDeviceName }[0]
                                                            .inputList.add(item)
                                                }
                                            }
                                        }
                                    }
                                }
                    } else {
                        //收 "Rx"
                        DataBaseUtil.getInputsFilterTo(mDbPath!!, mWLBean!!.toDevice!!.device_id.toString())
                                .forEach { item ->
                                    val fromDevice = deviceDateList.filter { it.device_id == item.model_id_from }
                                    item.isInput = false

                                    if (mConnectionList2.none { fromDevice[0].device_desc == it.outDeviceName }) {
                                        val inputList = ArrayList<Inputs>()
                                        inputList.add(item)
                                        mConnectionList2.add(ConnectionBean(fromDevice[0].device_desc, fromDevice[0].device_code, inputList))
                                    } else {
                                        mConnectionList2.filter { fromDevice[0].device_desc == it.outDeviceName }[0]
                                                .inputList.add(item)
                                    }
                                }
                    }
                }
            }
            //--------------------尾缆连线点击进来-----end----------------------

            //--------------------光缆连线点击进来-----start----------------------
            if (mGLBean != null) {
                if (mGLBean!!.odfConnection.internal_device_type == 1001 && mGLBean!!.odfOutConnection.internal_device_type == 1001) {
                    if (mGLBean!!.odfConnection.internal_rt_type == 1) {
                        // 发 Tx
                        val txList = DataBaseUtil.getInputsFilter(mDbPath!!, mGLBean!!.inDeviceId, mGLBean!!.outDeviceId, mGLBean!!.odfOutConnection.internal_device_port!!)
                        mConnectionList.addAll(txList)
                        mConnectionList.forEach { it.isInput = true }
                    } else {
                        // 收 Rx
                        val rxList = DataBaseUtil.getInputsFilter(mDbPath!!, mGLBean!!.outDeviceId, mGLBean!!.inDeviceId, mGLBean!!.odfConnection.internal_device_port!!)
                        mConnectionList.addAll(rxList)
                        mConnectionList.forEach { it.isInput = false }
                    }
                } else {
                    if (mGLBean!!.odfConnection.internal_device_type == 1001 && mGLBean!!.odfOutConnection.internal_device_type == 1000) {
                        if (mGLBean!!.odfConnection.internal_rt_type == 1) {
                            //发 Tx
                            DataBaseUtil.getInputsFilterFrom(mDbPath!!, mGLBean!!.inDeviceId)
                                    .forEach { item ->
                                        val toDevice = deviceDateList.filter { it.device_id == item.model_id_to }
                                        item.isInput = true

                                        if (switchDataConnection.none { item.port_to == it.to_port }.not()) {
                                            //如果在交换机连接里面找到了，说明是连的交换机
                                            if (mConnectionList2.none { toDevice[0].device_desc == it.outDeviceName }) {
                                                val inputList = ArrayList<Inputs>()
                                                inputList.add(item)
                                                mConnectionList2.add(ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList))
                                            } else {
                                                mConnectionList2.filter { toDevice[0].device_desc == it.outDeviceName }[0]
                                                        .inputList.add(item)
                                            }
                                        } else {
                                            //在交换机连接信息的表里面没有找到，就去odfConnection找
                                            val odfConnection = odfConnectionDataList.filter {
                                                it.internal_device_type == 1001 && it.internal_device_id == item.model_id_to
                                            }
                                            if (odfConnection.isNotEmpty()) {
                                                if (odfConnectionDataList.filter { it.odf_id == odfConnection[0].external_odf_id }[0].internal_device_type == 1000) {
                                                    //odf外接的设备为交换机
                                                    if (mConnectionList2.none { toDevice[0].device_desc == it.outDeviceName }) {
                                                        val inputList = ArrayList<Inputs>()
                                                        inputList.add(item)
                                                        mConnectionList2.add(ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList))
                                                    } else {
                                                        mConnectionList2.filter { toDevice[0].device_desc == it.outDeviceName }[0]
                                                                .inputList.add(item)
                                                    }
                                                }
                                            }
                                        }
                                    }
                        } else {
                            //收 "Rx"
                            DataBaseUtil.getInputsFilterTo(mDbPath!!, mGLBean!!.inDeviceId)
                                    .forEach { item ->
                                        val fromDevice = deviceDateList.filter { it.device_id == item.model_id_from }
                                        item.isInput = false

                                        if (mConnectionList2.none { fromDevice[0].device_desc == it.outDeviceName }) {
                                            val inputList = ArrayList<Inputs>()
                                            inputList.add(item)
                                            mConnectionList2.add(ConnectionBean(fromDevice[0].device_desc, fromDevice[0].device_code, inputList))
                                        } else {
                                            mConnectionList2.filter { fromDevice[0].device_desc == it.outDeviceName }[0]
                                                    .inputList.add(item)
                                        }
                                    }
                        }
                    } else if (mGLBean!!.odfConnection.internal_device_type == 1000 && mGLBean!!.odfOutConnection.internal_device_type == 1001) {
                        if (mGLBean!!.odfOutConnection.internal_rt_type == 1) {
                            //发 Tx
                            DataBaseUtil.getInputsFilterFrom(mDbPath!!, mGLBean!!.outDeviceId)
                                    .forEach { item ->
                                        val toDevice = deviceDateList.filter { it.device_id == item.model_id_to }
                                        item.isInput = true

                                        if (switchDataConnection.none { item.port_to == it.to_port }.not()) {
                                            //如果在交换机连接里面找到了，说明是连的交换机
                                            if (mConnectionList2.none { toDevice[0].device_desc == it.outDeviceName }) {
                                                val inputList = ArrayList<Inputs>()
                                                inputList.add(item)
                                                mConnectionList2.add(ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList))
                                            } else {
                                                mConnectionList2.filter { toDevice[0].device_desc == it.outDeviceName }[0]
                                                        .inputList.add(item)
                                            }
                                        } else {
                                            //在交换机连接信息的表里面没有找到，就去odfConnection找
                                            val odfConnection = odfConnectionDataList.filter {
                                                it.internal_device_type == 1001 && it.internal_device_id == item.model_id_to
                                            }
                                            if (odfConnection.isNotEmpty()) {
                                                if (odfConnectionDataList.filter { it.odf_id == odfConnection[0].external_odf_id }[0].internal_device_type == 1000) {
                                                    //odf外接的设备为交换机
                                                    if (mConnectionList2.none { toDevice[0].device_desc == it.outDeviceName }) {
                                                        val inputList = ArrayList<Inputs>()
                                                        inputList.add(item)
                                                        mConnectionList2.add(ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList))
                                                    } else {
                                                        mConnectionList2.filter { toDevice[0].device_desc == it.outDeviceName }[0]
                                                                .inputList.add(item)
                                                    }
                                                }
                                            }
                                        }
                                    }
                        } else {
                            //收 "Rx"
                            DataBaseUtil.getInputsFilterTo(mDbPath!!, mGLBean!!.outDeviceId)
                                    .forEach { item ->
                                        val fromDevice = deviceDateList.filter { it.device_id == item.model_id_from }
                                        item.isInput = false

                                        if (mConnectionList2.none { fromDevice[0].device_desc == it.outDeviceName }) {
                                            val inputList = ArrayList<Inputs>()
                                            inputList.add(item)
                                            mConnectionList2.add(ConnectionBean(fromDevice[0].device_desc, fromDevice[0].device_code, inputList))
                                        } else {
                                            mConnectionList2.filter { fromDevice[0].device_desc == it.outDeviceName }[0]
                                                    .inputList.add(item)
                                        }
                                    }
                        }
                    }
                }
            }
            //--------------------光缆连线点击进来-----end----------------------

            //--------------------跳纤连线点击进来-----start----------------------
            if (mTXBean != null) {
                if (mTXBean!!.inType == "1001" && mTXBean!!.toType == "1001") {
                    //如果跳纤是装置到装置

                    if (mTXBean!!.inputType == "Tx") {
                        // 发 Tx
                        val txList = DataBaseUtil.getInputsFilter(mDbPath!!, mTXBean!!.inDeviceId.toString(),
                                mTXBean!!.outDeviceId.toString(), mTXBean!!.outPort)
                        mConnectionList.addAll(txList)
                        mConnectionList.forEach { it.isInput = true }
                    } else {
                        // 收 Rx
                        val rxList = DataBaseUtil.getInputsFilter(mDbPath!!, mTXBean!!.outDeviceId.toString(),
                                mTXBean!!.inDeviceId.toString(), mTXBean!!.inPort)
                        mConnectionList.addAll(rxList)
                        mConnectionList.forEach { it.isInput = false }
                    }
                } else {

                    // 如果跳纤是装置与交换机
                    if (mTXBean!!.inType == "1001" && mTXBean!!.toType == "1000") {
                        //如果输出设备为装置 对面一定为交换机
                        if (mTXBean!!.inputType == "Tx") {
                            //发
                            DataBaseUtil.getInputsFilterFrom(mDbPath!!, mTXBean!!.inDeviceId.toString())
                                    .forEach { item ->
                                        val toDevice = deviceDateList.filter { it.device_id == item.model_id_to }
                                        item.isInput = true

                                        if (switchDataConnection.none { item.port_to == it.to_port }.not()) {
                                            //如果在交换机连接里面找到了，说明是连的交换机
                                            if (mConnectionList2.none { toDevice[0].device_desc == it.outDeviceName }) {
                                                val inputList = ArrayList<Inputs>()
                                                inputList.add(item)
                                                mConnectionList2.add(ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList))
                                            } else {
                                                mConnectionList2.filter { toDevice[0].device_desc == it.outDeviceName }[0]
                                                        .inputList.add(item)
                                            }
                                        } else {
                                            //在交换机连接信息的表里面没有找到，就去odfConnection找
                                            val odfConnection = odfConnectionDataList.filter {
                                                it.internal_device_type == 1001 && it.internal_device_id == item.model_id_to
                                            }
                                            if (odfConnection.isNotEmpty()) {
                                                if (odfConnectionDataList.filter { it.odf_id == odfConnection[0].external_odf_id }[0].internal_device_type == 1000) {
                                                    //odf外接的设备为交换机
                                                    if (mConnectionList2.none { toDevice[0].device_desc == it.outDeviceName }) {
                                                        val inputList = ArrayList<Inputs>()
                                                        inputList.add(item)
                                                        mConnectionList2.add(ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList))
                                                    } else {
                                                        mConnectionList2.filter { toDevice[0].device_desc == it.outDeviceName }[0]
                                                                .inputList.add(item)
                                                    }
                                                }
                                            }
                                        }
                                    }
                        } else {
                            //收 "Rx"
                            DataBaseUtil.getInputsFilterTo(mDbPath!!, mTXBean!!.inDeviceId.toString())
                                    .forEach { item ->
                                        val fromDevice = deviceDateList.filter { it.device_id == item.model_id_from }
                                        item.isInput = false

                                        if (mConnectionList2.none { fromDevice[0].device_desc == it.outDeviceName }) {
                                            val inputList = ArrayList<Inputs>()
                                            inputList.add(item)
                                            mConnectionList2.add(ConnectionBean(fromDevice[0].device_desc, fromDevice[0].device_code, inputList))
                                        } else {
                                            mConnectionList2.filter { fromDevice[0].device_desc == it.outDeviceName }[0]
                                                    .inputList.add(item)
                                        }
                                    }
                        }

                    } else if (mTXBean!!.inType == "1000" && mTXBean!!.toType == "1001") {
                        //如果输出设备为交换机
                        if (mTXBean!!.inputType == "Tx") {
                            //设备 收 Rx
                            DataBaseUtil.getInputsFilterTo(mDbPath!!, mTXBean!!.outDeviceId.toString())
                                    .forEach { item ->
                                        val fromDevice = deviceDateList.filter { it.device_id == item.model_id_from }
                                        item.isInput = false

                                        if (mConnectionList2.none { fromDevice[0].device_desc == it.outDeviceName }) {
                                            val inputList = ArrayList<Inputs>()
                                            inputList.add(item)
                                            mConnectionList2.add(ConnectionBean(fromDevice[0].device_desc, fromDevice[0].device_code, inputList))
                                        } else {
                                            mConnectionList2.filter { fromDevice[0].device_desc == it.outDeviceName }[0]
                                                    .inputList.add(item)
                                        }
                                    }

                        } else {
                            //设备发 Tx
                            DataBaseUtil.getInputsFilterFrom(mDbPath!!, mTXBean!!.outDeviceId.toString())
                                    .forEach { item ->
                                        val toDevice = deviceDateList.filter { it.device_id == item.model_id_to }
                                        item.isInput = true

                                        if (switchDataConnection.none { item.port_to == it.to_port }.not()) {
                                            //如果在交换机连接里面找到了，说明是连的交换机
                                            if (mConnectionList2.none { toDevice[0].device_desc == it.outDeviceName }) {
                                                val inputList = ArrayList<Inputs>()
                                                inputList.add(item)
                                                mConnectionList2.add(ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList))
                                            } else {
                                                mConnectionList2.filter { toDevice[0].device_desc == it.outDeviceName }[0]
                                                        .inputList.add(item)
                                            }
                                        } else {
                                            //在交换机连接信息的表里面没有找到，就去odfConnection找
                                            val odfConnection = odfConnectionDataList.filter {
                                                it.internal_device_type == 1001 && it.internal_device_id == item.model_id_to
                                            }
                                            if (odfConnection.isNotEmpty()) {
                                                if (odfConnectionDataList.filter { it.odf_id == odfConnection[0].external_odf_id }[0].internal_device_type == 1000) {
                                                    //odf外接的设备为交换机
                                                    if (mConnectionList2.none { toDevice[0].device_desc == it.outDeviceName }) {
                                                        val inputList = ArrayList<Inputs>()
                                                        inputList.add(item)
                                                        mConnectionList2.add(ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList))
                                                    } else {
                                                        mConnectionList2.filter { toDevice[0].device_desc == it.outDeviceName }[0]
                                                                .inputList.add(item)
                                                    }
                                                }
                                            }
                                        }
                                    }
                        }
                    }
                }
            }
            //--------------------跳纤连线点击进来-----end----------------------

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
                        initRecycler()
                    }

                    override fun onError(e: Throwable) {
                        progressDialog.dismiss()
                        toast("读取数据库失败，请检查数据库是否存在")
                    }

                    override fun onNext(dataList: String) {

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

        if (mWLBean != null && mConnectionList2.isNotEmpty()) {
            logE("----mWLBean------mConnectionList2.size = ${mConnectionList2.size}--mConnectionList2=$mConnectionList2--")
            //如果是尾缆，并且是装置与交换机，
            connectionTXRV.run {
                layoutManager = LinearLayoutManager(activity)
                adapter = ConnectionListItem2RecyclerAdapter(activity!!, mConnectionList2)
            }
        } else if (mGLBean != null && mConnectionList2.isNotEmpty()) {
            logE("----mGLBean------mConnectionList2.size = ${mConnectionList2.size}--mConnectionList2=$mConnectionList2---")
            //如果是光缆，并且是装置与交换机，
            connectionTXRV.run {
                layoutManager = LinearLayoutManager(activity)
                adapter = ConnectionListItem2RecyclerAdapter(activity!!, mConnectionList2)
            }
        } else if (mTXBean != null && mConnectionList2.isNotEmpty()) {
            logE("----mTXBean------mConnectionList2.size = ${mConnectionList2.size}--mConnectionList2=$mConnectionList2---")
            //如果是跳纤，并且是装置与交换机，
            connectionTXRV.run {
                layoutManager = LinearLayoutManager(activity)
                adapter = ConnectionListItem2RecyclerAdapter(activity!!, mConnectionList2)
            }
        } else {
            logE("------ mConnectionList.size = ${mConnectionList.size}-----")
            mAdapter = ConnectionListItemRecyclerAdapter(activity!!, mConnectionList)
            connectionRV.run {
                layoutManager = LinearLayoutManager(activity)
                adapter = mAdapter
            }
        }


        /**
         * 当数据长度大于了4条，就动态增加左右两边控件的高度
         */
        if (mConnectionList.size > 4) {
            inDeviceCard.apply {
                layoutParams.height = Util.dip2px(activity!!, 50 * mConnectionList.size)
            }
            outDeviceCard.apply {
                layoutParams.height = Util.dip2px(activity!!, 50 * mConnectionList.size)
            }
        }


        /**
         * 如果是1对多，左边的设备需要动态变高
         */
        var size = 0
        if (mConnectionList2.isNotEmpty()) {
            mConnectionList2.forEach {
                size += if (it.inputList.size < 3) {
                    3
                } else {
                    it.inputList.size
                }
            }
        }
        if (size > 4) {
            inDeviceCard.apply {
                layoutParams.height = Util.dip2px(activity!!, 50 * size)
            }
        }
    }

    override fun onBackPressed() = BackHandlerHelper.handleBackPress(this)

}
