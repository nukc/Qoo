package com.github.nukc.permissionrer

import androidx.fragment.app.FragmentActivity

/**
 * 权限请求
 */
object PermissionRer {
    private const val TAG = "PermissionRer"

    /**
     * 传入当前 [activity]，返回 [PermissionRequester]
     */
    fun with(activity: FragmentActivity): PermissionRequester {
        val fragmentManager = activity.supportFragmentManager
        var fragment = fragmentManager.findFragmentByTag(TAG) as PermissionRequestFragment?
        if (fragment == null) {
            fragment = PermissionRequestFragment()
            fragmentManager.beginTransaction()
                .add(fragment, TAG)
                .commitNowAllowingStateLoss()
        }
        return fragment
    }

}