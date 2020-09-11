package com.github.nukc.permissionrer

interface PermissionRequestListener {
    /**
     * @param permissions 请求的权限
     * @param grants 对应权限返回的结果
     * @param shouldShowRequestPermissionRationales [androidx.fragment.app.Fragment.shouldShowRequestPermissionRationale]
     */
    fun onRequestPermissionsResult(
        permissions: Array<out String>,
        grants: BooleanArray,
        shouldShowRequestPermissionRationales: BooleanArray
    )
}