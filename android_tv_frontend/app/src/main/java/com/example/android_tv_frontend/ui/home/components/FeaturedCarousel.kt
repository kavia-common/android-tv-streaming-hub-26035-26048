package com.example.android_tv_frontend.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.android_tv_frontend.data.ContentItem

/**
 * PUBLIC_INTERFACE
 * FeaturedCarousel
 * Displays a row of featured items with focus effects.
 */
@Composable
fun FeaturedCarousel(items: List<ContentItem>, onClick: (ContentItem) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items.take(10).forEach { item ->
            var focused by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(targetValue = if (focused) 1.07f else 1f, label = "focusScale")
            Card(
                modifier = Modifier
                    .width(420.dp)
                    .height(240.dp)
                    .scale(scale)
                    .focusable(onFocusChanged = { focused = it.isFocused }),
                onClick = { onClick(item) },
                shape = RoundedCornerShape(16.dp),
                border = if (focused) BorderStroke(2.dp, MaterialTheme.colorScheme.secondary) else null,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = item.backdropUrl,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp))
                    BasicText(
                        text = item.title,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        }
    }
}
