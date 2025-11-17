package com.example.android_tv_frontend.dev

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity

/**
 * PUBLIC_INTERFACE
 * Developer-only WebView preview that attempts to load http://localhost:3000.
 */
class PreviewWebViewActivity : FragmentActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this).apply {
            setBackgroundColor(Color.BLACK)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            webViewClient = WebViewClient()
        }
        setContentView(webView)

        val url = "http://localhost:3000"
        webView.loadUrl(url)
    }

    companion object {
        // PUBLIC_INTERFACE
        fun createIntent(context: Context): Intent = Intent(context, PreviewWebViewActivity::class.java)
    }
}
