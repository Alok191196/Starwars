package com.example.starwarblaster.api

import com.example.starwarblaster.di.BASE_URL
import retrofit2.http.GET

interface PlayerMatchesApi {

    @GET("${BASE_URL}b/JNYL")
    suspend fun retrieveMatches(): List<MatchResponse>

}