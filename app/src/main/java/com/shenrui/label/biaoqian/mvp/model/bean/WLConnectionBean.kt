package com.shenrui.label.biaoqian.mvp.model.bean

import android.bluetooth.BluetoothClass
import android.os.Parcel
import android.os.Parcelable
import com.luckongo.tthd.mvp.model.bean.*

/**
 * 尾缆连接图
 *
 * Created by cheng on 2018/3/25.
 */
data class WLConnectionBean(val wlTailFiber: TailFiber, val wlConnectionPanel: String,
                            val inDevice: Device?, val inDeviceConnection: DeviceConnection?,
                            val inSwitch: Switch?, val inSwitchConnection: SwitchConnection?,
                            val toDevice: Device?, val toSwitch: Switch?) : Parcelable {
    constructor(source: Parcel) : this(
            source.readParcelable<TailFiber>(TailFiber::class.java.classLoader),
            source.readString(),
            source.readParcelable<Device>(Device::class.java.classLoader),
            source.readParcelable<DeviceConnection>(DeviceConnection::class.java.classLoader),
            source.readParcelable<Switch>(Switch::class.java.classLoader),
            source.readParcelable<SwitchConnection>(SwitchConnection::class.java.classLoader),
            source.readParcelable<Device>(Device::class.java.classLoader),
            source.readParcelable<Switch>(Switch::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(wlTailFiber, 0)
        writeString(wlConnectionPanel)
        writeParcelable(inDevice, 0)
        writeParcelable(inDeviceConnection, 0)
        writeParcelable(inSwitch, 0)
        writeParcelable(inSwitchConnection, 0)
        writeParcelable(toDevice, 0)
        writeParcelable(toSwitch, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<WLConnectionBean> = object : Parcelable.Creator<WLConnectionBean> {
            override fun createFromParcel(source: Parcel): WLConnectionBean = WLConnectionBean(source)
            override fun newArray(size: Int): Array<WLConnectionBean?> = arrayOfNulls(size)
        }
    }
}