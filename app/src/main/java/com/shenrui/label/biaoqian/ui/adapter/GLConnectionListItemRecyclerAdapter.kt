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
                                          private val list: ArrayList<GLConnectionBean>,
                                          private val listener: AddOnClickListener? = null)
    : RecyclerView.Adapter<GLConnectionListItemRecyclerAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.deviceInTv.text = item.inDeviceName
        val inTxTypeStr = if (item.odfConnection.internal_rt_type == 0) {
            "Rx"
        } else {
            "Tx"
        }
        holder.deviceInPortTv.text = item.odfConnection.internal_device_port + "/" + inTxTypeStr
        holder.deviceInTxTv.text = item.odfConnection.internal_optical_fiber_number
        holder.deviceInODFTv.text = item.odf.odf_layer + item.odf.odf_port
        holder.deviceConnectTv.text = item.odfConnection.optical_fiber_number.toString()

        holder.deviceOutODFTv.text = item.odfOut.odf_layer + item.odfOut.odf_port
        val outTxTypeStr = if (item.odfOutConnection.internal_rt_type == 0) {
            "Rx"
        } else {
            "Tx"
        }
        holder.deviceOutTxTv.text = item.odfOutConnection.internal_optical_fiber_number
        holder.deviceOutPortTv.text = item.odfOutConnection.internal_device_port + "/" + outTxTypeStr
        holder.deviceOutTv.text = item.outDeviceName
        holder.txDescTv.text = if (item.odfConnection.description.isEmpty()) "暂无" else item.odfConnection.description

        holder.deviceConnectTv.setOnClickListener {
            listener?.onItemClick(item)
        }
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

    interface AddOnClickListener {
        fun onItemClick(item: GLConnectionBean)
    }

}