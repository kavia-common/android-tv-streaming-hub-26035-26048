package com.example.android_tv_frontend.ui.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.android_tv_frontend.data.model.ContentItem

/**
 * Hosts the ContentDetailsFragment.
 */
class DetailsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = androidx.fragment.app.FragmentContainerView(this).apply { id = android.R.id.content }
        setContentView(container)

        if (savedInstanceState == null) {
            val item: ContentItem = intent.getParcelableExtra(ARG_ITEM)!!
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, ContentDetailsFragment.newInstance(item))
                .commit()
        }
    }

    companion object {
        private const val ARG_ITEM = "arg_item"

        // PUBLIC_INTERFACE
        fun createIntent(context: Context, item: ContentItem): Intent =
            Intent(context, DetailsActivity::class.java).apply {
                putExtra(ARG_ITEM, item)
            }
    }
}
