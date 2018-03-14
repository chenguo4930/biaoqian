package com.shenrui.label.biaoqian.mvp.model.bean

import android.os.Parcel
import android.os.Parcelable
import com.luckongo.tthd.mvp.model.bean.Panel

/**
 * 区域bean
 * Created by chengguo on 18-3-13.
 */
data class RegionBean(val region_id: Int, val region_name: String,
                      val region_code: String, val panel: List<Panel>) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            source.readString(),
            source.createTypedArrayList(Panel.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(region_id)
        writeString(region_name)
        writeString(region_code)
        writeTypedList(panel)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<RegionBean> = object : Parcelable.Creator<RegionBean> {
            override fun createFromParcel(source: Parcel): RegionBean = RegionBean(source)
            override fun newArray(size: Int): Array<RegionBean?> = arrayOfNulls(size)
        }
    }
}