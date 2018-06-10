package com.shenrui.label.biaoqian.ui.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.luckongo.tthd.mvp.model.bean.Inputs
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.model.bean.ConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.PanelBean

/**
 * 设备间信息连接的 Adapter 最后一个图
 * @author Chengguo on 2018/4/20.
 */
class ConnectionListItem2RecyclerAdapter(private val context1: Context,
                                         private val list: ArrayList<ConnectionBean>)
    : RecyclerView.Adapter<ConnectionListItem2RecyclerAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]

        holder.outTitleTv.text = item.outDeviceCode
        holder.outDeviceNameTv.text = item.outDeviceName
        holder.connectionRv.run {
            layoutManager = LinearLayoutManager(context1)
            adapter = ConnectionListItemRecyclerAdapter(context1, item.inputList)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MyViewHolder(LayoutInflater.from(context1).inflate(
                    R.layout.recyclerview_item_list_connection2,
                    parent,
                    false))

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val connectionRv: RecyclerView = view.findViewById(R.id.connectionRV)
        val outTitleTv: TextView = view.findViewById(R.id.tvOutModeTitle)
        val outDeviceNameTv: TextView = view.findViewById(R.id.tvOutDeviceName)
    }
}