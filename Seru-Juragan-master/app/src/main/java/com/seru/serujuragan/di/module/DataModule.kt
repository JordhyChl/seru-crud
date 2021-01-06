package com.seru.serujuragan.di.module

//import com.flipkart.android.proteus.gson.ProteusTypeAdapterFactory
import com.google.gson.Gson
import com.seru.serujuragan.api.ApiService
import dagger.Module
import dagger.Provides

/**
 * Created by Arya Yudha Mahendra on 29/06/2019.
 */
@Module
class DataModule {

    @Provides
    fun providesApiServicesLocal():ApiService {
        return ApiService.local()
    }

    @Provides
    fun provideApiService(): ApiService {
        return ApiService.create()
    }

//    @Provides
//    @Singleton
//    fun providePostDatabase(appBase: AppBase): PostDatabase {
//        return Room
//            .databaseBuilder(appBase, PostDatabase::class.java, PostDatabase.DB_NAME)
//            .fallbackToDestructiveMigration()
//            .build()
//
//    }
//
//    @Provides
//    fun providePostDao(postDatabase: PostDatabase): PostDao {
//        return postDatabase.postsDao()
//    }
//
//    @Provides
//    fun provideRepository(apiService: ApiService, postDatabase: PostDatabase):AppRepoInterface{
//        return AppRepo(apiService,postDatabase)
//    }
}