package com.example.starwarblaster

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.starwarblaster.ui.viewmodels.MainViewModel
import com.example.starwarblaster.ui.viewmodels.PointTableViewModelContract
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PointsTableScreen(
    mainViewModel: MainViewModel,
    onPlayerClick: (id: Long) -> Unit
) {

    val pointsTableState = mainViewModel.pointsTableState.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.processAction(PointTableViewModelContract.MainViewModelAction.OnLaunch)
    }

    LaunchedEffect(Unit) {
        mainViewModel.mainViewModelEffect.collectLatest {
            when (it) {
                is PointTableViewModelContract.MainViewModelEffect.NavigateToPlayerMatchesDetails -> {
                    onPlayerClick(it.playerId)
                }
            }
        }
    }

    if (pointsTableState.value.isLoading) {
        Loader()
    }

    if (pointsTableState.value.error != null) {
        ErrorScreen(error = pointsTableState.value.error!!)
    }

    if (pointsTableState.value.points.isNotEmpty()) {
        PointsTableContent(state = pointsTableState.value, onPlayerClick = {
            mainViewModel.processAction(action = PointTableViewModelContract.MainViewModelAction.OnPlayerClick(it))
        })
    }

}


@Composable
fun ErrorScreen(error: String) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = error,
            color = Color.Red,
            fontSize = 20.sp
        )
    }
}

@Composable
fun Loader() {

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CircularProgressIndicator(
            modifier = Modifier.width(48.dp),
            color = Color.Blue,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = stringResource(R.string.fetching_points))
    }
}


@Composable
fun PointsTableContent(
    state: PointTableViewModelContract.PointsTableState,
    onPlayerClick: (id: Long) -> Unit
) {

    Column {

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.star_wars_blaster_tournament),
                fontSize = 28.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(R.string.points_table),
            fontSize = 20.sp,
            color = Color.Black,
        )

        LazyColumn {
            items(state.points) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            onPlayerClick(it.playerId)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {




                    AsyncImage(
                        model = it.imageUrl,
                        contentDescription = "null",
                        modifier = Modifier.size(80.dp)
                    )


                    Text(
                        modifier = Modifier.padding(4.dp),
                        fontSize = 24.sp,
                        color = Color.Black,
                        text = it.name,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        modifier = Modifier
                            .padding(4.dp)
                            .padding(end = 16.dp),
                        fontSize = 28.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        text = it.point.toString()
                    )

                }

                Divider(color = Color.Gray, thickness = 1.dp)

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

}
