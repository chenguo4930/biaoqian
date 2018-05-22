package com.example.lib.baselib.widget.banner

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.RelativeLayout
import android.widget.Scroller
import com.example.lib.baselib.R
import com.example.lib.baselib.utils.ViewUtils
import java.lang.ref.WeakReference

/**
 * @Author huangyue
 * @Date 2018/05/08 16:35
 * @Description
 */
class BannerView: RelativeLayout {

    companion object {
        class MyHandler(bannerView: BannerView): Handler() {
            private var bannerView = WeakReference<BannerView>(bannerView)

            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                if (msg?.what == 0 && bannerView.get()?.mIsAutoPlay != null && bannerView.get()?.mIsAutoPlay as Boolean) {
                    // 执行切换操作前先判断ViewPager的私有变量mFirstLayout是否为false
                    // 如果不是则置为false
                    bannerView.get()?.mViewPager?.checkFirstLayout()
                    // 滚动Banner
                    val mCurrentItem = bannerView.get()?.mViewPager?.currentItem?.inc()
                    if (mCurrentItem == (bannerView.get()?.mAdapter as BannerAdapter).count - 1) {
                        bannerView.get()?.mViewPager?.setCurrentItem(bannerView.get()?.getStartSelectItem() ?: 0, false)
                    } else {
                        bannerView.get()?.mViewPager?.currentItem = mCurrentItem ?: bannerView.get()?.getStartSelectItem() ?: 0
                    }
                }
                sendEmptyMessageDelayed(0, bannerView.get()?.mDelayedTime?.toLong() ?: 3000)
            }
        }
    }

    private var mViewPager: BannerViewPager? = null
    private var mAdapter: BannerAdapter? = null
    private var mBannerListener: BannerListener? = null
    private var mHandler: MyHandler = MyHandler(this)
    private var mIsAutoPlay = false         // 是否自动播放
    private var pageSpace = 0               // 两个页面之间的间距
    private var mDelayedTime = 3000         // Banner 切换时间间隔
    private var mScrollDuration = 800       // ViewPager默认的最大Duration 为600,我们默认稍微大一点。值越大越慢。

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    fun init() {
        val view = LayoutInflater.from(context).inflate(R.layout.baselib_banner_layout, this)
        mViewPager = view.findViewById(R.id.viewPager)
        mViewPager?.offscreenPageLimit = 4
        // 添加页面切换监听
        mViewPager?.addOnPageChangeListener(mOnPageChangeListenerWrapper)
        // 设置ViewPager页面的间距
        mViewPager?.pageMargin = pageSpace
        // 初始化Scroller
        initViewPagerScroll()
    }

    /**
     * 设置ViewPager的滑动速度
     */
    private fun initViewPagerScroll() {
        try {
            val mScroller = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller.isAccessible = true
            //通过反射将自定义的Scroller设置到自定义的ViewPager中
            mScroller.set(mViewPager, ViewPagerScroller(context))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 选取banner开始轮播的起始位置
     * @return
     */
    fun getStartSelectItem(): Int {
        //我们设置当前选中的位置为Integer.MAX_VALUE / 2,这样开始就能往左滑动
        var currentItem = Integer.MAX_VALUE / 2
        val realCount = mAdapter?.getRealCount() ?: return 1
        //要保证这个值与getRealPosition 的 余数为0，因为要从第一页开始显示
        if (currentItem % realCount == 0) {
            return currentItem
        }
        //直到找到从0开始的位置
        while (currentItem % realCount != 0) {
            currentItem++
        }
        return currentItem
    }

    /**
     * *************************** Touch事件监听 ***************************
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            // 按住Banner的时候，停止自动轮播
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_DOWN -> {
                val paddingLeft = mViewPager?.left ?: 0
                val touchX = ev.rawX
                // 去除两边的区域
                if (touchX >= paddingLeft && touchX < ViewUtils.getScreenWidth(context) - paddingLeft && mIsAutoPlay) {
                    mHandler.removeMessages(0)
                }
                mBannerListener?.onPageDrag(true)
            }
            MotionEvent.ACTION_UP -> {
                mBannerListener?.onPageDrag(false)
                if (mIsAutoPlay && !mHandler.hasMessages(0))
                    mHandler.sendEmptyMessageDelayed(0, mDelayedTime.toLong())
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * *************************** 页面切换监听器 ***************************
     */
    private var mOnPageChangeListenerWrapper = object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            // 不能直接将mOnPageChangeListener 设置给ViewPager ,否则拿到的position 是原始的positon
            val realSelectPosition = position.rem(mAdapter?.getRealCount() ?: return)
            mBannerListener?.onPageSelected(realSelectPosition)
        }
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    }

    /**
     * 由于ViewPager 默认的切换速度有点快，因此用一个Scroller 来控制切换的速度
     * 而实际上ViewPager 切换本来就是用的Scroller来做的，因此我们可以通过反射来
     * 获取取到ViewPager 的 mScroller 属性，然后替换成我们自己的Scroller
     */
    inner class ViewPagerScroller(context: Context): Scroller(context) {
        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            this.startScroll(startX, startY, dx, dy)
        }
    }

    /**
     * Banner的点击回调
     */
    interface BannerListener {
        fun onPageDrag(flag: Boolean)
        fun onPageSelected(position: Int)
    }

    /***************************************************************************
     **                             对外API                                    **
     ***************************************************************************/

    /**
     * 开始轮播
     *
     * 应该确保在调用用了[{][.setPages] 之后调用这个方法开始轮播
     */
    fun start() {
        // 如果Adapter为null, 说明还没有设置数据，这个时候不应该轮播Banner
        if (mAdapter == null) {
            throw RuntimeException("The BannerView did not set up adapter")
        }
        mIsAutoPlay = true
        if (!mHandler.hasMessages(0)) {
            mHandler.sendEmptyMessageDelayed(0, mDelayedTime.toLong())
        }
    }

    /**
     * 暂停轮播
     */
    fun pause() {
        mIsAutoPlay = false
        mHandler.removeMessages(0)
    }

    /**
     * 设置ViewPager的Padding值,可以改变是否可以显示部分上下页内容
     *
     * @param padding
     */
    fun setViewPagerPadding(padding: Int) {
        mViewPager?.setPadding(padding, 0, padding, 0)
    }

    /**
     * 设置BannerView 的切换时间间隔
     *
     * @param delayedTime
     */
    fun setDelayedTime(delayedTime: Int) {
        mDelayedTime = delayedTime
    }

    /**
     * 设置Banner的监听
     *
     * @param bannerListener [BannerListener]
     */
    fun setBannerListener(bannerListener: BannerListener) {
        mBannerListener = bannerListener
    }

    /**
     * 返回ViewPager
     * @return
     */
    fun getViewPager(): ViewPager? {
        return mViewPager
    }

    /**
     * 设置数据，这是最重要的一个方法。
     * 其他的配置应该在这个方法之前调用
     * @param adapter
     */
    fun setPages(adapter: BannerAdapter) {
        //设置ViewPager的一个属性,避免长度为int的最大值时再次设置数据导致ANR
        try {
            val mFirstLayout = ViewPager::class.java.getDeclaredField("mFirstLayout")
            mFirstLayout.isAccessible = true
            mFirstLayout.set(mViewPager, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //如果在播放，就先让播放停止
        pause()
        //设置Adapter
        mAdapter = adapter
        mViewPager?.adapter = adapter
        mViewPager?.currentItem = getStartSelectItem()
    }

    fun setPageMargin(margin: Int) {
        mViewPager?.pageMargin = margin
    }

    /**
     * 设置ViewPager切换的速度
     * @param duration 切换动画时间
     */
    fun setDuration(duration: Int) {
        mScrollDuration = duration
    }

    /**
     * 设置ViewPager的页面切换动画
     */
    fun setPageTransformer(flag: Boolean, pageTransformer: ViewPager.PageTransformer?) {
        mViewPager?.setPageTransformer(flag, pageTransformer)
    }
}