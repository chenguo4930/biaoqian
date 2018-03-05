package com.shenrui.label.biaoqian.mvp.contract

import com.shenrui.label.biaoqian.mvp.base.IBaseContract

/**
 *
 * @author Chengguo on 2018/3/5.
 */
interface BiaoQianContract{
    interface Model : IBaseContract.IBaseMode {

    }

    interface View : IBaseContract.IBaseView {

    }

    interface Presenter : IBaseContract.IBasePresenter<View> {

        /**
         * 获取注册界面的banner
         */
        fun requestBanner()
    }
}