package com.shenrui.label.biaoqian.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
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
import com.shenrui.label.biaoqian.mvp.model.bean.GLConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.TXConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.WLConnectionBean
import com.shenrui.label.biaoqian.ui.adapter.ConnectionListItemRecyclerAdapter
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import com.shenrui.label.biaoqian.utils.Util
import kotlinx.android.synthetic.main.fragment_gx_connection.*
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
     */
    private fun initText() {
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
        }
    }

    private fun initData() {
        Observable.create(Observable.OnSubscribe<String> {

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
                val outModelId = if (mWLBean!!.toDevice != null) {
                    mWLBean!!.toDevice!!.device_id.toString()
                } else {
                    mWLBean!!.toSwitch!!.switch_id.toString()
                }
                //输入端口Rx
                val outModelPort = if (mWLBean!!.inDeviceConnection != null) {
                    mWLBean!!.inDeviceConnection!!.to_port
                } else {
                    mWLBean!!.inSwitchConnection!!.to_port
                }
                //从数据库中获取inModel 到outModel的连接信息
                val inList = DataBaseUtil.getInputsFilter(mDbPath!!, inModelId, outModelId, outModelPort)
                //从数据库中获取outModel 到intModel的连接信息
                val outList = DataBaseUtil.getInputsFilter(mDbPath!!, outModelId, inModelId, inModelPort)
                Log.e("----", "---------mWLBean:$mWLBean")
                inList.forEach {
                    Log.e("----", "---------inList each:$it")
                    it.isInput = true
                }
                outList.forEach {
                    Log.e("----", "---------outList each:$it")
                    it.isInput = false
                }
                mConnectionList.addAll(inList)
                mConnectionList.addAll(outList)
            }

            //--------------------光缆连线点击进来-----start----------------------
            if (mGLBean != null) {
//                val inTxTypeStr = if (mGLBean!!.odfConnection.internal_rt_type == 0) {
//                    "Rx"
//                } else {
//                    "Tx"
//                }
                val inPort = if (mGLBean!!.odfConnection.internal_rt_type == 0) {
                    mGLBean!!.odfConnection.internal_device_port!!
                } else {
                    mGLBean!!.odfOutConnection.internal_device_port!!
                }
                val outPort = if (mGLBean!!.odfOutConnection.internal_rt_type == 0) {
                    mGLBean!!.odfOutConnection.internal_device_port!!
                } else {
                    mGLBean!!.odfConnection.internal_device_port!!
                }
                //从数据库中获取inModel 到outModel的连接信息
                val inList = DataBaseUtil.getInputsFilter(mDbPath!!, mGLBean!!.inDeviceId, mGLBean!!.outDeviceId, outPort)
                //从数据库中获取outModel 到intModel的连接信息
                val outList = DataBaseUtil.getInputsFilter(mDbPath!!, mGLBean!!.outDeviceId, mGLBean!!.inDeviceId, inPort)
                Log.e("----", "---------mGLBean:$mGLBean")
                inList.forEach {
                    Log.e("----", "---------inList each:$it")
                    it.isInput = true
                }
                outList.forEach {
                    Log.e("----", "---------outList each:$it")
                    it.isInput = false
                }
                mConnectionList.addAll(inList)
                mConnectionList.addAll(outList)
            }

            //--------------------跳纤连线点击进来-----start----------------------
            if (mTXBean != null) {

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
                inList.forEach {
                    Log.e("----", "-----txBean----inList each:$it")
                    it.isInput = true
                }
                outList.forEach {
                    Log.e("----", "-----txBean----outList each:$it")
                    it.isInput = false
                }
                mConnectionList.addAll(inList)
                mConnectionList.addAll(outList)
            }

            it.onCompleted()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<String>() {
                    override fun onCompleted() {
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

        mAdapter = ConnectionListItemRecyclerAdapter(activity!!, mConnectionList)
        connectionRV.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
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
