package com.github.nukc.demo.picker.model

import android.net.Uri

/**
 * 专辑
 */
data class Album(val name: String, var count: Int, var uri: Uri) : Selectable() {

    override fun equals(other: Any?): Boolean {
        if (other is Album) {
            return name == other.name
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}