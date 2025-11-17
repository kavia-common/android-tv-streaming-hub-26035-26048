package com.example.android_tv_frontend.ui.details

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
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
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.android_tv_frontend.R
import com.example.android_tv_frontend.cast.CastManager
import com.example.android_tv_frontend.data.model.ContentItem
import com.example.android_tv_frontend.ui.playback.PlaybackActivity

/**
 * PUBLIC_INTERFACE
 * DetailsSupportFragment showing content details and actions, visually aligned with Figma 'Content Info'.
 * - Applies a subtle horizontal gradient overlay over the backdrop to improve text contrast.
 * - Uses Ocean theme colors for surfaces and action areas.
 * - Preserves Leanback DPAD and focus behavior.
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

        // Class presenter selector with customized Details presenter
        presenterSelector = ClassPresenterSelector()
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter()).apply {
            // Figma indicates a light surface info area with colored accents.
            // We'll use surface white for the info area background and Ocean primary for actions background highlight.
            backgroundColor = Color.parseColor("#FFFFFF")
            actionsBackgroundColor = Color.parseColor("#2563EB")

            // Enable initial state aligned with LB defaults; keep large layout for TV
            setParticipatingEntranceTransition(true)

            // Increase spacing subtly by tweaking the description and actions alignment via row background paddings
            // (Leanback doesn't expose direct paddings; we'll rely on description typography to appear balanced.)
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter().apply {
            shadowEnabled = false
            selectEffectEnabled = true
        })

        adapterRows = ArrayObjectAdapter(presenterSelector)
        adapter = adapterRows

        detailsOverviewRow = DetailsOverviewRow(item).apply {
            // Placeholder before Glide loads
            imageDrawable = ColorDrawable(Color.DKGRAY)
        }

        // Define actions per Figma: Play (primary), Watchlist (bookmark), Cast
        val playAction = Action(1, getString(R.string.action_play))
        val watchlistAction = Action(2, getString(R.string.action_watchlist))
        val castAction = Action(3, getString(R.string.action_cast))

        detailsOverviewRow.actionsAdapter = SparseArrayObjectAdapter().apply {
            set(1, playAction)
            set(2, watchlistAction)
            set(3, castAction)
        }

        adapterRows.add(detailsOverviewRow)

        // Presenter-level OnActionClickedListener to satisfy current Leanback API
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

        // Set background behind the fragment to darken overall page like Figma (dark content zone with light info surface)
        view?.setBackgroundColor(Color.parseColor("#121212"))
    }

    override fun onStart() {
        super.onStart()
        // Load main image with a gradient overlay to emulate Figma "GradientHorizontalFull".
        Glide.with(requireContext())
            .load(item.backdropUrl ?: item.imageUrl)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    detailsOverviewRow.imageDrawable = applyHorizontalGradientOverlay(resource)
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    detailsOverviewRow.imageDrawable = placeholder ?: ColorDrawable(Color.DKGRAY)
                }
            })
    }

    /**
     * Create a bitmap with a right-side darkening horizontal gradient applied over the source drawable.
     * This improves readability of text and matches the design references.
     */
    private fun applyHorizontalGradientOverlay(source: Drawable): Drawable {
        val width = if (source.intrinsicWidth > 0) source.intrinsicWidth else 1280
        val height = if (source.intrinsicHeight > 0) source.intrinsicHeight else 720

        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        // Draw the original image
        source.setBounds(0, 0, width, height)
        source.draw(canvas)

        // Draw horizontal gradient overlay: transparent -> semi-transparent black
        val paint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, width.toFloat(), 0f,
                intArrayOf(
                    Color.TRANSPARENT,
                    Color.parseColor("#99000000") // ~60% at the far right
                ),
                floatArrayOf(0.35f, 1.0f), // start 35%, end 100%
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(Rect(0, 0, width, height), paint)

        return android.graphics.drawable.BitmapDrawable(resources, bmp)
    }
}

private class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {
    override fun onBindDescription(viewHolder: ViewHolder, item: Any) {
        val content = item as ContentItem
        // Title: larger, aligns with Figma typo-32 scale (approx on TV)
        viewHolder.title.text = content.title

        // Subtitle metadata: duration in minutes; genres not available directly -> keep concise runtime
        viewHolder.subtitle.text = content.durationSec?.let { "${it / 60} min" } ?: ""

        // Body: synopsis/description, Leanback handles max lines
        viewHolder.body.text = content.description
    }
}
