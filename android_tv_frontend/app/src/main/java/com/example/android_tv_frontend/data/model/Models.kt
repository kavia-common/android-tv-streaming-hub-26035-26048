package com.example.android_tv_frontend.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// PUBLIC_INTERFACE
@Parcelize
data class ContentItem(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val imageUrl: String,
    val backdropUrl: String? = null,
    val videoUrl: String? = null,
    val isLive: Boolean = false,
    val durationSec: Long? = null
) : Parcelable

// PUBLIC_INTERFACE
@Parcelize
data class Category(
    val id: String,
    val name: String
) : Parcelable

// PUBLIC_INTERFACE
@Parcelize
data class LiveChannel(
    val id: String,
    val number: String,
    val name: String,
    val logoUrl: String,
    val streamUrl: String
) : Parcelable
