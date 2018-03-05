package com.shenrui.label.biaoqian.ui.activity

import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.base.BaseActivity
import com.shenrui.label.biaoqian.mvp.contract.BiaoQianContract
import com.shenrui.label.biaoqian.mvp.presenter.BiaoQianPresenter
import com.shenrui.label.biaoqian.ui.fragment.HomeFragment
import com.shenrui.label.biaoqian.ui.fragment.ScanFragment
import com.shenrui.label.biaoqian.ui.fragment.SettingFragment
import kotlinx.android.synthetic.main.activity_biao_qian.*

class BiaoQianActivity : BaseActivity<BiaoQianContract.View,
        BiaoQianPresenter<BiaoQianContract.View>>(),
        BiaoQianContract.View{

    private var mHomeFragment: HomeFragment? = null
    private var mSettingFragment: SettingFragment? = null
    private var mScanFragment: ScanFragment? = null

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
    }

    override fun initListener() {
        home_btn.setOnClickListener {
            if (mHomeFragment == null){
                mHomeFragment = HomeFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.content_frame,mHomeFragment).commit()
        }

        setting_btn.setOnClickListener {
            if (mSettingFragment == null){
                mSettingFragment = SettingFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.content_frame, mSettingFragment).commit()
        }

        scan_btn.setOnClickListener {
            if (mScanFragment == null){
                mScanFragment = ScanFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.content_frame,mScanFragment).commit()
        }
    }
}
