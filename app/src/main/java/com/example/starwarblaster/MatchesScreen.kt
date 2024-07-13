package com.example.starwarblaster

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.starwarblaster.ui.viewmodels.MainViewModel
import com.example.starwarblaster.ui.viewmodels.PointTableViewModelContract

@Composable
fun MatchesScreen(
    mainViewModel: MainViewModel,
    onBackPressed: () -> Boolean
) {

    BackHandler {
        onBackPressed()
    }

    val playerMatchesState = mainViewModel.playerMatchesState.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.processAction(PointTableViewModelContract.MainViewModelAction.OnLaunch)
    }

    if (playerMatchesState.value.isLoading) {
        Loader()
    }

    if (playerMatchesState.value.error != null) {
        ErrorScreen(error = playerMatchesState.value.error!!)
    }

    if (playerMatchesState.value.matches.isNotEmpty()) {
        PlayerMatchesContent(state = playerMatchesState.value)
    }

}

@Composable
fun PlayerMatchesContent(state: PointTableViewModelContract.PlayerMatchesState) {

    Column {


        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.profilePlayerName,
                fontSize = 28.sp,
                color = Color.Black
            )
        }


        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(R.string.matches),
            fontSize = 20.sp,
            color = Color.Black,
        )

        LazyColumn {
            items(state.matches) {

                Row(
                    modifier = Modifier
                        .background(color = Color(it.bgColor.code))
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp,
                        color = Color.Black,
                        text = it.playerName1,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        modifier = Modifier.padding(4.dp),
                        fontSize = 20.sp,
                        color = Color.Black,
                        text = "${it.playerName1Score} - ${it.playerName2Score}",
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp,
                        color = Color.Black,
                        text = it.playerName2,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                }

            }
        }
    }

}
