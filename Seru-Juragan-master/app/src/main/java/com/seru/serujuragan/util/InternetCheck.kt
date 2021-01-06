package com.seru.serujuragan.util

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by Arya Yudha Mahendra on 29/06/2019.
 */
@Suppress("DEPRECATION")
object InternetCheck {
    fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        var isConnected = false
        if (connectivityManager != null) {
            val activeNetwork = connectivityManager.activeNetworkInfo
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }

        return isConnected
    }
}