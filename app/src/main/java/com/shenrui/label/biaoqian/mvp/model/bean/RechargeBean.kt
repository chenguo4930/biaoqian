package com.luckongo.tthd.mvp.model.bean

/**
 *
 * @author Chengguo on 2018/1/12.
 */
data class RechargeBean(var msg: String,
                        var error: Int,
                        var data: Data?) {
    data class Data(var gateway: String,
                    var params: String)
}