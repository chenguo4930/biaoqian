package com.shenrui.label.biaoqian.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.model.bean.GLConnectionBean

/**
 * 区域region Adapter
 * @author Chengguo on 2018/3/5.
 */
class DLConnectionListItemRecyclerAdapter(private val context1: Context,
                                          private val list: ArrayList<GLConnectionBean>,
                                          private val listener: AddOnClickListener? = null)
    : RecyclerView.Adapter<DLConnectionListItemRecyclerAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.deviceInTv.text = item.inDeviceName
        val inTxTypeStr = if (item.odfConnection.internal_rt_type == 0) {
            "Rx"
        } else {
            "Tx"
        }
        holder.deviceInPortTv.text = item.odfConnection.internal_device_port + "/" + inTxTypeStr
        holder.deviceInSignalTv.text = item.odfConnection.internal_optical_fiber_number
        holder.deviceInSignalPaiTv.text = item.odf.odf_layer + item.odf.odf_port
        holder.deviceConnectTv.text = item.odfConnection.optical_fiber_number.toString()

        holder.deviceOutSignalPaiTv.text = item.odfOut.odf_layer + item.odfOut.odf_port
        val outTxTypeStr = if (item.odfOutConnection.internal_rt_type == 0) {
            "Rx"
        } else {
            "Tx"
        }
        holder.deviceOutSignalTv.text = item.odfOutConnection.internal_optical_fiber_number
        holder.deviceOutPortTv.text = item.odfOutConnection.internal_device_port + "/" + outTxTypeStr
        holder.deviceOutTv.text = item.outDeviceName

        holder.deviceConnectTv.setOnClickListener {
            listener?.onItemClick(item)
        }
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
        var deviceInTv: TextView = view.findViewById(R.id.tv_tx_device_in)
        var deviceInPortTv: TextView = view.findViewById(R.id.tv_tx_device_in_port)
        var deviceInSignalTv: TextView = view.findViewById(R.id.tv_tx_device_in_signal)
        var deviceInSignalPaiTv: TextView = view.findViewById(R.id.tv_tx_device_in_signal_pai)
        var deviceConnectTv: TextView = view.findViewById(R.id.tv_tx_device_connect)
        var deviceOutPortTv: TextView = view.findViewById(R.id.tv_tx_device_out_port)
        var deviceOutSignalPaiTv: TextView = view.findViewById(R.id.tv_tx_device_out_signal_pai)
        var deviceOutSignalTv: TextView = view.findViewById(R.id.tv_tx_device_out_signal)
        var deviceOutTv: TextView = view.findViewById(R.id.tv_tx_device_out)
    }

    interface AddOnClickListener {
        fun onItemClick(item: GLConnectionBean)
    }

}