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
                            val cableNo: String,
                            val cableCoreNo: Int,
                            val internal_port_type: Int) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(fromPanelName)
        writeString(fromDeviceName)
        writeString(fromSignal)
        writeString(fromPortNo)
        writeString(fromBoardNo)
        writeString(toPanelName)
        writeString(toDeviceName)
        writeString(toSignal)
        writeString(toPortNo)
        writeString(toBoardNo)
        writeString(cableNo)
        writeInt(cableCoreNo)
        writeInt(internal_port_type)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DLConnectionBean> = object : Parcelable.Creator<DLConnectionBean> {
            override fun createFromParcel(source: Parcel): DLConnectionBean = DLConnectionBean(source)
            override fun newArray(size: Int): Array<DLConnectionBean?> = arrayOfNulls(size)
        }
    }
}
