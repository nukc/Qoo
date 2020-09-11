package com.github.nukc.demo.picker.model

import android.net.Uri
import android.os.Parcelable

interface BaseMediaItem : Parcelable {
    val id: Long
    val name: String?
    val path: String
    val size: Long
    val dateAdded: Long
    val dateModified: Long
    val contentUri: Uri
    val bucketName: String
}