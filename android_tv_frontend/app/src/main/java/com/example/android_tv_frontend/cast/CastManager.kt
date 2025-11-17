package com.example.android_tv_frontend.cast

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.example.android_tv_frontend.data.model.ContentItem
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.MediaQueueItem
import com.google.android.gms.cast.MediaStatus
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastState

import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.images.WebImage

/**
 * PUBLIC_INTERFACE
 * Simple helper for Google Cast. Initializes CastContext and exposes an action to cast a ContentItem.
 */
class CastManager private constructor(private val appContext: Context) {

    fun showCastDialog(activity: Activity, item: ContentItem) {
        val castContext = CastContext.getSharedInstance(appContext)
        if (castContext.castState == CastState.NO_DEVICES_AVAILABLE) {
            Toast.makeText(activity, "No cast devices available", Toast.LENGTH_SHORT).show()
            return
        }
        // Show default media route dialog
        // If already connected, load media; otherwise, instruct user
        Toast.makeText(activity, "If connected to a Cast device, loading will begin.", Toast.LENGTH_SHORT).show()

        // If already connected, load media
        val session = castContext.sessionManager.currentCastSession
        val remote: RemoteMediaClient? = session?.remoteMediaClient
        if (remote != null && item.videoUrl != null) {
            val metadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE).apply {
                putString(MediaMetadata.KEY_TITLE, item.title)
                item.imageUrl.let { addImage(WebImage(android.net.Uri.parse(it))) }
            }
            val mediaInfo = com.google.android.gms.cast.MediaInfo.Builder(item.videoUrl)
                .setStreamType(
                    if (item.isLive) com.google.android.gms.cast.MediaInfo.STREAM_TYPE_LIVE
                    else com.google.android.gms.cast.MediaInfo.STREAM_TYPE_BUFFERED
                )
                .setContentType(if (item.videoUrl.endsWith(".m3u8")) "application/x-mpegURL" else "video/mp4")
                .setMetadata(metadata)
                .build()
            val request = MediaLoadRequestData.Builder()
                .setAutoplay(true)
                .setMediaInfo(mediaInfo)
                .build()
            remote.load(request)
        }
    }

    companion object {
        private var instance: CastManager? = null
        // PUBLIC_INTERFACE
        fun get(context: Context): CastManager {
            if (instance == null) instance = CastManager(context.applicationContext)
            return instance!!
        }
    }
}
