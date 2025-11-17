package com.example.android_tv_frontend.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.android_tv_frontend.data.ContentItem
import com.example.android_tv_frontend.data.MockContentRepository
import com.example.android_tv_frontend.ui.home.components.CategoryRow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * PUBLIC_INTERFACE
 * SearchScreen
 * TV-optimized search input and result rows.
 */
@Composable
fun SearchScreen(onOpenDetail: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<ContentItem>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(query) {
        searchJob?.cancel()
        searchJob = scope.launch {
            delay(250)
            results = if (query.isBlank()) emptyList() else MockContentRepository.search(query)
        }
    }

    Column(modifier = Modifier.padding(24.dp)) {
        OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Search") })
        if (results.isNotEmpty()) {
            Text("Results", modifier = Modifier.padding(top = 16.dp))
            CategoryRow(items = results, onClick = { onOpenDetail(it.id) })
        }
    }
}
