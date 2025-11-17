package com.example.android_tv_frontend.data

import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

/**
 * PUBLIC_INTERFACE
 * MockContentRepository
 * In-memory repository that serves featured, categories, search results, detail items, and live EPG.
 */
object MockContentRepository {

    private val sampleHls = "https://storage.googleapis.com/shaka-demo-assets/angel-one-hls/hls.m3u8"
    private val sampleHls2 = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"

    private val posters = listOf(
        "https://picsum.photos/seed/tv1/600/900",
        "https://picsum.photos/seed/tv2/600/900",
        "https://picsum.photos/seed/tv3/600/900",
        "https://picsum.photos/seed/tv4/600/900",
        "https://picsum.photos/seed/tv5/600/900",
    )

    private val backdrops = listOf(
        "https://picsum.photos/seed/tvb1/1280/720",
        "https://picsum.photos/seed/tvb2/1280/720",
        "https://picsum.photos/seed/tvb3/1280/720",
        "https://picsum.photos/seed/tvb4/1280/720",
        "https://picsum.photos/seed/tvb5/1280/720",
    )

    private val categories = listOf(
        "Trending", "Continue Watching", "Movies", "Series", "Kids"
    )

    private val items: List<ContentItem> = List(20) { idx ->
        ContentItem(
            id = "vod_$idx",
            title = "Sample Movie $idx",
            description = "An engaging description for Sample Movie $idx. Enjoy stunning visuals and captivating story.",
            durationMinutes = 90 + idx,
            posterUrl = posters[idx % posters.size],
            backdropUrl = backdrops[idx % backdrops.size],
            categories = listOf(categories[(idx % (categories.size - 1)) + 1]), // avoid Trending for variety
            isLive = false,
            streamUrl = if (idx % 2 == 0) sampleHls else sampleHls2
        )
    }

    private val featured: List<ContentItem> = items.take(5)

    private val continueWatching = items.slice(5..9)

    private val channels: List<Channel> = listOf(
        Channel("ch_1", 101, "News", "", sampleHls),
        Channel("ch_2", 102, "Sports", "", sampleHls2),
        Channel("ch_3", 103, "Movies", "", sampleHls),
        Channel("ch_4", 104, "Kids", "", sampleHls2),
    )

    private val now = System.currentTimeMillis() / 1000
    private val epg: List<Program> = channels.flatMap { ch ->
        (0..6).flatMap { hourOffset ->
            val start = now + hourOffset * 3600
            listOf(
                Program("pg_${ch.id}_${hourOffset}_a", ch.id, "Program A $hourOffset", "Description A", start, start + 1800),
                Program("pg_${ch.id}_${hourOffset}_b", ch.id, "Program B $hourOffset", "Description B", start + 1800, start + 3600),
            )
        }
    }

    private val resumePositions = mutableMapOf<String, Long>() // id -> positionMs

    suspend fun getFeatured(): List<ContentItem> {
        delay(50)
        return featured
    }

    suspend fun getCategories(): List<Category> {
        delay(20)
        return categories.map { Category(it) }
    }

    suspend fun getCategoryItems(category: String): List<ContentItem> {
        delay(50)
        return when (category) {
            "Trending" -> items.shuffled().take(12)
            "Continue Watching" -> continueWatching
            else -> items.filter { it.categories.contains(category) }.ifEmpty { items.shuffled().take(10) }
        }
    }

    suspend fun search(query: String): List<ContentItem> {
        delay(100)
        return items.filter { it.title.contains(query, ignoreCase = true) }.take(20)
    }

    suspend fun detailById(id: String): ContentItem? {
        delay(40)
        return items.find { it.id == id } ?: channels.find { it.id == id }?.let {
            ContentItem(
                id = it.id,
                title = it.name,
                description = "Live channel ${it.name}",
                durationMinutes = 0,
                posterUrl = posters.first(),
                backdropUrl = backdrops.first(),
                categories = listOf("Live"),
                isLive = true,
                streamUrl = it.streamUrl
            )
        }
    }

    suspend fun getChannels(): List<Channel> {
        delay(40)
        return channels
    }

    suspend fun getEPGForChannel(channelId: String): List<Program> {
        delay(50)
        return epg.filter { it.channelId == channelId }
    }

    suspend fun getLiveEPG(): Map<Channel, List<Program>> {
        delay(80)
        return channels.associateWith { ch -> epg.filter { it.channelId == ch.id } }
    }

    fun saveResumePosition(id: String, positionMs: Long) {
        resumePositions[id] = max(0, positionMs)
    }

    fun getResumePosition(id: String): Long {
        return resumePositions[id] ?: 0L
    }

    fun clearResume(id: String) {
        resumePositions.remove(id)
    }
}
