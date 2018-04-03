package com.shenrui.label.biaoqian.mvp.model.bean

/**
 * 尾缆详细连接图
 *
 * Created by cheng on 2018/3/25.
 */
data class WLConnectionDetailBean(val inDeviceName: String, val inPort: String, val tailFiberNumber: Int,
                                  val outPort: String, val outDeviceName: String, val desc: String)