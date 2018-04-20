package com.shenrui.label.biaoqian.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.luckongo.tthd.mvp.model.bean.Inputs
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.mvp.model.bean.GLConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.RegionBean
import com.shenrui.label.biaoqian.mvp.model.bean.WLConnectionBean
import com.shenrui.label.biaoqian.ui.adapter.ConnectionListItemRecyclerAdapter
import com.shenrui.label.biaoqian.ui.adapter.RegionListAdapter
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import kotlinx.android.synthetic.main.fragment_gx_connection.*
import kotlinx.android.synthetic.main.title_layout.*
import org.jetbrains.anko.support.v4.toast
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.logging.Logger

/**
 * 纤芯点击后，显示设备间具体的连接信息
 */
class ConnectionFragment : BaseFragment(), FragmentBackHandler {

    companion object {
        private const val DB_PATH = "param1"
        private const val WL_BEAN = "param2"
        private const val GL_BEAN = "param3"
        private const val TX_BEAN = "param4"

        fun newInstance(dbPath: String, wlBean: WLConnectionBean?, glBean: GLConnectionBean?, txBean: String?): ConnectionFragment {
            val fragment = ConnectionFragment()
            val args = Bundle()
            args.putString(DB_PATH, dbPath)
            args.putParcelable(WL_BEAN, wlBean)
            args.putParcelable(GL_BEAN, glBean)
            args.putString(TX_BEAN, txBean)
            fragment.arguments = args
            return fragment
        }
    }

    private var mDbPath: String? = null
    private var mWLBean: WLConnectionBean? = null
    private var mGLBean: GLConnectionBean? = null
    private var mTXBean: String? = null

    //连线的数据List
    private val mConnectionList = ArrayList<Inputs>()

    private lateinit var mAdapter: ConnectionListItemRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mDbPath = arguments!!.getString(DB_PATH)
            mWLBean = arguments!!.getParcelable(WL_BEAN)
            mGLBean = arguments!!.getParcelable(GL_BEAN)
            mTXBean = arguments!!.getString(TX_BEAN)
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

        }

        //----------如果是跳纤连线点击进来------------
        if (mTXBean != null) {

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
                val outModelId = if (mWLBean!!.toDevice != null) {
                    mWLBean!!.toDevice!!.device_id.toString()
                } else {
                    mWLBean!!.toSwitch!!.switch_id.toString()
                }
                //从数据库中获取inModel 到outModel的连接信息
                val inList = DataBaseUtil.getInputsFilter(mDbPath!!, inModelId, outModelId)
                //从数据库中获取outModel 到intModel的连接信息
                val outList = DataBaseUtil.getInputsFilter(mDbPath!!, outModelId, inModelId)
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

            }

            //--------------------跳纤连线点击进来-----start----------------------
            if (mTXBean != null) {

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
    }

    override fun onBackPressed() = BackHandlerHelper.handleBackPress(this)

}
