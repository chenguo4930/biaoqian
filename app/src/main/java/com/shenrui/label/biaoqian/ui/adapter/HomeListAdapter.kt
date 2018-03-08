package com.shenrui.label.biaoqian.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.luckongo.tthd.mvp.model.bean.SubStation
import com.shenrui.label.biaoqian.R

/**
 *
 * @author Chengguo on 2018/3/5.
 */
class HomeListAdapter(context2: Context, list2: ArrayList<SubStation>) : RecyclerView.Adapter<HomeListAdapter.MyViewHolder>() {

    var context = context2    //接收变量
    private val list = list2
    var inter: MyInter? = null

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = list[position].sub_name

        holder.itemView.setOnClickListener {
            inter?.testStr(list[position].sub_name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recyclerview_item_list_home, parent, false))

    fun setOnclickliseer(inter: MyInter) {
        this.inter = inter
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name_tv)
    }

    interface MyInter {    //自定义的接口（测试接口方式的使用）
        fun testStr(str: String)
    }
}