package com.shenrui.label.biaoqian.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.model.bean.WLConnectionBean

/**
 * 区域region Adapter
 * @author Chengguo on 2018/3/5.
 */
class WLConnectionListItemRecyclerAdapter(private val context1: Context,
                                          private val list: ArrayList<WLConnectionBean>,
                                          private val listener: AddOnClickListener? = null)
    : RecyclerView.Adapter<WLConnectionListItemRecyclerAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        if (item.inDevice != null) {
            holder.deviceInTv.text = item.inDevice?.device_desc
            holder.deviceConnectTv.text = item.wlTailFiber.tail_fiber_number.toString()
            holder.deviceInPortTv.text = item.inDeviceConnection?.from_port + "/" + item.type
            if (item.type == "Tx") {
                holder.deviceOutPortTv.text = item.inDeviceConnection?.to_port+ "/Rx"
            } else {
                holder.deviceOutPortTv.text = item.inDeviceConnection?.to_port + "/Tx"
            }
            holder.txDescTv.text = item.wlTailFiber.tail_fiber_desc
            if (item.toDevice != null) {
                holder.deviceOutTv.text = item.toDevice?.device_desc
            } else {
                holder.deviceOutTv.text = item.toSwitch?.switch_name
            }
        } else {
            holder.deviceInTv.text = item.inSwitch?.switch_name
            holder.deviceConnectTv.text = item.wlTailFiber.tail_fiber_number.toString()
            holder.txDescTv.text = item.wlTailFiber.tail_fiber_desc
            holder.deviceInPortTv.text = item.inDeviceConnection?.from_port + "/" + item.type
            if (item.type == "Tx") {
                holder.deviceOutPortTv.text = item.inDeviceConnection?.to_port + "/Rx"
            } else {
                holder.deviceOutPortTv.text = item.inDeviceConnection?.to_port + "/Tx"
            }
            if (item.toDevice != null) {
                holder.deviceOutTv.text = item.toDevice?.device_desc
            } else {
                holder.deviceOutTv.text = item.toSwitch?.switch_name
            }
        }

        holder.deviceConnectTv.setOnClickListener {
            listener?.onItemClick(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MyViewHolder(LayoutInflater.from(context1).inflate(
                    R.layout.recyclerview_item_list_tx_connection,
                    parent,
                    false))

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var deviceInTv: TextView = view.findViewById(R.id.tv_tx_device_in)
        var deviceInPortTv: TextView = view.findViewById(R.id.tv_tx_device_in_port)
        var deviceConnectTv: TextView = view.findViewById(R.id.tv_tx_device_connect)
        var deviceOutPortTv: TextView = view.findViewById(R.id.tv_tx_device_out_port)
        var deviceOutTv: TextView = view.findViewById(R.id.tv_tx_device_out)
        var txDescTv: TextView = view.findViewById(R.id.tv_tx_desc)
    }

    interface AddOnClickListener {
        fun onItemClick(item: WLConnectionBean)
    }
}