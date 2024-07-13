package com.example.starwarblaster.api

import com.google.gson.annotations.SerializedName

data class MatchesResponse(
    val matchResponses: List<MatchResponse>
)

data class MatchResponse(
    @SerializedName("match") val match: Long,
    @SerializedName("player1") val playerScoreResponse1: PlayerScoreResponse,
    @SerializedName("player2") val playerScoreResponse2: PlayerScoreResponse,
)

data class PlayerScoreResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("score") val score: Long,
)