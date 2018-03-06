package com.shenrui.label.biaoqian.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.base.BaseActivity
import com.shenrui.label.biaoqian.mvp.contract.BiaoQianContract
import com.shenrui.label.biaoqian.mvp.presenter.BiaoQianPresenter
import com.shenrui.label.biaoqian.ui.fragment.HomeFragment
import com.shenrui.label.biaoqian.ui.fragment.ScanFragment
import com.shenrui.label.biaoqian.ui.fragment.SettingFragment
import com.xys.libzxing.zxing.activity.CaptureActivity
import kotlinx.android.synthetic.main.activity_biao_qian.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem
import org.jetbrains.anko.toast


class BiaoQianActivity : BaseActivity<BiaoQianContract.View,
        BiaoQianPresenter<BiaoQianContract.View>>(),
        BiaoQianContract.View {

    companion object {
        private val REQUEST_CODE = 100
    }

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
            //            if (mScanFragment == null){
//                mScanFragment = ScanFragment()
//            }
//            supportFragmentManager.beginTransaction().replace(R.id.content_frame,mScanFragment).commit()
            setScanPressed()
            val permissionItems = ArrayList<PermissionItem>()
            permissionItems.add(PermissionItem(Manifest.permission.CAMERA, "Camera", R.drawable.permission_ic_camera))
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
        home_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_homepage_pressed))
        setting_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_set_nor))
        scan_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_scan_nor))
    }

    /**
     * 首页被点击
     */
    private fun setSettingPressed() {
        home_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_homepage_nor))
        setting_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_set_pressed))
        scan_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_scan_nor))
    }

    /**
     * 首页被点击
     */
    private fun setScanPressed() {
        home_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_homepage_nor))
        setting_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_set_nor))
        scan_img.setImageDrawable(resources.getDrawable(R.mipmap.icon_scan_pressed))
    }

    /**
     * 打开默认二维码扫描界面
     */
    private fun goToScanActivity() {
        val intent = Intent(this@BiaoQianActivity, CaptureActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
//        Handler().postDelayed({
//            /**
//             * 只有当摄像头打开后才能，打开闪光灯
//             */
//            CodeUtils.isLightEnable(true)
//        }, 2000)
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
                val result = bundle?.getString("result")
                toast("解析结果:$result")
            }
        }
    }

    override fun onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed()
        }
    }


}
