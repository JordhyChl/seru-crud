package com.seru.serujuragan.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import com.seru.serujuragan.App
import javax.inject.Singleton

/**
 * Created by Arya Yudha Mahendra on 29/06/2019.
 */
@Module
class AppModule(private val app: App) {

    @Provides
    @Singleton
    fun provideApplication(): Application {
        return app
    }
}