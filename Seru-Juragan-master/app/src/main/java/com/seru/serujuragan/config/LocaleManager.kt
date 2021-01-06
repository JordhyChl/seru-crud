package com.seru.serujuragan.config

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

/**
 * Created by Aditya Augusta on 11/15/18.
 */
object LocaleManager {

    const val LANG_BAHASA = "in"
    const val LANG_ENGLISH = "en"

    private fun updateResources(c: Context, lang: String): Context {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val res = c.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)

        return c.createConfigurationContext(config)
    }

    fun setLocale(c: Context): Context? {
        return updateResources(c, LANG_BAHASA)
    }

    fun getLocale(c: Context): Locale {
        val config = c.resources.configuration
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) config.locales.get(0) else config.locale
    }

}