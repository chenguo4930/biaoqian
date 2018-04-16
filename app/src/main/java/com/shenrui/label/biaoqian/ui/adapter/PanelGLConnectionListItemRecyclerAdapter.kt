package com.shenrui.label.biaoqian.ui.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import com.luckongo.tthd.mvp.model.bean.Panel
import com.luckongo.tthd.mvp.model.bean.SubStation
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.model.bean.GLConnectionBean
import com.shenrui.label.biaoqian.mvp.model.bean.RegionBean
import com.shenrui.label.biaoqian.mvp.model.bean.WLConnectionBean

/**
 * 区域region Adapter
 * @author Chengguo on 2018/3/5.
 */
class PanelGLConnectionListItemRecyclerAdapter(private val context1: Context,
                                               private val list: List<GLConnectionBean>,
                                               private val onItemClickListener: GLConnectionClickListener? = null
) : RecyclerView.Adapter<PanelGLConnectionListItemRecyclerAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.panelTv.text = list[position].outPanelName
        holder.glNameTv.text = list[position].odfConnection.optical_cable_number
        holder.glLayout.setOnClickListener {
            onItemClickListener?.onGLConnectionItemClick(list[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MyViewHolder(LayoutInflater.from(context1).inflate(
                    R.layout.recyclerview_item_list_panel_connection_gl,
                    parent,
                    false))

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val panelTv: TextView = view.findViewById(R.id.tv_gl_panel_name)
        val glNameTv: TextView = view.findViewById(R.id.tv_gl_name)
        val glLayout: LinearLayout = view.findViewById(R.id.gl_layout)
    }

    interface GLConnectionClickListener {    //自定义的接口
        fun onGLConnectionItemClick(item: GLConnectionBean)
    }

}