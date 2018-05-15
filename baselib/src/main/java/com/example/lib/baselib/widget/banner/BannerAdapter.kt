package com.example.lib.baselib.widget.banner

import android.content.Context
import android.support.annotation.IdRes
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @Author huangyue
 * @Date 2018/05/08 18:12
 * @Description
 */
class BannerAdapter: PagerAdapter(){

    private var mList = mutableListOf<Any>()
    private var mBannerLinkMap = mutableMapOf<Class<*>, Any>()

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view === any
    }

    override fun getCount(): Int {
        // 返回int的最大值
        return Int.MAX_VALUE
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = getView<Any>(position)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
    }

    /**
     * 返回展示的View
     * @param position
     * @return
     */
    private fun <M: Any> getView(position: Int): View {
        val realPosition = position % getRealCount()
        // create holder
        val data: M = mList[realPosition] as M
        val bannerLink = (mBannerLinkMap[data.javaClass] as? (M) -> BannerHolder<M>) ?: throw RuntimeException("can not return a null holder")
        val holder: BannerHolder<M> = bannerLink(data)
        holder.initHolder()
        holder.setData(data)
        return holder.rootView
    }

    /**
     * 获取真实的Count
     * @return
     */
    fun getRealCount(): Int {
        return mList.size
    }

    /**
     * 将实体与Holder绑定
     */
    fun <T> register(dataClass: Class<T>, bannerLink: (T) -> BannerHolder<T>) {
        mBannerLinkMap[dataClass] = bannerLink
    }

    /**
     * 添加数据
     */
    fun add(any: Any) {
        mList.add(any)
        notifyDataSetChanged()
    }

    /**
     * 添加数据
     */
    fun addAll(collection: Collection<Any>) {
        mList.addAll(collection)
        notifyDataSetChanged()
    }

    abstract class BannerHolder<T>(context: Context, layoutId: Int) {

        var rootView: View = LayoutInflater.from(context).inflate(layoutId, null)

        protected fun <V : View> findView(@IdRes id: Int): V? {
            return rootView.findViewById(id)
        }

        open fun initHolder() {}
        open fun setData(data: T) {}
    }
}