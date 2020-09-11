package com.github.nukc.permissionrer

interface PermissionRequester {
    /**
     * 要请求的权限，执行该方法后便会立即执行 [androidx.fragment.app.Fragment.requestPermissions]
     * 可请求多个权限 [permissions]
     */
    fun request(vararg permissions: String): PermissionRequester

    /**
     * 订阅结果，传入一个 [PermissionRequestListener]
     */
    fun subscribe(listener: PermissionRequestListener)

    /**
     * 订阅结果， [names] 请求的权限数组，[grants] 对应权限数组的结果，[shouldShows] 对应权限数组（是否可以再次询问权限，false 是选中了拒绝再次询问）
     */
    fun subscribe(callback: (names: Array<out String>, grants: BooleanArray, shouldShows: BooleanArray) -> Unit)

    /**
     * 订阅结果，[granted] 是否允许，当为 false 时必有一个被拒绝
     */
    fun subscribe(callback: (granted: Boolean) -> Unit)

    /**
     * 订阅结果，[granted] 是否允许，当为 false 时必有一个被拒绝
     * [permissions] [Permission]组合
     */
    fun subscribe(callback: (granted: Boolean, permissions: Array<Permission>) -> Unit)
}