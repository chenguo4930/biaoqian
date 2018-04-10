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
class PanelWLConnectionListItemRecyclerAdapter(private val context1: Context,
                                               private val list: List<WLConnectionBean>,
                                               private val onItemClickListener: WLConnectionClickListener? = null
) : RecyclerView.Adapter<PanelWLConnectionListItemRecyclerAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.panelTv.text = list[position].wlConnectionPanel
        holder.wlNameTv.text = list[position].wlTailFiber.tail_cable_number
        holder.itemView.setOnClickListener {
            onItemClickListener?.onWLConnectionItemClick(list[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MyViewHolder(LayoutInflater.from(context1).inflate(
                    R.layout.recyclerview_item_list_panel_connection_wl,
                    parent,
                    false))

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val panelTv: TextView = view.findViewById(R.id.tv_wl_panel_name)
        val wlNameTv: TextView = view.findViewById(R.id.tv_wl_name)
    }

    interface WLConnectionClickListener {    //自定义的接口
        fun onWLConnectionItemClick(item: WLConnectionBean)
    }

}