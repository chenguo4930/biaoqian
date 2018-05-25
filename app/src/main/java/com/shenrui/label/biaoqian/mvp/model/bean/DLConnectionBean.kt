package com.shenrui.label.biaoqian.mvp.model.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * 电缆连接图
 * Created by cheng on 2018/5/25.
 */
data class DLConnectionBean(val inDeviceName: String,
                            val inDeviceId: Int,
                            val inDeviceCode: String,
                            val outDeviceName: String,
                            val outDeviceId: Int,
                            val outDeviceCode: String,
                            val inputType: String,
                            val inPort: String,
                            val outPort: String,
                            val tailCableNumber: String,
                            val desc: String) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(inDeviceName)
        writeInt(inDeviceId)
        writeString(inDeviceCode)
        writeString(outDeviceName)
        writeInt(outDeviceId)
        writeString(outDeviceCode)
        writeString(inputType)
        writeString(inPort)
        writeString(outPort)
        writeString(tailCableNumber)
        writeString(desc)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DLConnectionBean> = object : Parcelable.Creator<DLConnectionBean> {
            override fun createFromParcel(source: Parcel): DLConnectionBean = DLConnectionBean(source)
            override fun newArray(size: Int): Array<DLConnectionBean?> = arrayOfNulls(size)
        }
    }
}
