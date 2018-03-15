package com.shenrui.label.biaoqian.mvp.model.bean

import android.os.Parcel
import android.os.Parcelable
import com.luckongo.tthd.mvp.model.bean.Device
import com.luckongo.tthd.mvp.model.bean.Switch

/**
 * 屏柜bean
 *
 * Created by chengguo on 18-3-14.
 */
data class PanelBean(val panel_id: Int, val panel_name: String, val panel_code: String,
                     val region_id: Int, val device: List<Device>, val switch: List<Switch>) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.createTypedArrayList(Device.CREATOR),
            source.createTypedArrayList(Switch.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(panel_id)
        writeString(panel_name)
        writeString(panel_code)
        writeInt(region_id)
        writeTypedList(device)
        writeTypedList(switch)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PanelBean> = object : Parcelable.Creator<PanelBean> {
            override fun createFromParcel(source: Parcel): PanelBean = PanelBean(source)
            override fun newArray(size: Int): Array<PanelBean?> = arrayOfNulls(size)
        }
    }
}