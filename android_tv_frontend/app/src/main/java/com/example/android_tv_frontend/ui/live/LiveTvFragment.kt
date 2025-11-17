package com.example.android_tv_frontend.ui.live

import android.graphics.Color
import android.os.Bundle
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.android_tv_frontend.R
import com.example.android_tv_frontend.data.Repository
import com.example.android_tv_frontend.data.model.ContentItem
import com.example.android_tv_frontend.data.model.LiveChannel
import com.example.android_tv_frontend.ui.playback.PlaybackActivity
import kotlinx.coroutines.launch

/**
 * PUBLIC_INTERFACE
 * Lists live channels and opens live playback on click.
 */
class LiveTvFragment : RowsSupportFragment(), OnItemViewClickedListener {

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private val repo = Repository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = rowsAdapter
        onItemViewClickedListener = this
        loadChannels()
    }

    private fun loadChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
            val channels = repo.getLiveChannels()
            val presenter = ChannelCardPresenter()
            val adapter = ArrayObjectAdapter(presenter)
            channels.forEach { adapter.add(it) }
            rowsAdapter.add(ListRow(HeaderItem(0, getString(R.string.live_title)), adapter))
        }
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        if (item is LiveChannel) {
            val vodLike = ContentItem(
                id = "live-${item.id}",
                title = "${item.number} • ${item.name}",
                description = "Live channel",
                categoryId = "live",
                imageUrl = item.logoUrl,
                backdropUrl = item.logoUrl,
                videoUrl = item.streamUrl,
                isLive = true
            )
            startActivity(PlaybackActivity.createIntent(requireContext(), vodLike))
        }
    }
}

private class ChannelCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: android.view.ViewGroup): ViewHolder {
        val view = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setMainImageDimensions(313, 176)
            setInfoAreaBackgroundColor(Color.parseColor("#2563EB"))
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val card = viewHolder.view as ImageCardView
        val ch = item as LiveChannel
        card.titleText = "${ch.number} ${ch.name}"
        card.contentText = "Live"
        Glide.with(card.context).load(ch.logoUrl).centerCrop().into(card.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) = Unit
}
