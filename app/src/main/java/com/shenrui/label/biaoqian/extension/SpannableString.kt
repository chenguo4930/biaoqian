package com.shenrui.label.biaoqian.extension

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan


fun SpannableString.setForegroundColor(color: Int, start: Int = 0, end: Int = this.length, type: Int =
Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        : SpannableString
        = this.apply {
    setSpan(ForegroundColorSpan(color), start, end, type)
}

fun SpannableString.setSizeSpan(size: Int, start: Int = 0, end: Int = this.length,
                                dp: Boolean = true): SpannableString
        = this.apply {
    setSpan(AbsoluteSizeSpan(size, dp), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun SpannableStringBuilder.setForegroundColor(color: Int, start: Int = 0, end: Int = this.length)
        : SpannableStringBuilder
        = this.apply {
    setSpan(ForegroundColorSpan(color), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun SpannableStringBuilder.appendColorSpan(append: String, color: Int)
        : SpannableStringBuilder
        = if (Build.VERSION.SDK_INT >= 21) {
    append(append, ForegroundColorSpan(color), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
} else {
    val start = length
    append(append).setForegroundColor(color, start)
}

fun SpannableStringBuilder.appendColorSpanRes(context: Context, append: String, resId: Int)
        : SpannableStringBuilder
        = if (Build.VERSION.SDK_INT >= 21) {
    append(append, ForegroundColorSpan(ContextCompat.getColor(context, resId)),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
} else {
    val start = length
    append(append).setForegroundColor(ContextCompat.getColor(context, resId), start)
}

fun SpannableStringBuilder.appendColorSpanRes(context: Context, append: Int, resId: Int)
        : SpannableStringBuilder
        = if (Build.VERSION.SDK_INT >= 21) {
    append(context.getString(append), ForegroundColorSpan(ContextCompat.getColor(context, resId)),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
} else {
    val start = length
    append(context.getString(append))
            .setForegroundColor(ContextCompat.getColor(context, resId), start)
}

fun SpannableStringBuilder.appendSizeSpan(append: String, size: Int, dp: Boolean = true)
        : SpannableStringBuilder
        = if (Build.VERSION.SDK_INT >= 21) {
    append(append, AbsoluteSizeSpan(size, dp), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
} else {
    val start = length
    append(append).setSizeSpan(size, start, dp = dp)
}

fun SpannableStringBuilder.appendSizeSpanRes(context: Context, resStringId: Int, size: Int,
                                             dp: Boolean = true)
        : SpannableStringBuilder
        = if (Build.VERSION.SDK_INT >= 21) {
    append(context.getString(resStringId), AbsoluteSizeSpan(size, dp),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
} else {
    val start = length
    append(context.getString(resStringId)).setSizeSpan(size, start, dp = dp)
}

fun SpannableStringBuilder.setForegroundColorRes(context: Context, resId: Int, start: Int = 0,
                                                 end: Int = this.length)
        : SpannableStringBuilder
        = this.apply {
    setSpan(ForegroundColorSpan(ContextCompat.getColor(context, resId)), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun SpannableStringBuilder.setBackgroundColorRes(context: Context, resId: Int, start: Int = 0,
                                                 end: Int = this.length)
        : SpannableStringBuilder
        = this.apply {
    setSpan(BackgroundColorSpan(ContextCompat.getColor(context, resId)), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun SpannableStringBuilder.setForegroundColor(color: Int, start: String, end: String = "")
        : SpannableStringBuilder
        = this.apply {
    setSpan(ForegroundColorSpan(color), this.indexOf(start),
            if (end.isNullOrEmpty().not()) this.indexOf(end) else this.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun SpannableStringBuilder.setForegroundColorRes(context: Context, resId: Int,
                                                 start: String, end: String = "")
        : SpannableStringBuilder
        = this.apply {
    setSpan(ForegroundColorSpan(ContextCompat.getColor(context, resId)), this.indexOf(start),
            if (end.isNullOrEmpty().not()) this.indexOf(end) else this.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun SpannableStringBuilder.setSizeSpan(size: Int, start: Int = 0, end: Int = this.length,
                                       dp: Boolean = true)
        : SpannableStringBuilder
        = this.apply {
    setSpan(AbsoluteSizeSpan(size, dp), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun SpannableStringBuilder.setStyleSpan(style: Int, start: Int = 0, end: Int = this.length)
        : SpannableStringBuilder
        = this.apply {
    setSpan(StyleSpan(style), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun CharSequence.setForegroundColorSpan(color: Int, start: Int = 0, end: Int = this.length, type: Int = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        = SpannableString(this).setForegroundColor(color, start, end, type)

fun CharSequence.setForegroundColorSpanBuilder(color: Int, start: Int = 0, end: Int = this.length)
        = SpannableStringBuilder(this).setForegroundColor(color, start, end)

fun CharSequence.setForegroundColorResSpan(context: Context, resId: Int,
                                           start: Int = 0, end: Int = this.length)
        = SpannableString(this).setForegroundColor(ContextCompat.getColor(context, resId), start, end)

fun CharSequence.setForegroundColorResSpanBuilder(context: Context, resId: Int,
                                                  start: Int = 0, end: Int = this.length)
        = SpannableStringBuilder(this).setForegroundColor(ContextCompat.getColor(context, resId),
        start, end)

fun CharSequence.setSizeSpan(size: Int, start: Int = 0, end: Int = this.length, dp: Boolean = true)
        = SpannableString(this).setSizeSpan(size, start, end, dp)

fun CharSequence.setSizeSpanBuilder(size: Int, start: Int = 0, end: Int = this.length,
                                    dp: Boolean = true)
        = SpannableStringBuilder(this).setSizeSpan(size, start, end, dp)

fun CharSequence.setStyleSpanBuilder(style: Int = Typeface.BOLD, start: Int = 0, end: Int = this.length)
        = SpannableStringBuilder(this).setStyleSpan(style, start, end)

fun CharSequence.setStyleSpan(size: Int, color: Int, start: Int = 0, end: Int = this.length,
                              dp: Boolean = true)
        = SpannableString(this).setSizeSpan(size, start, end, dp)
        .setForegroundColor(color, start, end)
