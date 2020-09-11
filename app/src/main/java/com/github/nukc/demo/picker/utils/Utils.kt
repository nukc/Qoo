package com.github.nukc.demo.picker.utils

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

object Utils {
    private var width = 0
    private var height = 0

    private fun getDisplay(context: Context) {
        if (width <= 0 || height <= 0) {
            val dm: DisplayMetrics
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                val windowManager =
                    context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                dm = DisplayMetrics()
                // 获取真分辨率，API 17 以下可反射拿到
                windowManager.defaultDisplay.getRealMetrics(dm)
            } else { // 这个是减去了虚拟按键等后的显示分辨率
                val resources = context.resources
                dm = resources.displayMetrics
            }
            width = dm.widthPixels
            height = dm.heightPixels
        }
    }

    @JvmStatic
    fun getScreenWidth(context: Context): Int {
        getDisplay(context)
        return width
    }

    @JvmStatic
    fun getScreenHeight(context: Context): Int {
        getDisplay(context)
        return height
    }
}

