package com.shenrui.label.biaoqian.ui.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.luckongo.tthd.mvp.model.bean.DeviceConnection
import com.luckongo.tthd.mvp.model.bean.SwitchConnection
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.mvp.model.bean.TXConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.WLConnectionBean
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import kotlinx.android.synthetic.main.title_layout.*
import org.jetbrains.anko.support.v4.toast
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class GLConnectionFragment : BaseFragment(), FragmentBackHandler {

    private var mPath: String? = null
    private var mWLName: String? = null

    private val mWLConnectionList: ArrayList<WLConnectionBean> by lazy {
        ArrayList<WLConnectionBean>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mPath = arguments!!.getString(ARG_PARAM1)
            mWLName = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun getLayoutId() = R.layout.fragment_gl_connection

    @SuppressLint("SetTextI18n")
    override fun initView() {
        tv_back_title.text = mWLName

        tv_title.text = "纤芯信息"

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

            val deviceConnection = ArrayList<DeviceConnection>()
            //刷选出设备相关的设备连接情况
            val deviceDateList = DataBaseUtil.getDevice(mPath!!)
            //得到数据库中所有的设备连接
            val deviceDataConnectionList = DataBaseUtil.getDeviceConnection(mPath!!)

            //交换机
            val switchDataConnection = DataBaseUtil.getSwitchConnection(mPath!!)
            val switchConnection = ArrayList<SwitchConnection>()
            val switchDateList = DataBaseUtil.getSwitch(mPath!!)



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

//        //TX链接图
//        val txAdapter = PanelTXConnectionListItemRecyclerAdapter(activity!!, mTXConnectionList)
//        rv_tx_connection.run {
//            layoutManager = LinearLayoutManager(activity)
//            adapter = txAdapter
//        }

    }

    override fun onBackPressed() = BackHandlerHelper.handleBackPress(this)

    companion object {
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        fun newInstance(param1: String, param2: String): GLConnectionFragment {
            val fragment = GLConnectionFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
