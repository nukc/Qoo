package com.github.nukc.scanner.matcher

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.ArrayMap
import com.github.nukc.scanner.ScanProjection
import com.github.nukc.scanner.model.BaseItem

/**
 * 通过提供的 Uri 进行的匹配器，提供 Cursor 需要的参数
 * @see android.content.ContentResolver.query
 * @author Nukc.
 */
abstract class BaseUriMatcher<T : BaseItem> : Matcher<T> {

    protected val cacheColumn = ArrayMap<String, Int>()

    /**
     * The URI, using the content:// scheme, for the content to retrieve.
     */
    open fun provideUri(): Uri = MediaStore.Files.getContentUri("external")

    /**
     * A list of which columns to return. Passing null will
     * return all columns, which is inefficient.
     */
    open fun provideProjection(): Array<String> = ScanProjection.FILE_COLUMNS

    /**
     *  A filter declaring which rows to return, formatted as an
     *  SQL WHERE clause (excluding the WHERE itself). Passing null will
     *  return all rows for the given URI.
     */
    open fun provideSelection(): String? = null

    /**
     * You may include ?s in selection, which will be
     * replaced by the values from selectionArgs, in the order that they
     * appear in the selection. The values will be bound as Strings.
     */
    open fun provideSelectionArgs(): Array<String>? = null

    /**
     * How to order the rows, formatted as an SQL ORDER BY
     * clause (excluding the ORDER BY itself). Passing null will use the
     * default sort order, which may be unordered.
     */
    open fun provideSortOrder(): String? = null

    /**
     * fill Item
     * @see com.github.nukc.scanner.ScanManager.queryItems
     */
    abstract fun fill(cur: Cursor): T

    open fun cacheColumnIndices(cur: Cursor) {
        provideProjection().forEach {
            cacheColumn[it] = cur.getColumnIndexOrThrow(it)
        }
    }
}