package com.example.android_tv_frontend.cast

import android.content.Context
import com.google.android.gms.cast.CastMediaControlIntent
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider

/**
 * PUBLIC_INTERFACE
 * Provides CastOptions for Google Cast Framework.
 * Replace DEFAULT_RECEIVER_APP_ID with the actual app ID in production.
 */
class CastOptionsProvider : OptionsProvider {
    override fun getCastOptions(context: Context): CastOptions {
        val appId = DEFAULT_RECEIVER_APP_ID // Placeholder, replace via manifest or build config if needed
        return CastOptions.Builder()
            .setReceiverApplicationId(appId)
            .build()
    }

    override fun getAdditionalSessionProviders(context: Context): MutableList<SessionProvider>? = null

    companion object {
        const val DEFAULT_RECEIVER_APP_ID = CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID
        // For placeholder custom: "YOUR_CAST_APP_ID"
    }
}
