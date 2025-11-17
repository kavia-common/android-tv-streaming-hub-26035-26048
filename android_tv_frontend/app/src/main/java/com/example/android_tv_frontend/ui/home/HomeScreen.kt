package com.example.android_tv_frontend.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.android_tv_frontend.data.ContentItem
import com.example.android_tv_frontend.data.MockContentRepository
import com.example.android_tv_frontend.ui.home.components.CategoryRow
import com.example.android_tv_frontend.ui.home.components.FeaturedCarousel
import kotlinx.coroutines.launch

/**
 * PUBLIC_INTERFACE
 * HomeScreen
 * Displays featured carousel and category rows.
 */
@Composable
fun HomeScreen(
    onOpenDetail: (String) -> Unit,
    onOpenPlayer: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var featured by remember { mutableStateOf<List<ContentItem>>(emptyList()) }
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }
    var categoryItems by remember { mutableStateOf<Map<String, List<ContentItem>>>(emptyMap()) }

    LaunchedEffect(Unit) {
        featured = MockContentRepository.getFeatured()
        categories = MockContentRepository.getCategories().map { it.name }
        // prefetch a few
        categories.forEach { cat ->
            val items = MockContentRepository.getCategoryItems(cat)
            categoryItems = categoryItems + (cat to items)
        }
    }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        FeaturedCarousel(items = featured, onClick = { item ->
            if (item.isLive) onOpenPlayer(item.id) else onOpenDetail(item.id)
        })
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            items(categories) { cat ->
                Column {
                    Text(
                        text = cat,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    CategoryRow(
                        items = categoryItems[cat].orEmpty(),
                        onClick = { item -> if (item.isLive) onOpenPlayer(item.id) else onOpenDetail(item.id) }
                    )
                }
            }
        }
    }
}
