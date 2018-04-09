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
class GLConnectionListItemRecyclerAdapter(private val context1: Context,
                                          private val list: ArrayList<GLConnectionBean>)
    : RecyclerView.Adapter<GLConnectionListItemRecyclerAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.deviceInTv.text = list[position].inDeviceName
        val inTxTypeStr = if (list[position].odfConnection.internal_rt_type == 0) {
            "Tx"
        } else {
            "Rx"
        }
        holder.deviceInPortTv.text = list[position].odfConnection.internal_device_port + "/" + inTxTypeStr
        holder.deviceInTxTv.text = list[position].odfConnection.internal_optical_fiber_number
        holder.deviceInODFTv.text = list[position].odf.odf_layer + list[position].odf.odf_port
        holder.deviceConnectTv.text = list[position].odfConnection.optical_fiber_number.toString()

        holder.deviceOutODFTv.text =  list[position].odfOut.odf_layer + list[position].odfOut.odf_port
        val outTxTypeStr = if (list[position].odfOutConnection.internal_rt_type == 0) {
            "Tx"
        } else {
            "Rx"
        }
        holder.deviceOutTxTv.text = list[position].odfOutConnection.internal_optical_fiber_number
        holder.deviceOutPortTv.text = list[position].odfOutConnection.internal_device_port + "/" + outTxTypeStr
        holder.deviceOutTv.text = list[position].outDeviceName
        holder.txDescTv.text = "暂无"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MyViewHolder(LayoutInflater.from(context1).inflate(
                    R.layout.recyclerview_item_list_gl_connection,
                    parent,
                    false))

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var deviceInTv: TextView = view.findViewById(R.id.tv_tx_device_in)
        var deviceInPortTv: TextView = view.findViewById(R.id.tv_tx_device_in_port)
        var deviceInTxTv: TextView = view.findViewById(R.id.tv_tx_device_in_tx)
        var deviceInODFTv: TextView = view.findViewById(R.id.tv_tx_device_in_odf)
        var deviceConnectTv: TextView = view.findViewById(R.id.tv_tx_device_connect)
        var deviceOutPortTv: TextView = view.findViewById(R.id.tv_tx_device_out_port)
        var deviceOutODFTv: TextView = view.findViewById(R.id.tv_tx_device_out_odf)
        var deviceOutTxTv: TextView = view.findViewById(R.id.tv_tx_device_out_tx)
        var deviceOutTv: TextView = view.findViewById(R.id.tv_tx_device_out)
        var txDescTv: TextView = view.findViewById(R.id.tv_tx_desc)
    }

}