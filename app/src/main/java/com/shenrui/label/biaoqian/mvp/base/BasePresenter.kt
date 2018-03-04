package com.shenrui.label.biaoqian.mvp.base

import com.luckongo.tthd.mvp.base.IBaseContract
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference


abstract class BasePresenter<T : IBaseContract.IBaseView> : IBaseContract.IBasePresenter<T> {
    /**
     * 持有UI接口的弱引用
     */
    var mView: WeakReference<T>? = null
        private set

    private var compositeDisposable = CompositeDisposable()

    override fun attachView(mRootView: T) {
        this.mView = WeakReference(mRootView)
    }

    /**
     * 解绑
     */
    override fun detachView() {
        mView?.clear()
        mView = null

        //保证activity结束时取消所有正在执行的订阅
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    private val isViewAttached: Boolean
        get() = mView != null

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private class MvpViewNotAttachedException internal constructor() : RuntimeException("Please call IPresenter.attachView(IBaseView) before" + " requesting data to the IPresenter")


}