package com.shenrui.label.biaoqian.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.model.bean.TXConnectionBean

/**
 * 区域region Adapter
 * @author Chengguo on 2018/3/5.
 */
class PanelTXConnectionListItemRecyclerAdapter(private val context1: Context,
                                               private val list: List<TXConnectionBean>,
                                               private val listener: TXConnectionClickListener? = null)
    : RecyclerView.Adapter<PanelTXConnectionListItemRecyclerAdapter.MyViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.deviceInTv.text = item.inDeviceName
        if (item.inputType == "Tx") {
            holder.deviceInPortTv.text = item.inPort + "/Tx"
            holder.deviceOutPortTv.text = item.outPort + "/Rx"
        } else {
            holder.deviceInPortTv.text = item.inPort + "/Rx"
            holder.deviceOutPortTv.text = item.outPort + "/Tx"
        }

        holder.deviceConnectTv.text = item.tailCableNumber

        holder.deviceOutTv.text = item.outDeviceName
        holder.txDescTv.text = item.desc

        holder.deviceConnectTv.setOnClickListener {
            listener?.onTXConnectionItemClick(item)
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

    interface TXConnectionClickListener {    //自定义的接口
        fun onTXConnectionItemClick(item: TXConnectionBean)
    }

}