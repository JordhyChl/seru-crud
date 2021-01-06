package com.seru.serujuragan.config

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.seru.serujuragan.App
import com.seru.serujuragan.BuildConfig

/**
 * Created by Mahendra Dev on 16/12/2019
 */
object AppPreference {

    private val TAG = AppPreference::class.java.simpleName
    const val PREF_NAME = BuildConfig.APPLICATION_ID +"_prefapp"

    //Preference Keys
    private const val FRESH_INSTALL = "isFreshInstall"
    private const val IS_LOGGEDIN = "isLoggedIn"
    private const val TOKEN = "token"
    private const val FCM_TOKEN = "tokenFcm"
    private const val ID_JURAGAN = "idJuragan"
    private const val NAME_JURAGAN = "nameJuragan"
    private const val LONGITUDE_JURAGAN = "longitudeJuragan"
    private const val LATITUDE_JURAGAN = "latitudeJuragan"
    private const val RADIUS_JURAGAN = "radiusJuragan"
    private const val THRESHOLD_RADIUS_JURAGAN = "thresholdRadiusJuragan"
    private const val DAY = "day"
    private const val TEMP_CURRENT_LOCATION = "tempLocation"
    private const val DATE_TIMESTAMP = "dateTimestamp"

    private lateinit var pref: SharedPreferences

    fun init(c: Context) { pref = c.getSharedPreferences(PREF_NAME, 0) }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var freshInstall: Boolean
        get() = pref.getBoolean(FRESH_INSTALL, true)
        set(value) = pref.edit { it.putBoolean(FRESH_INSTALL, value) }

    var isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGGEDIN, false)
        set(value) = pref.edit { it.putBoolean(IS_LOGGEDIN, value) }

    var token: String
        get() = pref.getString(TOKEN,"") as String
        set(value) = pref.edit {it.putString(TOKEN, value)}

    var fcmToken: String
        get() = pref.getString(FCM_TOKEN,"") as String
        set(value) = pref.edit {it.putString(FCM_TOKEN, value)}

    var idJuragan: String
        get() = pref.getString(ID_JURAGAN,"") as String
        set(value) = pref.edit { it.putString(ID_JURAGAN, value) }

    var nameJuragan: String
        get() = pref.getString(NAME_JURAGAN,"") as String
        set(value) = pref.edit { it.putString(NAME_JURAGAN, value) }

    var longitudeJuragan: Float
        get() = pref.getFloat(LONGITUDE_JURAGAN,0f)
        set(value) = pref.edit { it.putFloat(LONGITUDE_JURAGAN, value) }

    var latitudeJuragan: Float
        get() = pref.getFloat(LATITUDE_JURAGAN,0f)
        set(value) = pref.edit { it.putFloat(LATITUDE_JURAGAN, value) }

    var radiusJuragan: Int
        get() = pref.getInt(RADIUS_JURAGAN,0)
        set(value) = pref.edit { it.putInt(RADIUS_JURAGAN, value) }

    var thresholdJuragan: Int
        get() = pref.getInt(THRESHOLD_RADIUS_JURAGAN,0)
        set(value) = pref.edit { it.putInt(THRESHOLD_RADIUS_JURAGAN, value) }

    var dayNow: String
        get() = pref.getString(DAY,"") as String
        set(value) = pref.edit {it.putString(DAY, value)}

    var tempLocation: String
        get() = pref.getString(TEMP_CURRENT_LOCATION,"000000") as String
        set(value) = pref.edit { it.putString(TEMP_CURRENT_LOCATION,value) }

    var dateTimestamp: Long
        get() = pref.getLong(DATE_TIMESTAMP,0)
        set(value) = pref.edit { it.putLong(DATE_TIMESTAMP, value) }
}