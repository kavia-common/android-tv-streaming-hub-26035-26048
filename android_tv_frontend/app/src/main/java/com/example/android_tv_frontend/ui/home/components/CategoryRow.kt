package com.example.android_tv_frontend.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.android_tv_frontend.data.ContentItem

/**
 * PUBLIC_INTERFACE
 * CategoryRow
 * Horizontal row of content cards with focus scaling and highlight ring.
 */
@Composable
fun CategoryRow(items: List<ContentItem>, onClick: (ContentItem) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 12.dp)) {
        items(items) { item ->
            var focused by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(targetValue = if (focused) 1.08f else 1f, label = "focusScale2")
            Card(
                onClick = { onClick(item) },
                modifier = Modifier
                    .size(200.dp, 300.dp)
                    .scale(scale)
                    .onFocusChanged { state -> focused = state.isFocused }
                    .focusable(),
                shape = RoundedCornerShape(16.dp),
                border = if (focused) BorderStroke(2.dp, MaterialTheme.colorScheme.secondary) else null,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                AsyncImage(model = item.posterUrl, contentDescription = item.title)
            }
        }
    }
}
