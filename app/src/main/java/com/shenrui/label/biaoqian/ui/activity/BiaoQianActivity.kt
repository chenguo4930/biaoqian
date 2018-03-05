package com.shenrui.label.biaoqian.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.base.BaseActivity
import com.shenrui.label.biaoqian.mvp.contract.BiaoQianContract
import com.shenrui.label.biaoqian.mvp.presenter.BiaoQianPresenter
import com.shenrui.label.biaoqian.ui.fragment.HomeFragment
import com.shenrui.label.biaoqian.ui.fragment.ScanFragment
import com.shenrui.label.biaoqian.ui.fragment.SettingFragment
import com.uuzuche.lib_zxing.activity.CaptureActivity
import com.uuzuche.lib_zxing.activity.CodeUtils
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
        home_btn.setOnClickListener {
            if (mHomeFragment == null) {
                mHomeFragment = HomeFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.content_frame, mHomeFragment).commit()
        }

        setting_btn.setOnClickListener {
            if (mSettingFragment == null) {
                mSettingFragment = SettingFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.content_frame, mSettingFragment).commit()
        }

        scan_btn.setOnClickListener {
            //            if (mScanFragment == null){
//                mScanFragment = ScanFragment()
//            }
//            supportFragmentManager.beginTransaction().replace(R.id.content_frame,mScanFragment).commit()
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
        Log.e("-------", "---------onActivityResult----0  resultCode=$resultCode")
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.e("-------", "---------onActivityResult----1")
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Log.e("-------", "---------onActivityResult----2")
                val bundle: Bundle? = data.extras ?: return
                Log.e("-------", "---------onActivityResult----3")
                if (bundle != null) {
                    Log.e("-------", "---------onActivityResult----4")
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        val result = bundle.getString(CodeUtils.RESULT_STRING)
                        toast("解析结果:$result")
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        toast("解析二维码失败")
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}
