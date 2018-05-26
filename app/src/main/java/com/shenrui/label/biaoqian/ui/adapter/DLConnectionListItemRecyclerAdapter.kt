package com.shenrui.label.biaoqian.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.model.bean.DLConnectionBean

/**
 * 电缆纤芯链接的 Adapter
 * @author Chengguo on 2018/5/26.
 */
class DLConnectionListItemRecyclerAdapter(private val context1: Context,
                                          private val list: ArrayList<DLConnectionBean>)
    : RecyclerView.Adapter<DLConnectionListItemRecyclerAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        //发送端
        holder.deviceInTv.text = item.fromDeviceName
        holder.deviceInSignalTv.text = item.fromSignal
        holder.deviceInPortTv.text = item.fromPortNo
        holder.deviceInSignalPaiTv.text = item.fromBoardNo
        //连线
        holder.deviceConnectTv.text = item.cableCoreNo.toString()
        //接收端
        holder.deviceOutSignalPaiTv.text = item.toBoardNo
        holder.deviceOutPortTv.text = item.toPortNo
        holder.deviceOutSignalTv.text = item.toSignal
        holder.deviceOutTv.text = item.toDeviceName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MyViewHolder(LayoutInflater.from(context1).inflate(
                    R.layout.recyclerview_item_list_dl_connection,
                    parent,
                    false))

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var deviceInTv: TextView = view.findViewById(R.id.tv_dl_device_in)
        var deviceInSignalTv: TextView = view.findViewById(R.id.tv_dl_device_in_signal)
        var deviceInPortTv: TextView = view.findViewById(R.id.tv_dl_device_in_port)
        var deviceInSignalPaiTv: TextView = view.findViewById(R.id.tv_dl_device_in_signal_pai)
        var deviceConnectTv: TextView = view.findViewById(R.id.tv_dl_device_connect)
        var deviceOutSignalPaiTv: TextView = view.findViewById(R.id.tv_dl_device_out_signal_pai)
        var deviceOutPortTv: TextView = view.findViewById(R.id.tv_dl_device_out_port)
        var deviceOutSignalTv: TextView = view.findViewById(R.id.tv_dl_device_out_signal)
        var deviceOutTv: TextView = view.findViewById(R.id.tv_dl_device_out)
    }


}