package com.shenrui.label.biaoqian.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.model.bean.DLConnectionBean

/**
 * 屏柜电缆 Adapter
 * @author Chengguo on 2018/5/26.
 */
class PanelDLConnectionListItemRecyclerAdapter(private val context1: Context,
                                               private val list: List<DLConnectionBean>,
                                               private val onItemClickListener: DLConnectionClickListener? = null
) : RecyclerView.Adapter<PanelDLConnectionListItemRecyclerAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.panelTv.text = list[position].toPanelName
        holder.dlNameTv.text = list[position].cableNo
        holder.dlLayout.setOnClickListener {
            onItemClickListener?.onDLConnectionItemClick(list[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MyViewHolder(LayoutInflater.from(context1).inflate(
                    R.layout.recyclerview_item_list_panel_connection_dl,
                    parent,
                    false))

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val panelTv: TextView = view.findViewById(R.id.tv_dl_panel_name)
        val dlNameTv: TextView = view.findViewById(R.id.tv_dl_name)
        val dlLayout: LinearLayout = view.findViewById(R.id.dl_layout)
    }

    interface DLConnectionClickListener {    //自定义的接口
        fun onDLConnectionItemClick(item: DLConnectionBean)
    }

}