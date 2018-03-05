package com.shenrui.label.biaoqian.mvp.presenter

import com.shenrui.label.biaoqian.mvp.base.BasePresenter
import com.shenrui.label.biaoqian.mvp.contract.BiaoQianContract

/**
 *
 * @author Chengguo on 2018/3/5.
 */
class BiaoQianPresenter<T> : BasePresenter<BiaoQianContract.View>(), BiaoQianContract.Presenter {

    override fun requestBanner() {
    }
}