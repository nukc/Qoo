package com.github.nukc.demo.picker.matcher

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.github.nukc.demo.picker.model.ImageItem
import com.github.nukc.scanner.matcher.BaseUriMatcher

/**
 * image matcher, eg: jpg, gif, png
 * @author Nukc.
 */
open class ImageMatcher : BaseUriMatcher<ImageItem>() {

    override fun provideUri(): Uri {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    override fun provideProjection(): Array<String> {
        return getProjection()
    }

    override fun provideSelection(): String? {
        return null
    }

    override fun provideSelectionArgs(): Array<String>? {
        return null
    }

    override fun provideSortOrder(): String? {
        return "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
    }

    override fun fill(cur: Cursor): ImageItem {
        val id = cur.getLong(cacheColumn[MediaStore.Images.Media._ID]!!)
        val contentUri: Uri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            id
        )
        return ImageItem(
            id = id,
            name = cur.getString(cacheColumn[MediaStore.Images.Media.DISPLAY_NAME]!!),
            path = cur.getString(cacheColumn[MediaStore.Images.Media.DATA]!!),
            size = cur.getLong(cacheColumn[MediaStore.Images.Media.SIZE]!!),
            dateAdded = cur.getLong(cacheColumn[MediaStore.Images.Media.DATE_ADDED]!!),
            dateModified = cur.getLong(cacheColumn[MediaStore.Images.Media.DATE_MODIFIED]!!),
            mimeType = cur.getString(cacheColumn[MediaStore.Images.Media.MIME_TYPE]!!),
            contentUri = contentUri,
            bucketName = cur.getString(cacheColumn[MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME]!!)
        )
    }


    private fun getProjection(): Array<String> {
        return arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Files.FileColumns.BUCKET_ID,
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
        )
    }

}