package com.example.android_tv_frontend.ui.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.android_tv_frontend.R
import com.example.android_tv_frontend.data.Repository
import com.example.android_tv_frontend.data.model.Category
import com.example.android_tv_frontend.data.model.ContentItem
import com.example.android_tv_frontend.ui.details.DetailsActivity
import com.example.android_tv_frontend.ui.live.LiveTvActivity
import com.example.android_tv_frontend.ui.search.SearchActivity
import kotlinx.coroutines.launch

/**
 * PUBLIC_INTERFACE
 * A Leanback BrowseSupportFragment home screen showing featured carousel and category rows.
 */
class HomeBrowseFragment : BrowseSupportFragment(),
    OnItemViewClickedListener, OnItemViewSelectedListener {

    companion object {
        // PUBLIC_INTERFACE
        fun newInstance() = HomeBrowseFragment()
    }

    private val viewModel: HomeViewModel by viewModels()
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private lateinit var cardPresenter: CardPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.app_name)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = Color.parseColor("#2563EB")
        searchAffordanceColor = Color.WHITE

        setOnSearchClickedListener {
            startActivity(SearchActivity.createIntent(requireContext()))
        }

        onItemViewClickedListener = this
        onItemViewSelectedListener = this

        adapter = rowsAdapter

        cardPresenter = CardPresenter()

        buildStaticRows()
        loadDynamicRows()
    }

    private fun buildStaticRows() {
        // Left-side "Settings/Developer" row for optional PreviewWebView
        val devAdapter = ArrayObjectAdapter(cardPresenter)
        devAdapter.add(SimpleCard("Developer Preview", "Open Preview WebView"))
        val header = HeaderItem(0, "Settings")
        rowsAdapter.add(ListRow(header, devAdapter))
    }

    private fun loadDynamicRows() {
        lifecycleScope.launch {
            val repo = Repository()
            val categories = repo.getCategories()

            // Featured
            val featuredAdapter = ArrayObjectAdapter(cardPresenter)
            repo.getContents("featured").take(10).forEach { featuredAdapter.add(it) }
            rowsAdapter.add(ListRow(HeaderItem(1, "Featured"), featuredAdapter))

            // Other categories
            var rowId = 2L
            categories.filter { it.id != "featured" && it.id != "live" }.forEach { cat ->
                val adapter = ArrayObjectAdapter(cardPresenter)
                repo.getContents(cat.id).forEach { adapter.add(it) }
                rowsAdapter.add(ListRow(HeaderItem(rowId++, cat.name), adapter))
            }

            // Live TV
            val liveAdapter = ArrayObjectAdapter(cardPresenter)
            liveAdapter.add(SimpleCard("Browse Live TV", "Open channel list"))
            rowsAdapter.add(ListRow(HeaderItem(rowId, "Live TV"), liveAdapter))
        }
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        when (item) {
            is ContentItem -> {
                startActivity(DetailsActivity.createIntent(requireContext(), item))
            }
            is SimpleCard -> {
                if (item.title.contains("Developer", true)) {
                    startActivity(com.example.android_tv_frontend.dev.PreviewWebViewActivity.createIntent(requireContext()))
                } else {
                    startActivity(LiveTvActivity.createIntent(requireContext()))
                }
            }
        }
    }

    override fun onItemSelected(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) = Unit
}

private data class SimpleCard(val title: String, val subtitle: String)

class CardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: android.view.ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setBackgroundColor(Color.parseColor("#ffffff"))
            setInfoAreaBackgroundColor(Color.parseColor("#2563EB"))
            setMainImageDimensions(313, 176)
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val cardView = viewHolder.view as ImageCardView
        when (item) {
            is ContentItem -> {
                cardView.titleText = item.title
                cardView.contentText = item.durationSec?.let { "${it / 60} min" } ?: ""
                cardView.badgeImage = null
                cardView.mainImage = ColorDrawable(Color.LTGRAY)
                Glide.with(cardView.context)
                    .load(item.imageUrl)
                    .centerCrop()
                    .into(cardView.mainImageView)
            }
            is SimpleCard -> {
                cardView.titleText = item.title
                cardView.contentText = item.subtitle
                cardView.mainImage = ColorDrawable(Color.parseColor("#F59E0B"))
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) = Unit
}

class HomeViewModel : androidx.lifecycle.ViewModel()
