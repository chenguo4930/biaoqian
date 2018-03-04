package com.shenrui.label.biaoqian.mvp.base

/**
 *
 * @author Chengguo on 2017/12/28.
 */
interface IBaseContract{
    interface IBaseMode{

    }

    interface IBaseView{
        fun <E> onError(e: E?)

        fun <D> onSuccess(d: D?)

        fun showLoading()

        fun dismissLoading()
    }

    interface IBasePresenter<in V : IBaseView> {

        fun attachView(mRootView: V)

        fun detachView()
    }
}