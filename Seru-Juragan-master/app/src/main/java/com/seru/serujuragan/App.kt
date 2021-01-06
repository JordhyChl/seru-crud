package com.seru.serujuragan

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.config.LocaleManager
import com.seru.serujuragan.di.component.AppComponent
import com.seru.serujuragan.di.component.DaggerAppComponent
import com.seru.serujuragan.permission.PermissionPreference
import com.seru.serujuragan.util.AndroidUtils

class App: Application() {

    companion object {
        lateinit var instance: App

        val TAG = App::class.java.simpleName

        val DEBUG = BuildConfig.DEBUG

        @Volatile
        private var context: App? = null
        fun getContext(): App {
            if (context != null) {
                return context as App
            }
            return synchronized(this) {
                val i2 = context
                if (i2 != null) {
                    i2
                } else {
                    val created = App()
                    context = created
                    created
                }
            }
        }
    }

    //init DI
    private lateinit var appComponent: AppComponent

    private fun injectDependency(){
        appComponent = DaggerAppComponent.builder().build()
        appComponent.inject(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        context = this

        AndroidThreeTen.init(base   )
        AppPreference.init(base)
        PermissionPreference.init(base)
        LocaleManager.setLocale(base)
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        injectDependency()

        FirebaseApp.initializeApp(getContext())
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        try {
            //LocaleManager.setLocale(this)
            AndroidUtils.checkDisplay(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (DEBUG)
            Log.d(TAG, "onConfigurationChanged() | New config: $newConfig")
    }
}