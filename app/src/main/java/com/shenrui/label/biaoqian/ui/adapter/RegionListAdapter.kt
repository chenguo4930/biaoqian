package com.shenrui.label.biaoqian.ui.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.luckongo.tthd.mvp.model.bean.SubStation
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.model.bean.RegionBean

/**
 * 区域region Adapter
 * @author Chengguo on 2018/3/5.
 */
class RegionListAdapter(private val context1: Context,
                        private val list: ArrayList<RegionBean>,
                        private val onItemClickListener: RegionClickListener? = null)
    : RecyclerView.Adapter<RegionListAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nameTv.text = list[position].region_name + "("+list[position].region_code+")"

        holder.panelRv.run {
            layoutManager = GridLayoutManager(context1, 3)
            adapter = RegionListItemRecyclerAdapter(context1, list[position].panel)
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.onRegionItemClick(list[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MyViewHolder(LayoutInflater.from(context1).inflate(
                    R.layout.recyclerview_item_list_region,
                    parent,
                    false))

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameTv: TextView = view.findViewById(R.id.tv_region_name)
        var panelRv: RecyclerView = view.findViewById(R.id.rv_region_panel)
    }

    interface RegionClickListener {    //自定义的接口
        fun onRegionItemClick(item: RegionBean)
    }
}