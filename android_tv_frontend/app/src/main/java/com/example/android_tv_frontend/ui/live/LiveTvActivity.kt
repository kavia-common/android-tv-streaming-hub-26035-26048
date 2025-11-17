package com.example.android_tv_frontend.ui.live

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * Hosts LiveTvFragment listing channels.
 */
class LiveTvActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = androidx.fragment.app.FragmentContainerView(this).apply { id = android.R.id.content }
        setContentView(container)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, LiveTvFragment())
                .commit()
        }
    }

    companion object {
        // PUBLIC_INTERFACE
        fun createIntent(context: Context): Intent = Intent(context, LiveTvActivity::class.java)
    }
}
