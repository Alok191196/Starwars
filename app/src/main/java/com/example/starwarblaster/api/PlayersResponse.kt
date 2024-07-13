package com.example.starwarblaster.api

import com.google.gson.annotations.SerializedName


data class PlayersResponse (
    val players: List<PlayerResponse>
)

data class PlayerResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String,
)
