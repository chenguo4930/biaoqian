package com.shenrui.label.biaoqian.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.constrant.AllSubStation.Companion.subStation
import com.shenrui.label.biaoqian.extension.logE
import com.shenrui.label.biaoqian.mvp.base.BaseActivity
import com.shenrui.label.biaoqian.mvp.contract.BiaoQianContract
import com.shenrui.label.biaoqian.mvp.model.bean.PanelBean
import com.shenrui.label.biaoqian.mvp.presenter.BiaoQianPresenter
import com.shenrui.label.biaoqian.ui.fragment.HomeFragment
import com.shenrui.label.biaoqian.ui.fragment.ScanFragment
import com.shenrui.label.biaoqian.ui.fragment.SettingFragment
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import com.xys.libzxing.zxing.activity.CaptureActivity
import kotlinx.android.synthetic.main.activity_biao_qian.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem
import org.jetbrains.anko.toast
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class BiaoQianActivity : BaseActivity<BiaoQianContract.View,
        BiaoQianPresenter<BiaoQianContract.View>>(),
        BiaoQianContract.View {

    companion object {
        private const val REQUEST_CODE = 100
    }

    private var mHomeFragment: HomeFragment? = null
    private var mSettingFragment: SettingFragment? = null
    private var mScanFragment: ScanFragment? = null
    private var mDbPath: String? = null // 数据库路径

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
        if (mHomeFragment == null) {
            mHomeFragment = HomeFragment()
        }
        supportFragmentManager.beginTransaction().replace(R.id.content_frame, mHomeFragment).commit()
        setHomePressed()
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
            permissionItems.add(PermissionItem(Manifest.permission.CAMERA, "开启摄像头", R.drawable.permission_ic_camera))
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
        //关掉当前Activity，回到主页
        finish()
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
                analysisResult(result)
                toast("解析结果:$result")
            }
        }
    }

    /**
     * 解析扫描二维码的结果
     * result ：JSNJ22TSB/GL1101/2N
     */
    private fun analysisResult(result: String?) {
        if (result == null) {
            toast("扫描数据为空，请检查二维码是否有效。")
            return
        }
        val resultArray = result.split("/")
        if (resultArray.size != 3) {
            toast("二维码数据格式有误")
            return
        }
        var subStationName: String = ""
        //变电站电压等级编号表JSNJ22TSB 22后面的TSB是变电站的简称，要把它解析出来
        when {
            result.indexOf("75") != -1 -> {
                subStationName = result.substring(result.indexOf("75") + 2)
            }
            result.indexOf("50") != -1 -> {
                subStationName = result.substring(result.indexOf("50") + 2)
            }
            result.indexOf("33") != -1 -> {
                subStationName = result.substring(result.indexOf("33") + 2)
            }
            result.indexOf("22") != -1 -> {
                subStationName = result.substring(result.indexOf("22") + 2)
            }
            result.indexOf("11") != -1 -> {
                subStationName = result.substring(result.indexOf("11") + 2)
            }
            result.indexOf("66") != -1 -> {
                subStationName = result.substring(result.indexOf("66") + 2)
            }
            result.indexOf("35") != -1 -> {
                subStationName = result.substring(result.indexOf("35") + 2)
            }
            result.indexOf("10") != -1 -> {
                subStationName = result.substring(result.indexOf("10") + 2)
            }
        }
        logE("----------变电站缩写subStationName = $subStationName")
        if (subStationName == "") {
            toast("二维码解析出变电站缩写为空，请检查二维码是否正确")
            return
        }

        //通过变电站缩写，从变电站集合中找出变电站，并获取到该变电站的数据库路径
        for (it in subStation!!) {
            if (it.sub_short_name == subStationName) {
                mDbPath = it.db_path
                break
            }
        }

        if (resultArray[1].startsWith("WL")) {
            //如果是尾缆
            searchData("WL")
        } else if (resultArray[1].startsWith("GL")) {
            //如果是光缆
            searchData("GL")
        }

    }

    private fun searchData(type: String) {
        if (type == "GL") {
            Observable.create(Observable.OnSubscribe<String> {
                val deviceList = DataBaseUtil.getDevice(mDbPath!!)
                val switchList = DataBaseUtil.getSwitch(mDbPath!!)
                val panelList = ArrayList<PanelBean>()

                it.onCompleted()
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<String>() {
                        override fun onCompleted() {
//                        toast("成功读取数据库")
                        }

                        override fun onError(e: Throwable) {
                            toast("读取数据库失败，请检查数据库是否存在")
                        }

                        override fun onNext(dataList: String) {

                        }
                    })
        } else if (type == "WL") {
            Observable.create(Observable.OnSubscribe<String> {
                val deviceList = DataBaseUtil.getDevice(mDbPath!!)
                val switchList = DataBaseUtil.getSwitch(mDbPath!!)
                val panelList = ArrayList<PanelBean>()

                it.onCompleted()
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<String>() {
                        override fun onCompleted() {
//                        toast("成功读取数据库")
                        }

                        override fun onError(e: Throwable) {
                            toast("读取数据库失败，请检查数据库是否存在")
                        }

                        override fun onNext(dataList: String) {

                        }
                    })
        }
    }

    override fun onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed()
        }
    }
}
