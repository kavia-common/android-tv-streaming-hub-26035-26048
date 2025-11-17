# Android TV App

## Overview
This Android TV frontend provides a remote-friendly streaming experience for both on-demand videos and live TV channels. It follows the Ocean Professional theme and uses Leanback components for TV navigation, Media3 for playback, Retrofit/OkHttp for optional networking, Glide for images, and Google Cast for Chromecast support.

## Theme
The app uses the Ocean Professional theme with the following palette:
- Primary: #2563EB
- Secondary/Success: #F59E0B
- Error: #EF4444
- Background: #f9fafb
- Surface: #ffffff
- Text: #111827

These colors are defined in res/values/colors.xml and applied via a Theme.Leanback-based style in res/values/styles.xml.

## Features
The app implements the following user-facing capabilities:
- Home browsing using a BrowseSupportFragment with a Featured row, category rows, and a Live TV entry.
- Content details using a DetailsSupportFragment with actions for Play, Watchlist, and Cast.
- Video playback for VOD and Live streams using Media3’s ExoPlayer integrated in a PlayerView.
- Global and in-app Search using SearchSupportFragment with voice and text input.
- Live TV browsing that lists channels and launches live playback on selection.
- Chromecast support via Google Cast SDK using the default media receiver by default.
- Optional developer WebView preview activity to load http://localhost:3000.

## App Navigation
The TV app is optimized for DPAD/remote navigation and uses Leanback components for consistency:
- Launch/Main entry: MainActivity hosts HomeBrowseFragment (BrowseSupportFragment).
- Home screen:
  - Settings row: includes a “Developer Preview” card to launch PreviewWebViewActivity.
  - Featured row: showcases highlighted VOD items.
  - Category rows: displays contents grouped by categories such as Movies, Series, Kids.
  - Live TV row: provides a “Browse Live TV” card that opens Live TV list.
- Details: Selecting a content card opens DetailsActivity which hosts ContentDetailsFragment. Actions include:
  - Play: launches PlaybackActivity with the selected item.
  - Watchlist: toggles a local visual acknowledgement.
  - Cast: attempts to cast playback when connected to a Cast device.
- Search: The SearchActivity hosts SearchFragment supporting voice/text input. Results show as a row; selecting an item opens details.
- Live TV: The LiveTvActivity hosts LiveTvFragment listing mock channels; selecting a channel opens PlaybackActivity in live mode.
- Playback: PlaybackActivity uses Media3 ExoPlayer and PlayerView with standard TV controls via DPAD.

## Build and Run
Prerequisites:
- Java 17 and Android SDK (compileSdk 34, targetSdk 34)
- Android Studio Hedgehog or later, or Gradle 8.7 via wrapper

Build from command line:
```bash
# From the android_tv_frontend directory or repo root:
./gradlew :app:assembleDebug
```

Install and run on a connected Android TV device or emulator:
```bash
./gradlew :app:installDebug
adb shell monkey -p com.example.android_tv_frontend -c android.intent.category.LEANBACK_LAUNCHER 1
```

You can also launch specific screens for debugging:
- Main launcher (Home): see above
- Search:
```bash
adb shell am start -n com.example.android_tv_frontend/.ui.search.SearchActivity
```
- Live TV list:
```bash
adb shell am start -n com.example.android_tv_frontend/.ui.live.LiveTvActivity
```
- Optional developer WebView (loads http://localhost:3000):
```bash
adb shell am start -n com.example.android_tv_frontend/.dev.PreviewWebViewActivity
```

## Chromecast Setup
The app integrates Google Cast via the Cast framework:
- Options provider: com.example.android_tv_frontend.cast.CastOptionsProvider
- Default receiver app ID: CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID

To use a custom Cast receiver app ID:
1. Open app/src/main/java/com/example/android_tv_frontend/cast/CastOptionsProvider.kt.
2. Replace DEFAULT_RECEIVER_APP_ID with your Cast application ID, for example:
```kotlin
companion object {
    const val DEFAULT_RECEIVER_APP_ID = "YOUR_CAST_APP_ID"
}
```
3. Rebuild and reinstall the app.

Alternatively, you can wire a BuildConfig field or a different mechanism if needed, but the code ships with the constant for clarity.

Cast framework registration:
- The AndroidManifest.xml declares the OPTIONS_PROVIDER_CLASS_NAME meta-data pointing at CastOptionsProvider.
- At runtime, CastManager checks CastContext and can load media to an active session if connected.

## API Configuration and Mock Data
The Repository class uses mock data by default. Networking via Retrofit is enabled automatically when a base URL is provided at runtime.

How it works:
- Repository checks for an API base URL through:
  - Java system property: api.baseUrl
  - Environment variable: API_BASE_URL
- If a base URL is present, Retrofit is initialized with Gson converter and OkHttp logging. The ApiService provides these endpoints:
  - GET categories
  - GET contents?categoryId=&q=
  - GET contents/{id}
  - GET live/channels
- If no base URL is configured or the request fails, Repository falls back to mock data for a consistent experience.

Configure a real backend (examples):
- Using a Java system property (e.g., Gradle task or Android Studio VM options):
```bash
# Example when launching tests or JVM tasks:
./gradlew :app:assembleDebug -Dapi.baseUrl=https://example.com/api/
```
- Using an environment variable (in the shell prior to launching the app):
```bash
export API_BASE_URL="https://example.com/api/"
./gradlew :app:installDebug
```
Note: For runtime on a device, environment variables from the host are generally not passed into the Android app process. The Java system property is also not persisted across device launches. If you need a persistent base URL for production, consider defining a BuildConfig field or a product flavor that sets the base URL at build time. The shipped logic is most useful for development sessions and instrumented runs.

Mock behavior:
- Categories: Featured, Movies, Series, Kids, Live TV
- VOD items: 20 sample items with images and a Big Buck Bunny MP4 URL
- Live channels: 10 channels using an HLS sample stream

## Notable Implementation Details
- HomeBrowseFragment builds rows asynchronously via Repository and Glide image loading for cards.
- ContentDetailsFragment uses a FullWidthDetailsOverviewRowPresenter and binds actions for Play/Watchlist/Cast.
- PlaybackActivity sets up Media3 ExoPlayer and configures live playback if the item is live.
- SearchFragment debounces input, queries Repository, and renders results as a single row.
- CastManager shows a basic message if no devices are available and loads media on an active Cast session.
- PreviewWebViewActivity is for developer use to load a local web app preview on http://localhost:3000.

## Project Structure
- Main entry: app/src/main/java/com/example/android_tv_frontend/MainActivity.kt
- Leanback UI:
  - Home: ui/home/HomeBrowseFragment.kt
  - Details: ui/details/DetailsActivity.kt and ContentDetailsFragment.kt
  - Search: ui/search/SearchActivity.kt and SearchFragment.kt
  - Live: ui/live/LiveTvActivity.kt and LiveTvFragment.kt
  - Playback: ui/playback/PlaybackActivity.kt
- Data layer:
  - ApiService.kt (Retrofit interface)
  - Repository.kt (mock-first with optional network)
  - Models.kt (ContentItem, Category, LiveChannel)
- Cast:
  - cast/CastOptionsProvider.kt
  - cast/CastManager.kt
- Theme and resources:
  - res/values/colors.xml, styles.xml, strings.xml
  - res/drawable/ic_launcher.xml, app_banner.xml

## Troubleshooting
- Build fails due to SDK/Gradle: Ensure Java 17 and Android Gradle Plugin 8.3 with Gradle 8.7 are in use (wrapper provided).
- No images on cards: Check network connectivity and Glide logs; mock URLs are public placeholders.
- No Cast devices shown: Verify the test device and Cast device are on the same network and Google Play Services are available.
- Backend not used: Confirm api.baseUrl Java property or API_BASE_URL environment variable is actually visible to the app process. Consider a BuildConfig-based approach for production.

### Kotlin compile error in ContentDetailsFragment.setOnActionClickedListener
If you see errors like:
- Unresolved reference: setOnActionClickedListener
- Cannot infer a type for this parameter
- Variable expected

This indicates the listener is being set on DetailsOverviewRow rather than on the FullWidthDetailsOverviewRowPresenter, or the callback signature is incorrect for the Leanback version in use.

Fix in code (reference snippet):
```kotlin
// After creating detailsPresenter and detailsOverviewRow
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

// Ensure you import:
import androidx.leanback.widget.OnActionClickedListener
```
Alternatively, if you prefer to set the listener on the row itself for older snippets, use:
```kotlin
detailsOverviewRow.actionsAdapter = SparseArrayObjectAdapter().apply {
    set(1, Action(1, getString(R.string.action_play)))
    set(2, Action(2, getString(R.string.action_watchlist)))
    set(3, Action(3, getString(R.string.action_cast)))
}
setOnItemViewClickedListener { _, item, _, _ -> /* handle row item clicks if needed */ }
```
Pick one consistent approach; the presenter-level OnActionClickedListener is recommended with the current Leanback library.

### Build fix note (exact patch to apply in ContentDetailsFragment)
Replace the current action listener wiring with the following block (after creating `detailsPresenter` and `detailsOverviewRow`):

```kotlin
import androidx.leanback.widget.OnActionClickedListener
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.SparseArrayObjectAdapter
import androidx.leanback.widget.Action

// Set actions
val playAction = Action(1, getString(R.string.action_play))
val watchlistAction = Action(2, getString(R.string.action_watchlist))
val castAction = Action(3, getString(R.string.action_cast))

detailsOverviewRow.actionsAdapter = SparseArrayObjectAdapter().apply {
    set(1, playAction)
    set(2, watchlistAction)
    set(3, castAction)
}

// Set presenter-level action click listener (Leanback 1.1.0-rc02)
(detailsPresenter as FullWidthDetailsOverviewRowPresenter).onActionClickedListener =
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
```

If still unresolved, ensure imports match the androidx.leanback.widget package and that `detailsPresenter` is the `FullWidthDetailsOverviewRowPresenter` instance used for the DetailsOverviewRow class in the ClassPresenterSelector.

## License
Internal/demo use. Replace assets and streams for production use. See proguard-rules.pro for current keep rules.

## Known Build Issue Addendum
If CI or local builds fail with Leanback action listener errors in ContentDetailsFragment (e.g., “Unresolved reference: setOnActionClickedListener”), apply the code shown in “Build fix note (exact patch to apply in ContentDetailsFragment)” above. After updating the presenter-level OnActionClickedListener and ensuring imports from androidx.leanback.widget, re-run:
```bash
./gradlew :app:clean :app:assembleDebug
```

## Configuration Quick Reference
- API base URL:
  - Environment variable: API_BASE_URL (example: https://example.com/api/)
  - Java system property: -Dapi.baseUrl=https://example.com/api/
  - Repository automatically uses Retrofit if the base URL is present; otherwise uses mock data.
- Chromecast receiver app ID:
  - File: app/src/main/java/com/example/android_tv_frontend/cast/CastOptionsProvider.kt
  - Constant: CastOptionsProvider.DEFAULT_RECEIVER_APP_ID
  - Replace with your custom app ID if not using the default media receiver.


Note: The documentation changes here do not modify source code automatically. CI will continue to fail until the ContentDetailsFragment.kt patch is applied in code as shown in the build fix note.

