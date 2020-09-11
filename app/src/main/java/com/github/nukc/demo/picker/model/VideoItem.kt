package com.github.nukc.demo.picker.model

import android.net.Uri
import com.github.nukc.scanner.model.BaseItem
import kotlinx.android.parcel.Parcelize

/**
 * @author Nukc.
 */
@Parcelize
data class VideoItem(
    override val id: Long,
    override val name: String?,
    override val path: String,
    override val size: Long,
    override val dateAdded: Long,
    override val dateModified: Long,
    val mimeType: String?,
    override val contentUri: Uri,
    override val bucketName: String
) : BaseItem, Selectable(), BaseMediaItem