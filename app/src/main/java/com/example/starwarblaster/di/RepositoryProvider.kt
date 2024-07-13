package com.example.starwarblaster.di

import com.example.starwarblaster.api.PlayerMatchesApi
import com.example.starwarblaster.api.PointTableApi
import com.example.starwarblaster.data.GetPlayerMatchesRepository
import com.example.starwarblaster.data.GetPointTableRepository
import com.example.starwarblaster.data.IGetPlayerMatchesRepository
import com.example.starwarblaster.data.IGetPointTableRespository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryProvider {


    @Singleton
    @Provides
    fun providePlayerMatchesRepository(playerMatchesApi: PlayerMatchesApi): IGetPlayerMatchesRepository =
        GetPlayerMatchesRepository(playerMatchesApi)

    @Singleton
    @Provides
    fun providePointTableRepository(pointTableApi: PointTableApi): IGetPointTableRespository =
        GetPointTableRepository(pointTableApi)


}