package com.example.android_tv_frontend.ui.details

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.ObjectAdapter
import androidx.leanback.widget.OnActionClickedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import androidx.leanback.widget.SparseArrayObjectAdapter
import com.bumptech.glide.Glide
import com.example.android_tv_frontend.R
import com.example.android_tv_frontend.cast.CastManager
import com.example.android_tv_frontend.data.model.ContentItem
import com.example.android_tv_frontend.ui.playback.PlaybackActivity

/**
 * PUBLIC_INTERFACE
 * DetailsSupportFragment showing content details and actions.
 */
class ContentDetailsFragment : DetailsSupportFragment() {

    private lateinit var item: ContentItem
    private lateinit var presenterSelector: ClassPresenterSelector
    private lateinit var detailsOverviewRow: DetailsOverviewRow
    private lateinit var adapterRows: ArrayObjectAdapter

    companion object {
        private const val ARG_ITEM = "arg_item"

        // PUBLIC_INTERFACE
        fun newInstance(item: ContentItem) = ContentDetailsFragment().apply {
            arguments = bundleOf(ARG_ITEM to item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = requireArguments().getParcelable(ARG_ITEM)!!

        presenterSelector = ClassPresenterSelector()
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter()).apply {
            backgroundColor = Color.parseColor("#ffffff")
            actionsBackgroundColor = Color.parseColor("#2563EB")
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())

        adapterRows = ArrayObjectAdapter(presenterSelector)

        detailsOverviewRow = DetailsOverviewRow(item).apply {
            imageDrawable = android.graphics.drawable.ColorDrawable(Color.DKGRAY)
        }
        adapter = adapterRows

        // Actions
        val playAction = Action(1, getString(R.string.action_play))
        val watchlistAction = Action(2, getString(R.string.action_watchlist))
        val castAction = Action(3, getString(R.string.action_cast))

        detailsOverviewRow.actionsAdapter = SparseArrayObjectAdapter().apply {
            set(1, playAction)
            set(2, watchlistAction)
            set(3, castAction)
        }

        adapterRows.add(detailsOverviewRow)

        // Wire action clicks at the presenter-level (correct Leanback API)
        (detailsPresenter).onActionClickedListener =
            OnActionClickedListener { action ->
                when (action.id.toInt()) {
                    1 -> startActivity(PlaybackActivity.createIntent(requireContext(), item))
                    2 -> {
                        action.label1 = getString(R.string.action_watchlisted)
                        (adapter as ArrayObjectAdapter).notifyArrayItemRangeChanged(0, 1)
                    }
                    3 -> CastManager.get(requireContext()).showCastDialog(requireActivity(), item)
                }
            }

        // Load artwork (handled in onStart via Glide)
        view?.post {
            // no-op placeholder to align with lifecycle; image loading in onStart
        }
    }

    override fun onStart() {
        super.onStart()
        // Load main image asynchronously using Glide into row
        Glide.with(requireContext())
            .load(item.backdropUrl ?: item.imageUrl)
            .into(object : com.bumptech.glide.request.target.CustomTarget<android.graphics.drawable.Drawable>() {
                override fun onResourceReady(
                    resource: android.graphics.drawable.Drawable,
                    transition: com.bumptech.glide.request.transition.Transition<in android.graphics.drawable.Drawable>?
                ) {
                    detailsOverviewRow.imageDrawable = resource
                }
                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}
            })
    }
}

private class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {
    override fun onBindDescription(viewHolder: ViewHolder, item: Any) {
        val content = item as ContentItem
        viewHolder.title.text = content.title
        viewHolder.subtitle.text = content.durationSec?.let { "${it/60} min" } ?: ""
        viewHolder.body.text = content.description
    }
}
