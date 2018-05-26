package com.shenrui.label.biaoqian.mvp.model.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * 电缆连接图Bean
 * Created by cheng on 2018/5/25.
 * 设备	信号	端子	端子排
 *
 */
data class DLConnectionBean(val fromPanelName: String,
                            val fromDeviceName: String,
                            val fromSignal: String,
                            val fromPortNo: String,
                            val fromBoardNo: String,
                            val toPanelName: String,
                            val toDeviceName: String,
                            val toSignal: String,
                            val toPortNo: String,
                            val toBoardNo: String,
                            val cableCoreNo: Int)