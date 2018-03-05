package com.shenrui.label.biaoqian.mvp.contract

import com.shenrui.label.biaoqian.mvp.base.IBaseContract

/**
 *
 * @author Chengguo on 2018/3/5.
 */
interface BiaoQianContract{
    interface Model : IBaseContract.IBaseMode {

        /**
         * 用户名注册
         */
        fun registerUserName(name: String, password: String, inviteCode: String?)

        /**
         * 手机号注册
         */
        fun registerPhone(phone: String, password: String, code: String, inviteCode: String?)

        /**
         * 获取手机验证码
         */
        fun requestIdentifyingCode()
    }

    interface View : IBaseContract.IBaseView {
        fun verificationCode(code: String)

        fun onSuccessBanner(code: String)
    }

    interface Presenter : IBaseContract.IBasePresenter<View> {
        /**
         * 获取手机验证码
         */
        fun requestIdentifyingCode(param: Int,
                                   phone: String,
                                   password: String?,
                                   verificationCode: String?,
                                   inviteCode: String?)

        /**
         * 用户名或者手机号注册
         */
        fun registerUserNameOrPhone(registerType: String,
                                    nameOrPhone: String,
                                    password: String,
                                    inviteCode: String?)

        fun initVerificationCodeUtils()

        /**
         * 获取注册界面的banner
         */
        fun requestBanner()
    }
}