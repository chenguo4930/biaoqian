package com.shenrui.label.biaoqian.app

import android.app.Application

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
    }
}