package com.github.nukc.scanner

import android.content.Context
import android.util.ArrayMap
import com.github.nukc.scanner.matcher.BasePathMatcher
import com.github.nukc.scanner.matcher.BaseUriMatcher
import com.github.nukc.scanner.matcher.Matcher
import com.github.nukc.scanner.model.BaseItem
import com.github.nukc.scanner.model.FileBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

/**
 * @author Nukc.
 */
object ScanManager {

    /**
     * Scan according to the matchers
     *
     * @param context [Context]
     * @param scanListener [OnScanListener]
     * @param matchers variable number of [Matcher]
     * @return [FileBox]
     */
    suspend fun scan(
        context: Context,
        scanListener: OnScanListener?,
        vararg matchers: Matcher<BaseItem>
    ): FileBox {
        val box = FileBox()
        box.scanStartTime = System.currentTimeMillis()
        scanListener?.onScanStart()

        val array = ArrayMap<Class<BaseItem>, List<BaseItem>>()
        withContext(Dispatchers.IO) {
            for (m in matchers) {
                if (!isActive) {
                    break
                }
                val items = when (m) {
                    is BaseUriMatcher -> queryItems(context, scanListener, m) { isActive }
                    is BasePathMatcher -> scanItems(scanListener, m) { isActive }
                    else -> throw IllegalArgumentException("Unknown matcher")
                }

                if (items.isNotEmpty()) {
                    @Suppress("UNCHECKED_CAST")
                    array[items[0]::class.java as Class<BaseItem>] = items
                }
            }
        }
        box.array = array
        box.scanEndTime = System.currentTimeMillis()
        scanListener?.onScanEnd()

        return box
    }

    /**
     * Query the cursor, returning items {@see [BaseItem]}
     *
     * @param context [Context]
     * @param scanListener [OnScanListener]
     * @param matcher [BaseUriMatcher]
     * @return [List][BaseItem]
     */
    private fun queryItems(
        context: Context,
        scanListener: OnScanListener?,
        matcher: BaseUriMatcher<BaseItem>,
        isActive: () -> Boolean
    ): List<BaseItem> {
        // Query the matcher provide URI
        val query = context.contentResolver.query(
            matcher.provideUri(),
            matcher.provideProjection(),
            matcher.provideSelection(),
            matcher.provideSelectionArgs(),
            matcher.provideSortOrder()
        )

        val items = mutableListOf<BaseItem>()
        query?.use { cursor ->
            matcher.cacheColumnIndices(cursor)
            while (cursor.moveToNext()) {
                if (!isActive()) {
                    break
                }
                val item = matcher.fill(cursor)
                if (matcher.filter(item)) {
                    items.add(item)
                    scanListener?.onScanning(item)
                }
            }
        }

        return items
    }

    /**
     * Scan matcher provide paths
     *
     * @param scanListener [OnScanListener]
     * @param matcher [BasePathMatcher]
     * @return [List][BaseItem]
     */
    private fun scanItems(
        scanListener: OnScanListener?,
        matcher: BasePathMatcher<BaseItem>,
        isActive: () -> Boolean
    ): List<BaseItem> {
        val items = mutableListOf<BaseItem>()
        matcher.providePaths()
            .forEach { path ->
                if (!isActive()) {
                    return@forEach
                }
                val item = matcher.fill(path)
                if (matcher.filter(item)) {
                    items += item
                    scanListener?.onScanning(item)
                }
            }
        return items
    }
}