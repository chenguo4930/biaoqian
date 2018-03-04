package com.luckongo.tthd.mvp.model.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * 充值类型
 * @author Chengguo on 2018/1/20.
 */
data class RechargeTypeBean(var name: String,
                            var showName: String,
                            var icon: String?,
                            var description: String,
                            var pid: Int, // 1 阿里网页支付  2 阿里app支付 3 和付支付I
                            var openUrl: String?,
                            var showIfNotAcquaint: Int) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readString(),
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(showName)
        writeString(icon)
        writeString(description)
        writeInt(pid)
        writeString(openUrl)
        writeInt(showIfNotAcquaint)
    }

    companion object {
        // 1 阿里网页支付  2 阿里app支付 3 和付支付I
        val WECHAT_TYPE = 1
        val ALIPAY_TYPE = 2
        val HFUPAY_TYPE = 3
        @JvmField
        val CREATOR: Parcelable.Creator<RechargeTypeBean> = object : Parcelable.Creator<RechargeTypeBean> {
            override fun createFromParcel(source: Parcel): RechargeTypeBean = RechargeTypeBean(source)
            override fun newArray(size: Int): Array<RechargeTypeBean?> = arrayOfNulls(size)
        }
    }
}