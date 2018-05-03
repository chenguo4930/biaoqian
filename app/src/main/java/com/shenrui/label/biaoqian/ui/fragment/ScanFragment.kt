package com.shenrui.label.biaoqian.ui.fragment

import android.os.Bundle
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.constrant.AllSubStation
import com.shenrui.label.biaoqian.mvp.base.BaseFragment

class ScanFragment : BaseFragment() {

    companion object {


        fun newInstance(param1: String, param2: String): ScanFragment {
            val fragment = ScanFragment()
            val args = Bundle()
            args.putString(AllSubStation.PARAM_1, param1)
            args.putString(AllSubStation.PARAM_2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    private var mParam1: String? = null
    private var mParam2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(AllSubStation.PARAM_1)
            mParam2 = arguments!!.getString(AllSubStation.PARAM_2)
        }
    }

    override fun getLayoutId() = R.layout.fragment_scan

    override fun initView() {
    }

    override fun lazyLoad() {
    }
}
