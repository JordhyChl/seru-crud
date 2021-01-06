package com.seru.serujuragan.permission

import android.Manifest

/**
 * Created by adityaaugusta on 9/7/16.
 */
object PermissionsConfig {

    const val ALL = 100
    const val CONTACT = 101
    const val STORAGE = 102
    const val CAMERA = 103
    const val SMS = 104
    const val CALL = 105
    const val LOCATION = 106
    const val CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE = 2011;

    val permissionContact = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CONTACTS)
    val permissionStorage = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

}
