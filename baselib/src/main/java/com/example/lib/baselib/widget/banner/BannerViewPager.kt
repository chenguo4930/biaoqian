package com.example.lib.baselib.widget.banner

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.View
import java.util.*

/**
 * @Author huangyue
 * @Date 2018/05/08 17:07
 * @Description
 */
class BannerViewPager: ViewPager {

    private var hasCalledOnAttached = false
    private val childCenterXAbs = ArrayList<Int>()
    private val childIndex = SparseIntArray()

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init()
    }

    fun init() {
        clipToPadding = false
        overScrollMode = View.OVER_SCROLL_NEVER
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        hasCalledOnAttached = true
    }

    override fun onDetachedFromWindow() {
        // 为了解决ViewPager嵌套在列表视图中,消失时动画被强制中断导致页面切换卡在中间
        // 不调用父类方法
        if (false) super.onDetachedFromWindow()
    }

    /**
     * ViewPager每一页绘制的顺序
     * @param childCount
     * @param n
     * @return 第n个位置的child 的绘制索引
     */
    override fun getChildDrawingOrder(childCount: Int, n: Int): Int {
        if (n == 0 || childIndex.size() != childCount) {
            childCenterXAbs.clear()
            childIndex.clear()
            val viewCenterX = getViewCenterX(this)
            for (i in 0 until childCount) {
                var indexAbs = Math.abs(viewCenterX - getViewCenterX(getChildAt(i)))
                //两个距离相同，后来的那个做自增，从而保持abs不同
                if (childIndex.get(indexAbs) != null) {
                    ++indexAbs
                }
                childCenterXAbs.add(indexAbs)
                childIndex.append(indexAbs, i)
            }
            Collections.sort<Int>(childCenterXAbs)//1,0,2  0,1,2
        }
        //那个item距离中心点远一些，就先draw它。（最近的就是中间放大的item,最后draw）
        return childIndex.get(childCenterXAbs[childCount - 1 - n])
    }

    private fun getViewCenterX(view: View): Int {
        val array = IntArray(2)
        view.getLocationOnScreen(array)
        return array[0] + view.width / 2
    }

    /**
     * 如果ViewPager执行了[.onAttachedToWindow],需要重置[.mFirstLayout]变量
     */
    fun checkFirstLayout() {
        if (hasCalledOnAttached) {
            hasCalledOnAttached = false
            // ViewPager里有一个私有变量mFirstLayout，它表示是不是第一次显示布局，
            // 如果是true，则使用无动画的方式显示当前item，
            // 如果是false，则使用动画方式显示当前item。
            // 这里的修改是为了解决ViewPager嵌套在列表视图中,消失又显示时跳转无动画效果
            try {
                val mFirstLayout = ViewPager::class.java.getDeclaredField("mFirstLayout")
                mFirstLayout.isAccessible = true
                mFirstLayout.set(this, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}