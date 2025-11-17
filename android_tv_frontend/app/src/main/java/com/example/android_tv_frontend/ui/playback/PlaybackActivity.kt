package com.example.android_tv_frontend.ui.playback

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.android_tv_frontend.data.model.ContentItem

/**
 * PUBLIC_INTERFACE
 * PlaybackActivity integrates Media3 PlayerView to play VOD and Live streams with DPAD controls.
 */
class PlaybackActivity : FragmentActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var item: ContentItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerView = PlayerView(this).apply {
            useController = true
        }
        setContentView(playerView)
        item = intent.getParcelableExtra(ARG_ITEM)!!
    }

    override fun onStart() {
        super.onStart()
        player = ExoPlayer.Builder(this).build().also { exo ->
            playerView.player = exo
            val builder = MediaItem.Builder()
                .setUri(Uri.parse(item.videoUrl ?: item.backdropUrl ?: ""))
            if (item.isLive) {
                builder.setLiveConfiguration(MediaItem.LiveConfiguration.Builder().build())
            }
            val mediaItem = builder.build()
            exo.setMediaItem(mediaItem)
            exo.prepare()
            exo.playWhenReady = true
        }
    }

    override fun onStop() {
        super.onStop()
        playerView.player = null
        player?.release()
        player = null
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Basic DPAD handling falls back to PlayerView; keep default
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        private const val ARG_ITEM = "arg_item"

        // PUBLIC_INTERFACE
        fun createIntent(context: Context, item: ContentItem): Intent =
            Intent(context, PlaybackActivity::class.java).apply {
                putExtra(ARG_ITEM, item)
            }
    }
}
