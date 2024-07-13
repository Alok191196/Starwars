package com.example.starwarblaster.ui.viewmodels

import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starwarblaster.api.MatchResponse
import com.example.starwarblaster.api.PlayerResponse
import com.example.starwarblaster.data.IGetPlayerMatchesRepository
import com.example.starwarblaster.data.IGetPointTableRespository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPlayerMatchesRepository: IGetPlayerMatchesRepository,
    private val getPointTableRepository: IGetPointTableRespository
) : ViewModel() {

    private val _playerMatchesState: MutableStateFlow<PointTableViewModelContract.PlayerMatchesState> =
        MutableStateFlow(PointTableViewModelContract.PlayerMatchesState())
    val playerMatchesState: StateFlow<PointTableViewModelContract.PlayerMatchesState> =
        _playerMatchesState

    private val _pointsTableState: MutableStateFlow<PointTableViewModelContract.PointsTableState> =
        MutableStateFlow(PointTableViewModelContract.PointsTableState(listOf()))
    val pointsTableState: StateFlow<PointTableViewModelContract.PointsTableState> =
        _pointsTableState

    private val _mainViewModelEffect: MutableSharedFlow<PointTableViewModelContract.MainViewModelEffect> =
        MutableSharedFlow(replay = 0)
    val mainViewModelEffect: SharedFlow<PointTableViewModelContract.MainViewModelEffect> get() = _mainViewModelEffect

    private var matchesResponse: List<MatchResponse>? = null
    private var playersResponse: List<PlayerResponse>? = null
    private var clickedId: Long? = null

    fun processAction(action: PointTableViewModelContract.MainViewModelAction) =
        viewModelScope.launch {
            when (action) {
                PointTableViewModelContract.MainViewModelAction.OnLaunch -> onLaunch()
                is PointTableViewModelContract.MainViewModelAction.OnPlayerClick -> {
                    clickedId = action.playerId
                    calculateMatches()
                    _mainViewModelEffect.emit(
                        PointTableViewModelContract.MainViewModelEffect.NavigateToPlayerMatchesDetails(
                            action.playerId
                        )
                    )

                }
            }
        }

    private suspend fun onLaunch() {
        _pointsTableState.value =
            _pointsTableState.value.copy(isLoading = true)
        val matchJob = CoroutineScope(Dispatchers.IO).async {
            getPlayerMatchesRepository.getMatches()
        }
        val playersJob = CoroutineScope(Dispatchers.IO).async {
            getPointTableRepository.getTable()
        }

        val matchesResult = matchJob.await()
        if (matchesResult.isSuccess) {
            matchesResponse = matchesResult.getOrNull()
        } else {
            _pointsTableState.value = _pointsTableState.value.copy(
                error = matchesResult.exceptionOrNull()!!.toString(),
                isLoading = false
            )
        }

        val playersResult = playersJob.await()
        if (playersResult.isSuccess) {
            playersResponse = playersResult.getOrNull()
        } else {
            _pointsTableState.value = _pointsTableState.value.copy(
                error = matchesResult.exceptionOrNull()!!.toString(),
                isLoading = false
            )
        }
        calculatePoints()
        _pointsTableState.value =
            _pointsTableState.value.copy(isLoading = false)
    }

    private fun calculateMatches() {
        val calculatedMatches: MutableList<PointTableViewModelContract.Match> = mutableListOf()

        val filteredMatches = matchesResponse?.filter {
            it.playerScoreResponse1.id == clickedId || it.playerScoreResponse2.id == clickedId
        }

        filteredMatches?.forEach { matchResponse ->
            val (keyPlayer, oppPlayer) = if (matchResponse.playerScoreResponse1.id == clickedId) {
                matchResponse.playerScoreResponse1 to matchResponse.playerScoreResponse2
            } else {
                matchResponse.playerScoreResponse2 to matchResponse.playerScoreResponse1
            }

            val keyPlayerScore = keyPlayer.score
            val keyPlayerName = playersResponse?.find { it.id == keyPlayer.id }?.name ?: ""
            val oppPlayerScore = oppPlayer.score
            val oppPlayerName = playersResponse?.find { it.id == oppPlayer.id }?.name ?: ""

            val color = when {
                keyPlayerScore > oppPlayerScore -> PointTableViewModelContract.BgColor.GREEN
                keyPlayerScore == oppPlayerScore -> PointTableViewModelContract.BgColor.WHITE
                else -> PointTableViewModelContract.BgColor.RED
            }

            val match = PointTableViewModelContract.Match(
                playerName1 = if (matchResponse.playerScoreResponse1.id == clickedId) keyPlayerName else oppPlayerName,
                playerName1Score = if (matchResponse.playerScoreResponse1.id == clickedId) keyPlayerScore else oppPlayerScore,
                playerName2 = if (matchResponse.playerScoreResponse1.id == clickedId) oppPlayerName else keyPlayerName,
                playerName2Score = if (matchResponse.playerScoreResponse1.id == clickedId) oppPlayerScore else keyPlayerScore,
                bgColor = color
            )
            calculatedMatches.add(match)
        }

        _playerMatchesState.value = _playerMatchesState.value.copy(
            profilePlayerName = playersResponse?.find { it.id == clickedId}?.name ?: "",
            matches = calculatedMatches
        )
    }

    private fun calculatePoints() {

        val calculatedPoint: MutableList<PointTableViewModelContract.Point> = mutableListOf()

        playersResponse?.forEach { playerResponse ->
            var playerTotalPoint = 0
            val id = playerResponse.id
            val player = playersResponse?.find { it.id == id }
            val name = player?.name
            val playerImage = player?.icon

            val filteredList = matchesResponse?.filter {
                it.playerScoreResponse1.id == id || it.playerScoreResponse2.id == id
            }

            filteredList?.forEach { matchResponse ->

                if (matchResponse.playerScoreResponse1.id == id && matchResponse.playerScoreResponse1.score > matchResponse.playerScoreResponse2.score) {
                    playerTotalPoint += 3
                } else if (matchResponse.playerScoreResponse1.score == matchResponse.playerScoreResponse2.score) {
                    playerTotalPoint += 1
                }

                if (matchResponse.playerScoreResponse2.id == id && matchResponse.playerScoreResponse2.score > matchResponse.playerScoreResponse1.score) {
                    playerTotalPoint += 3
                } else if (matchResponse.playerScoreResponse1.score == matchResponse.playerScoreResponse2.score) {
                    playerTotalPoint += 1
                }

            }

            val point = PointTableViewModelContract.Point(
                playerId = id,
                imageUrl = playerImage?.replace("http://","https://") ?: "",
                name = name ?: "",
                point = playerTotalPoint
            )
            calculatedPoint.add(point)
        }

        _pointsTableState.value =
            _pointsTableState.value.copy(points = calculatedPoint.sortedByDescending { it.point })
    }
}


interface PointTableViewModelContract {

    data class PlayerMatchesState(
        val profilePlayerName: String = "",
        val matches: List<Match> = listOf(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    data class Match(
        val playerName1: String = "",
        val playerName1Score: Long = 0,
        val playerName2: String = "",
        val playerName2Score: Long = 0,
        val bgColor: BgColor = BgColor.WHITE
    )


    data class PointsTableState(
        val points: List<Point> = listOf(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    data class Point(
        val playerId: Long = 0L,
        val imageUrl: String = "",
        val name: String = "",
        val point: Int = 0
    )

    sealed interface MainViewModelAction {
        data object OnLaunch : MainViewModelAction
        data class OnPlayerClick(val playerId: Long) : MainViewModelAction
    }

    interface MainViewModelEffect {
        class NavigateToPlayerMatchesDetails(val playerId: Long) : MainViewModelEffect
    }

    enum class BgColor(val code: Int) {
        GREEN(Color.GREEN),
        RED(Color.RED),
        WHITE(Color.WHITE)
    }

}