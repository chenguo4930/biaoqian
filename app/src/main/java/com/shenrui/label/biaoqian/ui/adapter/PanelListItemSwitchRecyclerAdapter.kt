package com.shenrui.label.biaoqian.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.luckongo.tthd.mvp.model.bean.Device
import com.luckongo.tthd.mvp.model.bean.Switch
import com.shenrui.label.biaoqian.R

/**
 * 区域region Adapter
 * @author Chengguo on 2018/3/5.
 */
class PanelListItemSwitchRecyclerAdapter(private val context1: Context,
                                         private val list: List<Switch>)
    : RecyclerView.Adapter<PanelListItemSwitchRecyclerAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nameTv.text = list[position].switch_name + list[position].switch_code
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MyViewHolder(LayoutInflater.from(context1).inflate(
                    R.layout.recyclerview_item_list_panel_rv_item_switch,
                    parent,
                    false))

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameTv: TextView = view.findViewById(R.id.tv_switch_name)
    }

}