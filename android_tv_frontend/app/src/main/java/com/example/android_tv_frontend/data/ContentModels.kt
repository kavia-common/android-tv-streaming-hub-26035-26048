package com.example.android_tv_frontend.data

/**
 * PUBLIC_INTERFACE
 * Data models used across the TV app.
 */

/** Represents a content item (VOD or live) */
data class ContentItem(
    val id: String,
    val title: String,
    val description: String,
    val durationMinutes: Int,
    val posterUrl: String,
    val backdropUrl: String,
    val categories: List<String>,
    val isLive: Boolean,
    val streamUrl: String
)

/** A TV Channel */
data class Channel(
    val id: String,
    val number: Int,
    val name: String,
    val logoUrl: String,
    val streamUrl: String
)

/** An EPG Program airing on a channel */
data class Program(
    val id: String,
    val channelId: String,
    val title: String,
    val description: String,
    val startEpochSec: Long,
    val endEpochSec: Long
)

/** Category of content rows on Home */
data class Category(val name: String)
