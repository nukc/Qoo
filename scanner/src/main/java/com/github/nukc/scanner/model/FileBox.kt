package com.github.nukc.scanner.model

import android.util.ArrayMap

/**
 * Record scan, delete times and items..
 * start field is set in start function [com.github.nukc.scanner.FileScanner.start]
 *
 * @author Nukc.
 */
class FileBox {
    var scanStartTime: Long = System.currentTimeMillis()
    var scanEndTime: Long = 0
    var array: ArrayMap<Class<BaseItem>, List<BaseItem>>? = null
}