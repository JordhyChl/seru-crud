package com.seru.serujuragan.permission

import android.content.Context
import android.content.SharedPreferences
import com.seru.serujuragan.BuildConfig

/**
 * Created by adityaaugusta on 10/25/16.
 */
object PermissionPreference {

    private val TAG = PermissionPreference::class.java.simpleName
    private const val PREF_NAME = BuildConfig.APPLICATION_ID +"_prefpermission"

    //Preference Keys
    private const val KEY_CONTACT = "permContact"
    private const val KEY_MEDIA = "permMedia"
    private const val KEY_CAMERA = "permCamera"

    private const val KEY_CONTACT_DNAA = "permContactDNAA"
    private const val KEY_MEDIA_DNAA = "permMediaDNAA"
    private const val KEY_CAMERA_DNAA = "permCameraDNAA"

    private lateinit var pref: SharedPreferences

    fun init(c: Context) { pref = c.getSharedPreferences(PREF_NAME, 0) }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var hasContactGranted: Boolean
        get() = pref.getBoolean(KEY_CONTACT, false)
        set(value) = pref.edit { it.putBoolean(KEY_CONTACT, value) }

    var hasMediaGranted: Boolean
        get() = pref.getBoolean(KEY_MEDIA, false)
        set(value) = pref.edit { it.putBoolean(KEY_MEDIA, value) }

    var hasCameraGranted: Boolean
        get() = pref.getBoolean(KEY_CAMERA, false)
        set(value) = pref.edit { it.putBoolean(KEY_CAMERA, value) }



    var hasContactDNAA: Boolean
        get() = pref.getBoolean(KEY_CONTACT_DNAA, false)
        set(value) = pref.edit { it.putBoolean(KEY_CONTACT_DNAA, value) }

    var hasMediaDNAA: Boolean
        get() = pref.getBoolean(KEY_MEDIA_DNAA, false)
        set(value) = pref.edit { it.putBoolean(KEY_MEDIA_DNAA, value) }

    var hasCameraDNAA: Boolean
        get() = pref.getBoolean(KEY_CAMERA_DNAA, false)
        set(value) = pref.edit { it.putBoolean(KEY_CAMERA_DNAA, value) }

}
