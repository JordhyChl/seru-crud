package com.seru.serujuragan.permission

/**
 * Created by adityaaugusta on 5/15/17.
 */
class PermissionListener(private val listener: OnPermissionChangeListener) {

    fun onPermissionUpdate(permType: Int, hasGrant: Boolean) {
        when (permType) {
            PermissionsConfig.CONTACT -> {
                PermissionPreference.hasContactGranted = hasGrant
                PermissionPreference.hasContactDNAA = false
            }
            PermissionsConfig.STORAGE -> {
                PermissionPreference.hasMediaGranted = hasGrant
                PermissionPreference.hasMediaDNAA = false
            }
            PermissionsConfig.CAMERA -> {
                PermissionPreference.hasCameraGranted = hasGrant
                PermissionPreference.hasCameraDNAA = false
            }
        }
        listener.hasPermissionGranted(permType, hasGrant)
    }

    fun onNeverAskAgain(permType: Int) {
        when (permType) {
            PermissionsConfig.CONTACT -> {
                PermissionPreference.hasContactGranted = false
                PermissionPreference.hasContactDNAA = true
            }
            PermissionsConfig.STORAGE -> {
                PermissionPreference.hasMediaGranted = false
                PermissionPreference.hasMediaDNAA = true
            }
            PermissionsConfig.CAMERA -> {
                PermissionPreference.hasCameraGranted = false
                PermissionPreference.hasCameraDNAA = true
            }
        }
        listener.hasDeniedNeverAskAgain(permType)
    }

    interface OnPermissionChangeListener {
        fun hasPermissionGranted(permission: Int, hasGranted: Boolean)
        fun hasDeniedNeverAskAgain(permission: Int)
    }

}