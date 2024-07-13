package com.example.starwarblaster.data

import com.example.starwarblaster.api.MatchResponse
import com.example.starwarblaster.api.PlayerMatchesApi
import javax.inject.Inject

class GetPlayerMatchesRepository @Inject constructor(
    private val playerMatchesApi: PlayerMatchesApi
) : IGetPlayerMatchesRepository {
    override suspend fun getMatches(): Result<List<MatchResponse>> {
        return try {
            val response = playerMatchesApi.retrieveMatches()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

interface IGetPlayerMatchesRepository {
    suspend fun getMatches() : Result<List<MatchResponse>>
}


