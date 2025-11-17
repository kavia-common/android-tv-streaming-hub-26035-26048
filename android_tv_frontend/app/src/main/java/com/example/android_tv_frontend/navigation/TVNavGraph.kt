package com.example.android_tv_frontend.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.android_tv_frontend.ui.details.VideoDetailScreen
import com.example.android_tv_frontend.ui.home.HomeScreen
import com.example.android_tv_frontend.ui.live.LiveTvGuideScreen
import com.example.android_tv_frontend.ui.player.PlayerScreen
import com.example.android_tv_frontend.ui.search.SearchScreen
import com.example.android_tv_frontend.ui.settings.SettingsScreen
import androidx.tv.material3.ExperimentalTvMaterial3Api

/**
 * PUBLIC_INTERFACE
 * TVNavGraph
 * Composable that sets up the Navigation routes for the TV app and persistent left-side navigation.
 */
@Composable
fun TVNavGraph() {
    val navController = rememberNavController()
    TVScaffold(navController = navController)
}

object Routes {
    const val Home = "home"
    const val Live = "liveGuide"
    const val Detail = "detail/{id}"
    const val Player = "player/{id}"
    const val Search = "search"
    const val Settings = "settings"
}

/**
 * Persistent left nav + page container
 */
@Composable
private fun TVScaffold(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val focusRequester = FocusRequester()
        Column(
            modifier = Modifier
                .width(240.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(20.dp)
                .focusRequester(focusRequester),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            NavButton("Home") { navController.navigate(Routes.Home) }
            NavButton("Live TV") { navController.navigate(Routes.Live) }
            NavButton("On-Demand") { navController.navigate(Routes.Home) }
            NavButton("Search") { navController.navigate(Routes.Search) }
            NavButton("Settings") { navController.navigate(Routes.Settings) }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.Home
            ) {
                composable(Routes.Home) {
                    HomeScreen(
                        onOpenDetail = { id -> navController.navigate("detail/$id") },
                        onOpenPlayer = { id -> navController.navigate("player/$id") }
                    )
                }
                composable(Routes.Live) {
                    LiveTvGuideScreen(
                        onPlayChannel = { id -> navController.navigate("player/$id") }
                    )
                }
                composable(Routes.Search) {
                    SearchScreen(
                        onOpenDetail = { id -> navController.navigate("detail/$id") }
                    )
                }
                composable(Routes.Settings) {
                    SettingsScreen()
                }
                composable(Routes.Detail) { backStack ->
                    val id = backStack.arguments?.getString("id").orEmpty()
                    VideoDetailScreen(
                        id = id,
                        onPlay = { navController.navigate("player/$id") },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Routes.Player) { backStack ->
                    val id = backStack.arguments?.getString("id").orEmpty()
                    PlayerScreen(
                        id = id,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavButton(text: String, onClick: () -> Unit) {
    // TV Material3 button automatically supports focus states with glow and scale
    androidx.tv.material3.Button(onClick = onClick) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
