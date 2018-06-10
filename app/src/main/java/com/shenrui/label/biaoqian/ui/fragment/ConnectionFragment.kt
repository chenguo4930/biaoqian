package com.shenrui.label.biaoqian.ui.fragment

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
    private lateinit var mAdapter2: ConnectionListItem2RecyclerAdapter

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
        }

        //----------如果是光缆连线点击进来------------
        if (mGLBean != null) {
            logE("-----------ConnectionFragment---mGLBean = $mGLBean-")
            tvInDeviceName.text = mGLBean!!.inDeviceName
            tvInModeTitle.text = mGLBean!!.inDeviceCode
            tvOutDeviceName.text = mGLBean!!.outDeviceName
            tvOutModeTitle.text = mGLBean!!.outDeviceCode
        }

        //----------如果是跳纤连线点击进来------------
        if (mTXBean != null) {
            logE("-----------ConnectionFragment---mTXBean = $mTXBean-")
            tvInDeviceName.text = mTXBean!!.inDeviceName
            tvInModeTitle.text = mTXBean!!.inDeviceCode
            tvOutDeviceName.text = mTXBean!!.outDeviceName
            tvOutModeTitle.text = mTXBean!!.outDeviceCode
            if ((mTXBean!!.inType == "1001" && mTXBean!!.toType == "1001").not()) {
                connectionRV.visibility = View.GONE
                outDeviceCard.visibility = View.GONE
                connectionRV.visibility = View.VISIBLE
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
        Observable.create(ObservableOnSubscribe<String> {

            mConnectionList.clear()
            //--------------------尾缆连线点击进来-----start----------------------
            if (mWLBean != null) {
                //输出装置的id，输入装置的id
                val inModelId = if (mWLBean!!.inDevice != null) {
                    mWLBean!!.inDevice!!.device_id.toString()
                } else {
                    mWLBean!!.inSwitch!!.switch_id.toString()
                }
                //输出端口Tx
                val inModelPort = if (mWLBean!!.inDeviceConnection != null) {
                    mWLBean!!.inDeviceConnection!!.from_port
                } else {
                    mWLBean!!.inSwitchConnection!!.from_port
                }
                val toModelId = if (mWLBean!!.toDevice != null) {
                    mWLBean!!.toDevice!!.device_id.toString()
                } else {
                    mWLBean!!.toSwitch!!.switch_id.toString()
                }
                //输入端口Rx
                val toModelPort = if (mWLBean!!.inDeviceConnection != null) {
                    mWLBean!!.inDeviceConnection!!.to_port
                } else {
                    mWLBean!!.inSwitchConnection!!.to_port
                }
                //从数据库中获取inModel 到outModel的连接信息
                val inList = DataBaseUtil.getInputsFilter(mDbPath!!, inModelId, toModelId, toModelPort)
                //从数据库中获取outModel 到intModel的连接信息
                val outList = DataBaseUtil.getInputsFilter(mDbPath!!, toModelId, inModelId, inModelPort)
                Log.e("----", "---------mWLBean:$mWLBean")
                if (mWLBean!!.type == "Tx") {
                    inList.forEach {
                        Log.e("----", "---------inList each:$it")
                        it.isInput = true
                    }
//                    outList.forEach {
//                        Log.e("----", "---------outList each:$it")
//                        it.isInput = false
//                    }
                    mConnectionList.addAll(inList)
//                    mConnectionList.addAll(outList)
                } else {
//                    inList.forEach {
//                        Log.e("----", "---------inList each:$it")
//                        it.isInput = false
//                    }
                    outList.forEach {
                        Log.e("----", "---------outList each:$it")
                        it.isInput = false
                    }
                    mConnectionList.addAll(outList)
//                    mConnectionList.addAll(inList)
                }
            }
            //--------------------尾缆连线点击进来-----end----------------------

            //--------------------光缆连线点击进来-----start----------------------
            if (mGLBean != null) {
                val inTxTypeStr = if (mGLBean!!.odfConnection.internal_rt_type == 0) {
                    "Rx"
                } else {
                    "Tx"
                }
                val inPort = if (mGLBean!!.odfConnection.internal_rt_type == 0) {
                    mGLBean!!.odfConnection.internal_device_port!!
                } else {
                    mGLBean!!.odfOutConnection.internal_device_port!!
                }
                val toPort = if (mGLBean!!.odfOutConnection.internal_rt_type == 0) {
                    mGLBean!!.odfOutConnection.internal_device_port!!
                } else {
                    mGLBean!!.odfConnection.internal_device_port!!
                }
                //从数据库中获取inModel 到outModel的连接信息
                val inList = DataBaseUtil.getInputsFilter(mDbPath!!, mGLBean!!.inDeviceId, mGLBean!!.outDeviceId, toPort)
                //从数据库中获取outModel 到intModel的连接信息
                val outList = DataBaseUtil.getInputsFilter(mDbPath!!, mGLBean!!.outDeviceId, mGLBean!!.inDeviceId, inPort)
                Log.e("----", "---------mGLBean:$mGLBean")
                if (inTxTypeStr == "Tx") {
                    inList.forEach {
                        Log.e("----", "---------inList each:$it")
                        it.isInput = true
                    }
                    mConnectionList.addAll(inList)
                } else {
                    outList.forEach {
                        Log.e("----", "---------outList each:$it")
                        it.isInput = false
                    }
                    mConnectionList.addAll(outList)
                }
            }
            //--------------------光缆连线点击进来-----end----------------------

            //--------------------跳纤连线点击进来-----start----------------------
            if (mTXBean != null) {
                if (mTXBean!!.inType == "1001" && mTXBean!!.toType == "1001") {
                    //如果跳纤是装置到装置
                    val inPort = if (mTXBean!!.inputType == "Tx") {
                        mTXBean!!.inPort
                    } else {
                        mTXBean!!.outPort
                    }
                    val toPort = if (mTXBean!!.inputType == "Rx") {
                        mTXBean!!.inPort
                    } else {
                        mTXBean!!.outPort
                    }

                    //从数据库中获取inModel 到outModel的连接信息
                    val inList = DataBaseUtil.getInputsFilter(mDbPath!!, mTXBean!!.inDeviceId.toString(), mTXBean!!.outDeviceId.toString(), toPort)
                    //从数据库中获取outModel 到intModel的连接信息
                    val outList = DataBaseUtil.getInputsFilter(mDbPath!!, mTXBean!!.outDeviceId.toString(), mTXBean!!.inDeviceId.toString(), inPort)
                    if (mTXBean!!.inputType == "Tx") {
                        inList.forEach {
                            Log.e("----", "-----txBean----inList each:$it")
                            it.isInput = true
                        }
                        mConnectionList.addAll(inList)
                    } else {
                        outList.forEach {
                            Log.e("----", "-----txBean----outList each:$it")
                            it.isInput = false
                        }
                        mConnectionList.addAll(outList)
                    }
                } else {

                    val switchDataConnection = DataBaseUtil.getSwitchConnection(mDbPath!!)
                    val deviceDateList = DataBaseUtil.getDevice(mDbPath!!)
                    val odfConnectionDataList = DataBaseUtil.getODFConnection(mDbPath!!)

                    // 如果跳纤是装置与交换机
                    if (mTXBean!!.inType == "1001" && mTXBean!!.toType == "1000") {
                        //如果输出设备为装置 对面一定为交换机
                        if (mTXBean!!.inputType == "Tx") {
                            //发
                            DataBaseUtil.getInputsFilterFrom(mDbPath!!, mTXBean!!.inDeviceId.toString())
                                    .forEach { item ->
                                        val toDevice = deviceDateList.filter { it.device_id == item.model_id_to }
                                        item.isInput = true

                                        if (switchDataConnection.none { item.port_to == it.from_port }.not()) {
                                            //如果在交换机连接里面找到了，说明是连的交换机
                                            if (mConnectionList2.none { toDevice[0].device_desc == it.outDeviceName }) {
                                                val inputList = ArrayList<Inputs>()
                                                inputList.add(item)
                                                ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList)
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
                                                        ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList)
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
                                            ConnectionBean(fromDevice[0].device_desc, fromDevice[0].device_code, inputList)
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
                                            ConnectionBean(fromDevice[0].device_desc, fromDevice[0].device_code, inputList)
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

                                        if (switchDataConnection.none { item.port_to == it.from_port }.not()) {
                                            //如果在交换机连接里面找到了，说明是连的交换机
                                            if (mConnectionList2.none { toDevice[0].device_desc == it.outDeviceName }) {
                                                val inputList = ArrayList<Inputs>()
                                                inputList.add(item)
                                                ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList)
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
                                                        ConnectionBean(toDevice[0].device_desc, toDevice[0].device_code, inputList)
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
                        initRecycler()
                    }

                    override fun onError(e: Throwable) {
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

        if (mTXBean != null && !(mTXBean!!.inType == "1001" && mTXBean!!.toType == "1001" || mTXBean!!.inType == "1000" && mTXBean!!.toType == "1000")) {
            //如果是跳纤，并且是装置与交换机，
            mAdapter2 = ConnectionListItem2RecyclerAdapter(activity!!, mConnectionList2)
            connectionTXRV.run {
                layoutManager = LinearLayoutManager(activity)
                adapter = mAdapter
            }
        } else {
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

    }

    override fun onBackPressed() = BackHandlerHelper.handleBackPress(this)

}
