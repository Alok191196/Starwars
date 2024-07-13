package com.example.starwarblaster.di

import com.example.starwarblaster.api.PlayerMatchesApi
import com.example.starwarblaster.api.PointTableApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

const val BASE_URL = "https://www.jsonkeeper.com/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun providePointTableService(retrofit: Retrofit): PointTableApi {
        return retrofit.create(PointTableApi::class.java)
    }

    @Provides
    @Singleton
    fun providePlayerMatchService(retrofit: Retrofit): PlayerMatchesApi {
        return retrofit.create(PlayerMatchesApi::class.java)
    }


}