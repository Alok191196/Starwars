package com.example.starwarblaster.data

import com.example.starwarblaster.api.PlayerResponse
import com.example.starwarblaster.api.PlayersResponse
import com.example.starwarblaster.api.PointTableApi
import javax.inject.Inject

class GetPointTableRepository @Inject constructor(
    private val pointTableService: PointTableApi
): IGetPointTableRespository {
    override suspend fun getTable(): Result<List<PlayerResponse>> {
        return try {
            val response = pointTableService.retrievePlayersPoints()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

interface IGetPointTableRespository {

    suspend fun getTable() : Result<List<PlayerResponse>>

}

