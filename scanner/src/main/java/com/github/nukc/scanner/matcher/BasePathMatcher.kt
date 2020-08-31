package com.github.nukc.scanner.matcher

import com.github.nukc.scanner.model.BaseItem

/**
 * 根据提供的路径进行的匹配器
 * @author Nukc.
 */
interface BasePathMatcher<T : BaseItem> : Matcher<T> {

    /**
     * Dir or file path
     */
    fun providePaths() : Array<String>

    /**
     * fill Item
     * @see com.github.nukc.scanner.ScanManager.scanItems
     */
    fun fill(path: String): T
}