package com.example.android_tv_frontend.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

/**
 * PUBLIC_INTERFACE
 * ExoPlayerManager
 * Small lifecycle-aware wrapper to create and release a single ExoPlayer instance and prepare streams.
 */
class ExoPlayerManager(private val context: Context) {

    private var _player: ExoPlayer? = null
    val player: ExoPlayer
        get() = _player ?: createPlayer().also { _player = it }

    private fun createPlayer(): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(context))
            .build().apply {
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_OFF
            }
    }

    fun prepare(url: String, title: String? = null, isLive: Boolean = false, resumePositionMs: Long = 0L) {
        val builder = MediaItem.Builder()
            .setUri(url)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .build()
            )
        if (isLive) {
            builder.setLiveConfiguration(MediaItem.LiveConfiguration.Builder().build())
        }
        val mediaItem = builder.build()
        player.setMediaItem(mediaItem)
        player.prepare()
        if (!isLive && resumePositionMs > 0) {
            player.seekTo(resumePositionMs)
        }
        player.play()
    }

    fun release() {
        _player?.release()
        _player = null
    }
}
