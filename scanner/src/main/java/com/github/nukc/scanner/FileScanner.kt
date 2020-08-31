package com.github.nukc.scanner

import android.content.Context
import com.github.nukc.scanner.matcher.BasePathMatcher
import com.github.nukc.scanner.matcher.BaseUriMatcher
import com.github.nukc.scanner.matcher.Matcher
import com.github.nukc.scanner.model.BaseItem
import com.github.nukc.scanner.model.FileBox
import java.lang.ref.WeakReference

/**
 * @author Nukc.
 */
class FileScanner(private val context: WeakReference<Context>) {

    private val matchers = arrayListOf<Matcher<BaseItem>>()
    private var scanListener: OnScanListener? = null

    /**
     * Add a matcher to scan
     *
     * @param matcher [BaseUriMatcher] or [BasePathMatcher]
     * @return [FileScanner]
     */
    @Suppress("UNCHECKED_CAST")
    fun scan(matcher: Matcher<*>): FileScanner {
        if (matcher is BaseUriMatcher || matcher is BasePathMatcher) {
            matchers.add(matcher as Matcher<BaseItem>)
        } else {
            throw IllegalArgumentException("matcher must be extends BaseUriMatcher or BasePathMatcher")
        }
        return this
    }

    /**
     * Add matchers to scan
     *
     * @param matchers array of [BaseUriMatcher] or [BasePathMatcher]
     * @return [FileScanner]
     */
    fun scan(vararg matchers: Matcher<*>): FileScanner {
        matchers.forEach {
            scan(it)
        }
        return this
    }

    /**
     * @param listener [OnScanListener]
     */
    fun setScanListener(listener: OnScanListener): FileScanner {
        scanListener = listener
        return this
    }

    /**
     * Scan by the added matchers
     *
     * @return [FileBox]
     */
    suspend fun start(): FileBox {
        return ScanManager.scan(context.get()!!, scanListener, *matchers.toTypedArray())
    }

    companion object {
        /**
         * new [FileScanner]
         *
         * @param context [Context]
         * @return [FileScanner]
         */
        fun build(context: Context): FileScanner {
            return FileScanner(WeakReference(context))
        }
    }

}
