package com.example.lib.baselib.utils

import android.util.Log

import org.json.JSONArray
import org.json.JSONObject

/**
 * Author  ZYH
 * Date    2018/1/24
 * Des     Logger
 */
object Logger {
    var DEBUG = true/*BuildConfig.DEBUG*///这个待定写出根据debug模式来
    const val TAG = "fungo_net_test"
    private const val MIN_STACK_OFFSET = 3
    private const val METHOD_COUNT = 1
    private const val JSON_INDENT = 4
    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
    private val CHUNK_SIZE = 4000

    @Synchronized private fun log(logType: Int, str: String, showHeader: Boolean) {
        var content = str
        if (DEBUG) {
            if (showHeader) {
                logHeaderContent()
            }

            content = "| " + content
            val bytes = content.toByteArray()
            val length = bytes.size

            var i = 0
            while (i < length) {
                val count = Math.min(length - i, CHUNK_SIZE)
                val msg = String(bytes, i, count)

                when (logType) {
                    Log.ERROR -> Log.e(TAG, msg)
                    Log.INFO -> Log.i(TAG, msg)
                    else -> Log.v(TAG, msg)
                }
                i += CHUNK_SIZE
            }
        }
    }

    private fun log(logType: Int, content: String) {
        log(logType, content, true)
    }

    fun i(content: String) {
        log(Log.INFO, content)
    }

    @JvmStatic
    fun i(args: Array<String>) {
        try {
            val stringBuilder = StringBuilder()
            for (arg in args) {
                stringBuilder.append(arg)
                stringBuilder.append(",")
            }
            i(stringBuilder.toString())
        } catch (throwable: Throwable) {
            e(throwable)
        }

    }

    fun i(content: Int) {
        i(content.toString())
    }

    fun d(tag: String, content: String) {
        if (DEBUG) {
            Log.d(tag, content)
        }
    }

    fun e(content: String) {
        log(Log.ERROR, content)
    }

    fun e(throwable: Throwable) {
        var msg = ""
        if (throwable.message != null) {
            msg = throwable.message!!
        }

        e(msg)
        throwable.printStackTrace()
    }

    fun json(json: String?) {
        if (DEBUG) {
            try {
                var message = json
                if (json == null || json.isEmpty()) {
                    message = ""
                } else if (json.startsWith("{")) {
                    val jsonObject = JSONObject(json)
                    message = jsonObject.toString(JSON_INDENT)
                } else if (json.startsWith("[")) {
                    val jsonArray = JSONArray(json)
                    message = jsonArray.toString(JSON_INDENT)
                }
                i(message!!)
            } catch (throwable: Throwable) {
                var msg = ""

                if (throwable.cause != null && throwable.cause!!.message != null) {
                    msg = throwable.cause!!.message!!
                }

                e(msg + "\n" + json)
            }

        }
    }

    private fun logHeaderContent() {
        try {
            val trace = Thread.currentThread().stackTrace
            val stackOffset = getStackOffset(trace)

            var stackEnd = stackOffset + METHOD_COUNT
            if (stackEnd >= trace.size) {
                stackEnd = trace.size - 1
            }

            //Log.i(TAG, "|------------------------------------------------------");
            Log.d(TAG, "|---Thread: " + Thread.currentThread().name + " ------------------------------------------------------")

            for (i in stackEnd downTo stackOffset) {

                val builder = StringBuilder()
                builder.append("| ")
                        .append(getSimpleClassName(trace[i].className))
                        .append("")
                        .append(trace[i].methodName)
                        .append(" ")
                        .append(" (")
                        .append(trace[i].fileName)
                        .append(":")
                        .append(trace[i].lineNumber)
                        .append(")")
                Log.d(TAG, builder.toString())
            }
        } catch (throwable: Throwable) {
            e(throwable)
        }

    }

    private fun getStackOffset(trace: Array<StackTraceElement>): Int {
        var start = -1

        for (i in MIN_STACK_OFFSET until trace.size) {
            val element = trace[i]
            val name = element.className

            if (name != Logger::class.java.name) {
                start = i
                break
            }
        }
        return start
    }

    private fun getSimpleClassName(name: String): String {
        val lastIndex = name.lastIndexOf("")
        return name.substring(lastIndex + 1)
    }
}
