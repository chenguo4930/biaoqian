package com.shenrui.label.biaoqian.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.luckongo.tthd.mvp.model.bean.Inputs
import com.shenrui.label.biaoqian.R

/**
 * 设备间信息连接的 Adapter 最后一个图
 * @author Chengguo on 2018/4/20.
 */
class ConnectionListItemRecyclerAdapter(private val context1: Context,
                                        private val list: ArrayList<Inputs>)
    : RecyclerView.Adapter<ConnectionListItemRecyclerAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        if (item.isInput) {
            holder.inMsgTv.text = item.desc_from
            holder.outMsgTv.text = item.desc_to
        } else {
            holder.inMsgTv.text = item.desc_to
            holder.outMsgTv.text = item.desc_from
        }

        if (item.isInput) {
            holder.directionImg.setImageResource(R.drawable.ic_connection_right)
        } else {
            holder.directionImg.setImageResource(R.drawable.ic_connection_left)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MyViewHolder(LayoutInflater.from(context1).inflate(
                    R.layout.recyclerview_item_list_connection,
                    parent,
                    false))

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val inMsgTv: TextView = view.findViewById(R.id.inMsgTv)
        val outMsgTv: TextView = view.findViewById(R.id.outMsgTv)
        val directionImg: ImageView = view.findViewById(R.id.imgDirection)
    }
}