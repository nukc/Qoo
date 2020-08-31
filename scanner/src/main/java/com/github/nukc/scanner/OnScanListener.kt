package com.github.nukc.scanner

import com.github.nukc.scanner.model.BaseItem

/**
 * @author Nukc.
 */
interface OnScanListener {
    fun onScanStart()

    fun onScanning(item: BaseItem)

    fun onScanEnd()
}