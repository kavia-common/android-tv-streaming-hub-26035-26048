package com.example.android_tv_frontend.data

import android.util.Log
import com.example.android_tv_frontend.data.model.Category
import com.example.android_tv_frontend.data.model.ContentItem
import com.example.android_tv_frontend.data.model.LiveChannel
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

/**
 * PUBLIC_INTERFACE
 * Repository that provides content categories, VOD items, and live channels.
 * Uses mock data by default, but can call a real API if BASE_URL is set at runtime:
 * - set system property api.baseUrl or environment variable API_BASE_URL
 */
class Repository {

    private val baseUrl: String? by lazy {
        // Prefer system property, then environment
        System.getProperty("api.baseUrl") ?: System.getenv("API_BASE_URL")
    }

    private val apiService: ApiService? by lazy {
        baseUrl?.let { url ->
            val logging = HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BASIC) }
            val client = OkHttpClient.Builder().addInterceptor(logging).build()
            Retrofit.Builder()
                .baseUrl(if (url.endsWith("/")) url else "$url/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(ApiService::class.java)
        }
    }

    // PUBLIC_INTERFACE
    suspend fun getCategories(): List<Category> {
        return apiService?.runCatching { categories() }?.getOrNull() ?: mockCategories()
    }

    // PUBLIC_INTERFACE
    suspend fun getContents(categoryId: String? = null): List<ContentItem> {
        val result = apiService?.runCatching { contents(categoryId = categoryId) }?.getOrNull()
        return result ?: mockContents().filter { categoryId == null || it.categoryId == categoryId }
    }

    // PUBLIC_INTERFACE
    suspend fun search(q: String): List<ContentItem> {
        val result = apiService?.runCatching { contents(query = q) }?.getOrNull()
        return result ?: mockContents().filter { it.title.contains(q, ignoreCase = true) }
    }

    // PUBLIC_INTERFACE
    suspend fun getContentDetails(id: String): ContentItem {
        val result = apiService?.runCatching { contentDetails(id) }?.getOrNull()
        return result ?: mockContents().first { it.id == id }
    }

    // PUBLIC_INTERFACE
    suspend fun getLiveChannels(): List<LiveChannel> {
        return apiService?.runCatching { liveChannels() }?.getOrNull() ?: mockLive()
    }

    // Simple mock data
    private fun mockCategories(): List<Category> = listOf(
        Category("featured", "Featured"),
        Category("movies", "Movies"),
        Category("series", "Series"),
        Category("kids", "Kids"),
        Category("live", "Live TV")
    )

    private fun mockContents(): List<ContentItem> {
        val thumbs = listOf(
            "https://picsum.photos/seed/a/600/338",
            "https://picsum.photos/seed/b/600/338",
            "https://picsum.photos/seed/c/600/338",
            "https://picsum.photos/seed/d/600/338",
            "https://picsum.photos/seed/e/600/338",
        )
        val backdrops = listOf(
            "https://picsum.photos/seed/ba/1280/720",
            "https://picsum.photos/seed/bb/1280/720",
            "https://picsum.photos/seed/bc/1280/720",
        )
        val vodUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        val items = mutableListOf<ContentItem>()
        val cats = listOf("featured", "movies", "series", "kids")
        repeat(20) { idx ->
            items += ContentItem(
                id = "vod-$idx",
                title = "Sample Video $idx",
                description = "A sample description for item $idx. Enjoy smooth playback with Media3.",
                categoryId = cats[idx % cats.size],
                imageUrl = thumbs[idx % thumbs.size],
                backdropUrl = backdrops[idx % backdrops.size],
                videoUrl = vodUrl,
                isLive = false,
                durationSec = 60L * (60 + idx)
            )
        }
        return items
    }

    private fun mockLive(): List<LiveChannel> {
        val logo = "https://dummyimage.com/200x200/2563EB/ffffff&text=LIVE"
        val stream = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"
        return (1..10).map {
            LiveChannel(
                id = "ch-$it",
                number = (100 + it).toString(),
                name = "Channel $it",
                logoUrl = logo,
                streamUrl = stream
            )
        }
    }
}
