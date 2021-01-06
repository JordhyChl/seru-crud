package com.seru.serujuragan.di.component

import com.seru.serujuragan.App
import com.seru.serujuragan.di.module.AppModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Mahendra Dev on 16/12/2019
 */
@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(app: App)

}