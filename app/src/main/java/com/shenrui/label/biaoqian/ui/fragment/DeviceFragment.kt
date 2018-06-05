package com.shenrui.label.biaoqian.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.luckongo.tthd.mvp.model.bean.Device
import com.luckongo.tthd.mvp.model.bean.Switch
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.constrant.AllSubStation
import com.shenrui.label.biaoqian.mvp.base.BaseFragment
import com.shenrui.label.biaoqian.mvp.model.bean.PanelBean
import com.shenrui.label.biaoqian.mvp.model.bean.RegionBean
import com.shenrui.label.biaoqian.ui.adapter.PanelGridAdapter
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_device.*
import kotlinx.android.synthetic.main.title_layout.*
import org.jetbrains.anko.support.v4.toast
import org.reactivestreams.Subscriber

/**
 * 设备的fragment
 */
class DeviceFragment : BaseFragment(), FragmentBackHandler {


    private var mDbPath: String? = null
    private var mRegionBean: RegionBean? = null

    private lateinit var mAdapter: PanelGridAdapter
    private lateinit var mPanelBeanList: ArrayList<PanelBean>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mDbPath = arguments!!.getString(AllSubStation.PARAM_1)
            mRegionBean = arguments!!.getParcelable(AllSubStation.PARAM_2)
        }
    }

    override fun getLayoutId() = R.layout.fragment_device

    override fun initView() {

    }

    @SuppressLint("SetTextI18n")
    override fun lazyLoad() {
        img_back.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        tv_back_title.text = mRegionBean?.region_name + "(" + mRegionBean?.region_code + ")"
        if (mDbPath.isNullOrEmpty()) {
            toast("数据库路径为空")
        } else {
            Log.e("----", "---------mDbPath:$mDbPath")
            initData()
        }
    }

    private fun initData() {

        Observable.create(ObservableOnSubscribe<ArrayList<PanelBean>> {
            val deviceList = DataBaseUtil.getDevice(mDbPath!!)
            val switchList = DataBaseUtil.getSwitch(mDbPath!!)
            val panelList = ArrayList<PanelBean>()
            mRegionBean?.panel?.forEach { item ->
                val deviceBeanList = ArrayList<Device>()
                deviceList.forEach {
                    if (it.panel_id == item.panel_id) {
                        deviceBeanList.add(it)
                    }
                }

                val switchBeanList = ArrayList<Switch>()
                switchList.forEach {
                    if (it.panel_id == item.panel_id) {
                        switchBeanList.add(it)
                    }
                }
                panelList.add(
                        PanelBean(item.panel_id, item.panel_name, item.panel_code,
                                item.region_id, deviceBeanList, switchBeanList))
            }
            it.onNext(panelList)
            it.onComplete()
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ArrayList<PanelBean>> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onComplete() {
//                        toast("成功读取数据库")
                    }

                    override fun onError(e: Throwable) {
                        toast("读取数据库失败，请检查数据库是否存在")
                    }

                    override fun onNext(dataList: ArrayList<PanelBean>) {
                        mPanelBeanList = dataList
                        initRecycler()
                    }
                })
    }

    private fun initRecycler() {

        if (activity == null) {
            return
        }

        mAdapter = PanelGridAdapter(activity!!, mPanelBeanList, object : PanelGridAdapter.PanelClickListener {
            override fun onPanelItemClick(item: PanelBean) {
                activity?.supportFragmentManager?.beginTransaction()
                        ?.add(R.id.content_frame, PanelFragment.newInstance(mDbPath!!, item))
                        ?.addToBackStack("PanelFragment")
                        ?.commit()
            }
        })
        rv_panel.run {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = mAdapter
        }

    }


    override fun onBackPressed() = BackHandlerHelper.handleBackPress(this)

    companion object {

        fun newInstance(param1: String, param2: RegionBean): DeviceFragment {
            val fragment = DeviceFragment()
            val args = Bundle()
            args.putString(AllSubStation.PARAM_1, param1)
            args.putParcelable(AllSubStation.PARAM_2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
