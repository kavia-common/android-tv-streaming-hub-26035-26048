package com.example.android_tv_frontend.ui.home

import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.android_tv_frontend.R
import com.example.android_tv_frontend.data.Repository
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
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var cardPresenter: CardPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = ""
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = Color.parseColor("#2563EB")
        searchAffordanceColor = Color.WHITE

        // Background: subtle gradient to darker top matching Figma
        view?.setBackgroundColor(Color.parseColor("#121212"))

        setOnSearchClickedListener {
            startActivity(SearchActivity.createIntent(requireContext()))
        }

        onItemViewClickedListener = this
        onItemViewSelectedListener = this

        val listRowPresenter = ListRowPresenter().apply {
            shadowEnabled = false
            selectEffectEnabled = true

            // Header styling: use a TextView-based header with our style
            headerPresenter = object : RowHeaderPresenter(R.style.HomeRowHeader) {
                init {
                    setNullItemVisibilityGone(true)
                }
            }

            // Note: ListRowPresenter in Leanback 1.1.0-rc02 does not expose direct spacing setters.
            // We keep default paddings to preserve DPAD behavior and apply spacing through card sizes.
        }

        rowsAdapter = ArrayObjectAdapter(listRowPresenter)
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
        viewLifecycleOwner.lifecycleScope.launch {
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

/**
 * CardPresenter that applies:
 * - Rounded corners
 * - Focus scale and focus ring via background drawable
 * - Bottom gradient overlay for text legibility
 * - Title and subtitle styling aligned with Ocean/Figma
 */
class CardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: android.view.ViewGroup): ViewHolder {
        val context = parent.context
        val width = context.resources.getDimensionPixelSize(R.dimen.home_card_width)
        val height = context.resources.getDimensionPixelSize(R.dimen.home_card_height)

        // Root container so we can overlay gradient + labels on top of image
        val container = FrameLayout(context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            layoutParams = android.view.ViewGroup.LayoutParams(width, height)
            background = context.getDrawable(R.drawable.bg_card_normal)
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(
                        0, 0, view.width, view.height,
                        context.resources.getDimension(R.dimen.home_card_radius)
                    )
                }
            }
        }

        val image = ImageView(context).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageDrawable(ColorDrawable(Color.DKGRAY))
        }
        container.addView(image)

        val overlay = View(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            background = context.getDrawable(R.drawable.overlay_card_gradient)
        }
        container.addView(overlay)

        // Labels at bottom
        val title = TextView(context).apply {
            id = View.generateViewId()
            setTextAppearance(context, R.style.HomeCardTitleText)
            setPadding(12, 0, 12, 8)
            ellipsize = android.text.TextUtils.TruncateAt.END
        }
        val subtitle = TextView(context).apply {
            id = View.generateViewId()
            setTextAppearance(context, R.style.HomeCardSubtitleText)
            setPadding(12, 0, 12, 12)
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        // Position labels manually at bottom
        val titleParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.BOTTOM
            bottomMargin = 24
        }
        val subtitleParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.BOTTOM
        }
        container.addView(subtitle, subtitleParams)
        container.addView(title, titleParams)

        // Focus behavior: scale and background swap
        container.setOnFocusChangeListener { v, hasFocus ->
            val scale = if (hasFocus) 1.06f else 1.0f
            v.animate().scaleX(scale).scaleY(scale)
                .setDuration(160)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
            v.background = if (hasFocus)
                v.context.getDrawable(R.drawable.bg_card_focused)
            else
                v.context.getDrawable(R.drawable.bg_card_normal)

            ViewCompat.setElevation(
                v,
                if (hasFocus)
                    v.context.resources.getDimension(R.dimen.home_card_elevation_focused)
                else
                    v.context.resources.getDimension(R.dimen.home_card_elevation_normal)
            )
        }

        // Return a properly typed Presenter.ViewHolder
        return CardViewHolder(container, image, title, subtitle)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val holder = viewHolder as CardPresenter.CardViewHolder
        when (item) {
            is ContentItem -> {
                holder.title.text = item.title
                holder.subtitle.text = item.durationSec?.let { "${it / 60} min" } ?: ""
                Glide.with(holder.container.context)
                    .load(item.imageUrl)
                    .centerCrop()
                    .into(holder.image)
            }
            is SimpleCard -> {
                holder.title.text = item.title
                holder.subtitle.text = item.subtitle
                holder.image.setImageDrawable(ColorDrawable(Color.parseColor("#F59E0B")))
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) = Unit

    private class CardViewHolder(
        val container: FrameLayout,
        val image: ImageView,
        val title: TextView,
        val subtitle: TextView
    ) : Presenter.ViewHolder(container)
}

class HomeViewModel : androidx.lifecycle.ViewModel()
