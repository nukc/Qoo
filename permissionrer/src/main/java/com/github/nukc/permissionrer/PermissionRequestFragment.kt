package com.github.nukc.permissionrer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment

/**
 * 当请求权限的时候会 add 到当前 Activity，不可见的 Fragment
 */
class PermissionRequestFragment : Fragment(), PermissionRequester {

    private var requestListener: PermissionRequestListener? = null
        set(value) {
            field = value
            request()
        }
    private var permissions: Array<out String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroy() {
        super.onDestroy()
        requestListener = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            val size = permissions.size
            val grants = BooleanArray(size)
            val shouldShows = BooleanArray(size)
            for ((index, element) in permissions.withIndex()) {
                grants[index] = grantResults[index] == PackageManager.PERMISSION_GRANTED
                shouldShows[index] = shouldShowRequestPermissionRationale(element)
            }
            requestListener?.onRequestPermissionsResult(permissions, grants, shouldShows)
        }
    }

    override fun request(vararg permissions: String): PermissionRequester {
        this.permissions = permissions
        return this
    }

    override fun subscribe(listener: PermissionRequestListener) {
        requestListener = listener
    }

    override fun subscribe(callback: (names: Array<out String>, grants: BooleanArray, shouldShows: BooleanArray) -> Unit) {
        requestListener = object : PermissionRequestListener {
            override fun onRequestPermissionsResult(
                permissions: Array<out String>,
                grants: BooleanArray,
                shouldShowRequestPermissionRationales: BooleanArray
            ) {
                callback(permissions, grants, shouldShowRequestPermissionRationales)
            }
        }
    }

    override fun subscribe(callback: (granted: Boolean) -> Unit) {
        requestListener = object : PermissionRequestListener {
            override fun onRequestPermissionsResult(
                permissions: Array<out String>,
                grants: BooleanArray,
                shouldShowRequestPermissionRationales: BooleanArray
            ) {
                grants.forEach {
                    if (!it) {
                        callback(false)
                        return
                    }
                }
                callback(true)
            }
        }
    }

    override fun subscribe(callback: (granted: Boolean, permissions: Array<Permission>) -> Unit) {
        requestListener = object : PermissionRequestListener {
            override fun onRequestPermissionsResult(
                permissions: Array<out String>,
                grants: BooleanArray,
                shouldShowRequestPermissionRationales: BooleanArray
            ) {
                val size = permissions.size
                val result = arrayOfNulls<Permission>(size)
                var granted = true
                for (i in 0 until size) {
                    if (granted && !grants[i]) {
                        granted = false
                    }
                    result[i] = Permission(
                        permissions[i],
                        grants[i],
                        shouldShowRequestPermissionRationales[i]
                    )
                }
                @Suppress("UNCHECKED_CAST")
                callback(
                    granted,
                    result as Array<Permission>
                )
            }
        }
    }

    private fun request() {
        permissions?.let {
            if (it.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                val grant =
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        Settings.canDrawOverlays(context)
                    } else {
                        true
                    }
                requestListener?.onRequestPermissionsResult(
                    arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW),
                    booleanArrayOf(grant),
                    booleanArrayOf(!grant)
                )
            } else {
                requestPermissions(it, REQUEST_CODE)
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 312
    }
}