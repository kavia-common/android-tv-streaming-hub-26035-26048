package com.example.android_tv_frontend.ui.search

import android.os.Bundle
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.android_tv_frontend.data.Repository
import com.example.android_tv_frontend.data.model.ContentItem
import com.example.android_tv_frontend.ui.details.DetailsActivity
import com.example.android_tv_frontend.ui.home.HomeBrowseFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * PUBLIC_INTERFACE
 * TV Search fragment with voice input support if available.
 */
class SearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private val repo = Repository()
    private var searchJob: Job? = null
    private val presenter = com.example.android_tv_frontend.ui.home.CardPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        setSearchResultProvider(this)
        setOnItemViewClickedListener { _, item, _, _ ->
            if (item is ContentItem) {
                startActivity(DetailsActivity.createIntent(requireContext(), item))
            }
        }
    }

    override fun getResultsAdapter(): ObjectAdapter = rowsAdapter

    override fun onQueryTextChange(newQuery: String?): Boolean {
        performSearch(newQuery)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        performSearch(query)
        return true
    }

    private fun performSearch(query: String?) {
        val q = (query ?: "").trim()
        searchJob?.cancel()
        if (q.isEmpty()) {
            rowsAdapter.clear()
            return
        }
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(250)
            val results = repo.search(q)
            val rowAdapter = ArrayObjectAdapter(presenter)
            results.forEach { rowAdapter.add(it) }
            rowsAdapter.clear()
            rowsAdapter.add(ListRow(HeaderItem(0, "Results for \"$q\""), rowAdapter))
        }
    }
}
