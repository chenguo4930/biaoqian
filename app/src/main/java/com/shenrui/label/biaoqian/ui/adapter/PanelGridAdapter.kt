package com.shenrui.label.biaoqian.ui.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.model.bean.PanelBean

/**
 * 区域的panel Adapter
 * @author Chengguo on 2018/3/14.
 */
class PanelGridAdapter(private val context1: Context,
                       private val list: ArrayList<PanelBean>,
                       private val onItemClickListener: PanelClickListener? = null)
    : RecyclerView.Adapter<PanelGridAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nameTv.text = list[position].panel_name + list[position].panel_code

        holder.panelDeviceRv.run {
            layoutManager = LinearLayoutManager(context1)
            adapter = PanelListItemDeviceRecyclerAdapter(context1, list[position].device)
        }

        holder.panelSwitchRv.run {
            layoutManager = LinearLayoutManager(context1)
            adapter = PanelListItemSwitchRecyclerAdapter(context1, list[position].switch)
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.onPanelItemClick(list[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MyViewHolder(LayoutInflater.from(context1).inflate(
                    R.layout.recyclerview_item_list_panel,
                    parent,
                    false))

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameTv: TextView = view.findViewById(R.id.tv_panel_name)
        var panelDeviceRv: RecyclerView = view.findViewById(R.id.rv_panel_device)
        var panelSwitchRv: RecyclerView = view.findViewById(R.id.rv_panel_switch)
    }

    interface PanelClickListener {    //自定义的接口
        fun onPanelItemClick(item: PanelBean)
    }
}