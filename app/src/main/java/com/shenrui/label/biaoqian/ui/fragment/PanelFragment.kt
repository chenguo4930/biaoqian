package com.shenrui.label.biaoqian.ui.fragment

import android.os.Bundle
import android.util.Log
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.mvp.model.bean.TXConnectionBean
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import org.jetbrains.anko.support.v4.toast
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class PanelFragment : BaseFragment(), FragmentBackHandler {

    private var mPath: String? = null
    private var mPanelId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mPath = arguments!!.getString(ARG_PARAM1)
            mPanelId = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun getLayoutId() = R.layout.fragment_panel

    override fun initView() {

    }

    override fun lazyLoad() {

        Observable.create(Observable.OnSubscribe<ArrayList<TXConnectionBean>> {
            val deviceList = DataBaseUtil.getDevice(mPath!!)
            val deviceConnection = DataBaseUtil.getDeviceConnection(mPath!!)
            val switchList = DataBaseUtil.getSwitch(mPath!!)
            val switchConnection = DataBaseUtil.getSwitchConnection(mPath!!)

//            it.onNext(panelList)
            it.onCompleted()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<TXConnectionBean>>() {
                    override fun onCompleted() {
//                        toast("成功读取数据库")
                    }

                    override fun onError(e: Throwable) {
                        toast("读取数据库失败，请检查数据库是否存在")
                    }

                    override fun onNext(dataList: ArrayList<TXConnectionBean>) {
                        dataList.forEach {
                            Log.e("-----", "-----PanelBean=$it")
                        }
                    }
                })

    }

    override fun onBackPressed() = BackHandlerHelper.handleBackPress(this)

    companion object {
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        fun newInstance(param1: String, param2: String): PanelFragment {
            val fragment = PanelFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
