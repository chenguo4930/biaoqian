package com.shenrui.label.biaoqian.mvp.model.bean

/**
 * 跳纤连接图
 * Created by cheng on 2018/3/25.
 */
data class TXConnectionBean(val inDeviceName: String, val inPort: String, val tailCableNumber: String,
                            val outPort: String, val outDeviceName: String, val desc: String)