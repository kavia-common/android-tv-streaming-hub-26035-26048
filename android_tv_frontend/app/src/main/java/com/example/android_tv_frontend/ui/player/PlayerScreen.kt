@file:OptIn(androidx.media3.common.util.UnstableApi::class)

package com.example.android_tv_frontend.ui.player

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

import androidx.media3.ui.PlayerView
import com.example.android_tv_frontend.data.MockContentRepository
import com.example.android_tv_frontend.player.ExoPlayerManager

/**
 * PUBLIC_INTERFACE
 * PlayerScreen
 * Integrates Media3 ExoPlayer for playback of VOD/live streams. Saves resume positions.
 */
@Composable
fun PlayerScreen(
    id: String,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val manager = remember { ExoPlayerManager(context) }
    var url by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("Playing") }
    var isLive by remember { mutableStateOf(false) }
    val resume = remember { MockContentRepository.getResumePosition(id) }

    LaunchedEffect(id) {
        val item = MockContentRepository.detailById(id)
        item?.let {
            url = it.streamUrl
            title = it.title
            isLive = it.isLive
            manager.prepare(url, title, isLive, resume)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (!isLive) {
                val pos = manager.player.currentPosition
                MockContentRepository.saveResumePosition(id, pos)
            }
            manager.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PlayerView(it).apply {
                    useController = true
                    controllerAutoShow = true
                    controllerShowTimeoutMs = 3000
                    player = manager.player
                    layoutParams = android.view.ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    keepScreenOn = true
                }
            }
        )
    }
}
