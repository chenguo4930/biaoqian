package com.shenrui.label.biaoqian.mvp.model.bean

import com.luckongo.tthd.mvp.model.bean.Inputs

/**
 * 跳纤连接图
 * Created by cheng on 2018/3/25.
 */
data class ConnectionBean(val outDeviceName: String,
                          val outDeviceCode: String,
                          val inputList: ArrayList<Inputs>)