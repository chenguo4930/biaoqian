package com.shenrui.label.biaoqian.app

import android.app.Application
import com.uuzuche.lib_zxing.activity.ZXingLibrary

/**
 * Created by cheng on 2018/3/4.
 */
class App : Application() {

    companion object {
        lateinit var mApplication: App
    }

    override fun onCreate() {
        super.onCreate()
        mApplication = this
        //初始化二维码扫描
        ZXingLibrary.initDisplayOpinion(this)
    }
}