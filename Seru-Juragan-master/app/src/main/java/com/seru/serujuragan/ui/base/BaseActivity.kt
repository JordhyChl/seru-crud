package com.seru.serujuragan.ui.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.seru.serujuragan.permission.PermissionListener
import com.seru.serujuragan.permission.PermissionsConfig

/**
 * Created by Mahendra Dev on 28/12/2019
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), PermissionListener.OnPermissionChangeListener {

    private lateinit var permissionListener: PermissionListener

    fun checkPermission(activity: Activity, perms: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(activity, perms) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, perms))
                ActivityCompat.requestPermissions(activity, arrayOf(perms), requestCode)
            else ActivityCompat.requestPermissions(activity, arrayOf(perms), requestCode)
        else permissionListener.onPermissionUpdate(requestCode, true)
    }

    fun checkPermission(activity: Activity, perms: Array<String>, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(activity, perms[0]) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, perms[0]))
                ActivityCompat.requestPermissions(activity, perms, requestCode)
            else ActivityCompat.requestPermissions(activity, perms, requestCode)
        else permissionListener.onPermissionUpdate(requestCode, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        permissionListener = PermissionListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionsConfig.CONTACT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has granted.
                    permissionListener.onPermissionUpdate(PermissionsConfig.CONTACT, true)
                } else {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.READ_CONTACTS)
                        if (!showRationale) {
                            // Permission checked 'Never Ask Again'.
                            permissionListener.onNeverAskAgain(PermissionsConfig.CONTACT)
                        } else {
                            // Permission has denied.
                            permissionListener.onPermissionUpdate(PermissionsConfig.CONTACT, false)
                        }
                    }
                }
            }
            PermissionsConfig.STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionListener.onPermissionUpdate(PermissionsConfig.STORAGE, true)
                } else {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        if (!showRationale)
                            permissionListener.onNeverAskAgain(PermissionsConfig.STORAGE)
                        else permissionListener.onPermissionUpdate(PermissionsConfig.STORAGE, false)
                    }
                }
            }
            PermissionsConfig.SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionListener.onPermissionUpdate(PermissionsConfig.SMS, true)
                } else {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.READ_SMS)
                        if (!showRationale)
                            permissionListener.onNeverAskAgain(PermissionsConfig.SMS)
                        else permissionListener.onPermissionUpdate(PermissionsConfig.SMS, false)
                    }
                }
            }
            PermissionsConfig.CAMERA,
            PermissionsConfig.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionListener.onPermissionUpdate(PermissionsConfig.CAMERA, true)
                } else {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.CAMERA)
                        if (!showRationale)
                            permissionListener.onNeverAskAgain(PermissionsConfig.CAMERA)
                        else permissionListener.onPermissionUpdate(PermissionsConfig.CAMERA, false)
                    }
                }
            }
        }
    }

    override fun hasPermissionGranted(permission: Int, hasGranted: Boolean) {}

    override fun hasDeniedNeverAskAgain(permission: Int) {}
}