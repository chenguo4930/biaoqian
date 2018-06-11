package com.shenrui.label.biaoqian.mvp.model.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * 跳纤连接图
 * Created by cheng on 2018/3/25.
 */
data class TXConnectionBean(val inDeviceName: String,
                            val inDeviceId: Int,
                            val inDeviceCode: String,
                            val outDeviceName: String,
                            val outDeviceId: Int,
                            val outDeviceCode: String,
                            val inputType: String,
                            val inPort: String,
                            val outPort: String,
                            val tailCableNumber: String?,
                            val desc: String?,
                            val inType: String,
                            val toType: String) : Parcelable {
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
        writeString(inType)
        writeString(toType)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TXConnectionBean> = object : Parcelable.Creator<TXConnectionBean> {
            override fun createFromParcel(source: Parcel): TXConnectionBean = TXConnectionBean(source)
            override fun newArray(size: Int): Array<TXConnectionBean?> = arrayOfNulls(size)
        }
    }
}