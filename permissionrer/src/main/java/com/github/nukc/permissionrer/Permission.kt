package com.github.nukc.permissionrer

/**
 * [name] 请求的权限
 * [granted] 对应返回的结果
 * [shouldShowRequestPermissionRationale] [androidx.fragment.app.Fragment.shouldShowRequestPermissionRationale]
 */
data class Permission(
    val name: String,
    var granted: Boolean = false,
    val shouldShowRequestPermissionRationale: Boolean = false
)