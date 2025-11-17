package com.example.android_tv_frontend

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.example.android_tv_frontend.ui.home.HomeBrowseFragment

/**
 * PUBLIC_INTERFACE
 * Main Activity for Android TV.
 * Hosts the HomeBrowseFragment which renders the Leanback BrowseSupportFragment home.
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Host fragment container programmatically to keep it lean
        val container = androidx.fragment.app.FragmentContainerView(this).apply {
            id = R.id.main_container
            setBackgroundColor(ContextCompat.getColor(context, R.color.ocean_background))
        }
        setContentView(container)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.main_container, HomeBrowseFragment.newInstance())
            }
        }
    }
}
