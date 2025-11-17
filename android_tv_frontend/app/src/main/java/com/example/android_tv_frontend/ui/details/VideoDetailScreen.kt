package com.example.android_tv_frontend.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import coil.compose.AsyncImage
import com.example.android_tv_frontend.data.ContentItem
import com.example.android_tv_frontend.data.MockContentRepository
import com.example.android_tv_frontend.ui.home.components.CategoryRow

/**
 * PUBLIC_INTERFACE
 * VideoDetailScreen
 * Shows poster/backdrop, synopsis, and actions; allows Play/Resume and opens related items.
 */
@OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class) // tv-material3 components are experimental
@Composable
fun VideoDetailScreen(
    id: String,
    onPlay: () -> Unit,
    onBack: () -> Unit
) {
    var item by remember { mutableStateOf<ContentItem?>(null) }
    var related by remember { mutableStateOf<List<ContentItem>>(emptyList()) }

    LaunchedEffect(id) {
        item = MockContentRepository.detailById(id)
        item?.let {
            related = MockContentRepository.getCategoryItems(it.categories.firstOrNull() ?: "Trending")
        }
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AsyncImage(model = item?.posterUrl, contentDescription = item?.title)
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = item?.title ?: "", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = item?.description ?: "", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    androidx.tv.material3.Button(onClick = onPlay) { Text("Play") }
                    androidx.tv.material3.Button(onClick = { /* TODO: watchlist action */ }) { Text("Watchlist") }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Related", style = MaterialTheme.typography.titleLarge)
        CategoryRow(items = related, onClick = { /* open another detail could be implemented */ })
    }
}
