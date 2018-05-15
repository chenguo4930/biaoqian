package com.example.lib.baselib.widget.banner

import android.support.v4.view.ViewPager
import android.view.View

/**
 * @author huangyue
 * @version V1.0
 * @Package org.fungo.a8sport.baseuilib.view.bannerv2
 * @Description: ViewPager页面的变换
 * @date 2018/02/06 15:29
 */

class CoverModeTransformer(private val mViewPager: ViewPager?) : ViewPager.PageTransformer {

    private var reduceX = 0.0f
    private var itemWidth = 0f
    private var offsetPosition = 0f
    private val mCoverWidth: Int = 0
    private val mScaleMax = 1.0f
    private val mScaleMin = 0.9f

    override fun transformPage(view: View, position: Float) {
        if (mViewPager == null) {
            return
        }
        if (offsetPosition == 0f) {
            val paddingLeft = mViewPager.paddingLeft.toFloat()
            val paddingRight = mViewPager.paddingRight.toFloat()
            val width = mViewPager.measuredWidth.toFloat()
            offsetPosition = paddingLeft / (width - paddingLeft - paddingRight)
        }
        val currentPos = position - offsetPosition
        if (itemWidth == 0f) {
            itemWidth = view.width.toFloat()
            //由于左右边的缩小而减小的x的大小的一半
            reduceX = (2.0f - mScaleMax - mScaleMin) * itemWidth / 2.0f
        }
        if (currentPos <= -1.0f) {
            view.translationX = reduceX + mCoverWidth
            view.scaleX = mScaleMin
            view.scaleY = mScaleMin
        } else if (currentPos <= 1.0) {
            val scale = (mScaleMax - mScaleMin) * Math.abs(1.0f - Math.abs(currentPos))
            val translationX = currentPos * -reduceX
            when {
                currentPos <= -0.5 -> //两个view中间的临界，这时两个view在同一层，左侧View需要往X轴正方向移动覆盖的值()
                    view.translationX = translationX + mCoverWidth * Math.abs(Math.abs(currentPos) - 0.5f) / 0.5f
                currentPos <= 0.0f -> view.translationX = translationX
                currentPos >= 0.5 -> //两个view中间的临界，这时两个view在同一层
                    view.translationX = translationX - mCoverWidth * Math.abs(Math.abs(currentPos) - 0.5f) / 0.5f
                else -> view.translationX = translationX
            }
            view.scaleX = scale + mScaleMin
            view.scaleY = scale + mScaleMin
        } else {
            view.scaleX = mScaleMin
            view.scaleY = mScaleMin
            view.translationX = -reduceX - mCoverWidth
        }
    }
}