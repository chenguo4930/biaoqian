package com.shenrui.label.biaoqian.mvp.model.bean

import android.os.Parcel
import android.os.Parcelable
import com.luckongo.tthd.mvp.model.bean.ODF
import com.luckongo.tthd.mvp.model.bean.ODFConnection

/**
 * 光缆连接图
 *
 * Created by cheng on 2018/3/25.
 */
data class GLConnectionBean(val inDeviceName: String,
                            val inDeviceId: String,
                            val inDeviceCode: String,
                            val outDeviceName: String,
                            val outDeviceId: String,
                            val outDeviceCode: String,
                            val outPanelName: String,
                            val odf: ODF,
                            val odfConnection: ODFConnection,
                            val odfOut: ODF,
                            val odfOutConnection: ODFConnection) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readParcelable<ODF>(ODF::class.java.classLoader),
            source.readParcelable<ODFConnection>(ODFConnection::class.java.classLoader),
            source.readParcelable<ODF>(ODF::class.java.classLoader),
            source.readParcelable<ODFConnection>(ODFConnection::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(inDeviceName)
        writeString(inDeviceId)
        writeString(inDeviceCode)
        writeString(outDeviceName)
        writeString(outDeviceId)
        writeString(outDeviceCode)
        writeString(outPanelName)
        writeParcelable(odf, 0)
        writeParcelable(odfConnection, 0)
        writeParcelable(odfOut, 0)
        writeParcelable(odfOutConnection, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<GLConnectionBean> = object : Parcelable.Creator<GLConnectionBean> {
            override fun createFromParcel(source: Parcel): GLConnectionBean = GLConnectionBean(source)
            override fun newArray(size: Int): Array<GLConnectionBean?> = arrayOfNulls(size)
        }
    }
}

