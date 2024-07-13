package com.example.starwarblaster.api

import com.example.starwarblaster.di.BASE_URL
import retrofit2.http.GET

interface PointTableApi {

    @GET("${BASE_URL}b/IKQQ")
    suspend fun retrievePlayersPoints(): List<PlayerResponse>

}
