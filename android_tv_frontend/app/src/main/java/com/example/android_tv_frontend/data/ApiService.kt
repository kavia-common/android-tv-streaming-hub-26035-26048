package com.example.android_tv_frontend.data

import com.example.android_tv_frontend.data.model.Category
import com.example.android_tv_frontend.data.model.ContentItem
import com.example.android_tv_frontend.data.model.LiveChannel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * PUBLIC_INTERFACE
 * Retrofit API definition. Can point to a real backend; by default repository uses mock data.
 */
interface ApiService {
    @GET("categories")
    suspend fun categories(): List<Category>

    @GET("contents")
    suspend fun contents(@Query("categoryId") categoryId: String? = null, @Query("q") query: String? = null): List<ContentItem>

    @GET("contents/{id}")
    suspend fun contentDetails(@Path("id") id: String): ContentItem

    @GET("live/channels")
    suspend fun liveChannels(): List<LiveChannel>
}
