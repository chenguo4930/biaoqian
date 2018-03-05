package com.shenrui.label.biaoqian.mvp.presenter

import com.shenrui.label.biaoqian.mvp.base.BasePresenter
import com.shenrui.label.biaoqian.mvp.contract.BiaoQianContract

/**
 *
 * @author Chengguo on 2018/3/5.
 */
class BiaoQianPresenter<T> : BasePresenter<BiaoQianContract.View>(), BiaoQianContract.Presenter {
    override fun requestIdentifyingCode(param: Int, phone: String, password: String?, verificationCode: String?, inviteCode: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerUserNameOrPhone(registerType: String, nameOrPhone: String, password: String, inviteCode: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initVerificationCodeUtils() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun requestBanner() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}