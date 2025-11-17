package com.example.android_tv_frontend.ui.live

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.android_tv_frontend.data.Channel
import com.example.android_tv_frontend.data.MockContentRepository
import com.example.android_tv_frontend.data.Program
import kotlinx.coroutines.launch

/**
 * PUBLIC_INTERFACE
 * LiveTvGuideScreen
 * Displays channels with a simple program grid; OK navigates to play current stream.
 */
@Composable
fun LiveTvGuideScreen(onPlayChannel: (String) -> Unit) {
    var channels by remember { mutableStateOf<List<Channel>>(emptyList()) }
    var epg by remember { mutableStateOf<Map<String, List<Program>>>(emptyMap()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        channels = MockContentRepository.getChannels()
        val map = mutableMapOf<String, List<Program>>()
        channels.forEach { ch ->
            map[ch.id] = MockContentRepository.getEPGForChannel(ch.id)
        }
        epg = map
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(16.dp)) {
        items(channels) { ch ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(tonalElevation = 2.dp, modifier = Modifier.focusable(), onClick = { onPlayChannel(ch.id) }) {
                    Text(
                        text = "${ch.number} ${ch.name}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                epg[ch.id].orEmpty().take(3).forEach { program ->
                    Surface(tonalElevation = 1.dp, modifier = Modifier.focusable(), onClick = { onPlayChannel(ch.id) }) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(program.title, style = MaterialTheme.typography.bodyLarge)
                            Text(program.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
