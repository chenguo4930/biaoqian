package com.shenrui.label.biaoqian.ui.adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.luckongo.tthd.mvp.model.bean.SubStation
import com.shenrui.label.biaoqian.R


/**
 *
 * @author Chengguo on 2018/3/5.
 */
class HomeGridListAdapter(private val mItems: List<SubStation>,
                          private val mLayoutManager: GridLayoutManager,
                          private var mOnItemClickListener: StationClickListener? = null)
    : RecyclerView.Adapter<HomeGridListAdapter.ItemViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        val spanCount = mLayoutManager.spanCount
        return if (spanCount == SPAN_COUNT_ONE) {
            VIEW_TYPE_BIG
        } else {
            VIEW_TYPE_SMALL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View = if (viewType == VIEW_TYPE_BIG) {
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_list_home, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_grid_home, parent, false)
        }
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
//        val item = mItems[position % 4]
//        holder.nameTv.text = item.name
        holder.nameTv.text = mItems[position].sub_name

        holder.itemView.setOnClickListener {
            mOnItemClickListener?.onStationItemClick(mItems[position])
        }
        holder.deleteImg.setOnClickListener {
            mOnItemClickListener?.onDeleteItemClick(mItems[position])
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameTv: TextView = view.findViewById(R.id.name_tv)
        val deleteImg: ImageView = view.findViewById(R.id.img_delete)
    }

    companion object {
        const val SPAN_COUNT_ONE = 1
        const val SPAN_COUNT_FOUR = 3

        private const val VIEW_TYPE_SMALL = 1
        private const val VIEW_TYPE_BIG = 2
    }

    interface StationClickListener {    //自定义的接口
        fun onStationItemClick(item: SubStation)
        fun onDeleteItemClick(item: SubStation)
    }
}