package com.shenrui.label.biaoqian.ui.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.luckongo.tthd.mvp.model.bean.Panel
import com.luckongo.tthd.mvp.model.bean.SubStation
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.model.bean.RegionBean

/**
 * 区域region Adapter
 * @author Chengguo on 2018/3/5.
 */
class RegionListItemRecyclerAdapter(private val context1: Context,
                                    private val list: List<Panel>)
    : RecyclerView.Adapter<RegionListItemRecyclerAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nameTv.text = list[position].panel_name

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MyViewHolder(LayoutInflater.from(context1).
                    inflate(R.layout.recyclerview_item_list_region_rv_item_panel, parent, false))

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameTv: TextView = view.findViewById(R.id.tv_panel_name)
    }

}