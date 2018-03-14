package com.shenrui.label.biaoqian.ui.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.model.bean.PanelBean
import com.shenrui.label.biaoqian.mvp.model.bean.RegionBean

/**
 * 区域的panel Adapter
 * @author Chengguo on 2018/3/14.
 */
class PanelGridAdapter(private val context1: Context,
                       private val list: ArrayList<PanelBean>,
                       private val onItemClickListener: PanelClickListener? = null)
    : RecyclerView.Adapter<PanelGridAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nameTv.text = list[position].panel_name

        holder.panelRv.run {
            layoutManager = LinearLayoutManager(context1)
            adapter = PanelListItemRecyclerAdapter(context1, list[position].device)
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
        var nameTv: TextView = view.findViewById(R.id.rv_panel_panel)
        var panelRv: RecyclerView = view.findViewById(R.id.rv_panel_panel)
    }

    interface PanelClickListener {    //自定义的接口
        fun onPanelItemClick(item: PanelBean)
    }
}