package com.example.starwarblaster

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.starwarblaster.ui.theme.StarWarBlasterTheme
import com.example.starwarblaster.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    lateinit var navController: NavHostController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StarWarBlasterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    navController = rememberNavController()


                    NavHost(navController = navController, "points_table") {

                        composable("points_table") {
                            PointsTableScreen(mainViewModel) {
                                navController.navigate("matches/${it}")
                            }
                        }

                        composable(
                            "matches/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) {
                            val id = it.arguments?.getInt("id") ?: 0

                            MatchesScreen(
                                mainViewModel = mainViewModel,
                                onBackPressed = {
                                    navController.popBackStack()
                                }
                            )

                        }

                    }


                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StarWarBlasterTheme {
        Greeting("Android")
    }
}