# Android TV App

- Theme: Ocean Professional (primary #2563EB, secondary/success #F59E0B, error #EF4444, background #f9fafb, surface #ffffff, text #111827)
- Features:
  - BrowseSupportFragment home with Featured and category rows
  - DetailsSupportFragment with Play / Watchlist / Cast actions
  - PlaybackActivity using Media3 (VOD and Live)
  - SearchSupportFragment voice/text search over mock repository
  - Live TV list navigating to live playback
  - Google Cast SDK with default media receiver ID
  - Optional developer PreviewWebViewActivity (loads http://localhost:3000)

Configuration:
- To use a real backend, set environment variable `API_BASE_URL` or Java system property `-Dapi.baseUrl=https://example.com/api/`.
- To use a custom Cast app ID, update `CastOptionsProvider.DEFAULT_RECEIVER_APP_ID`.

```bash
# Example Gradle task
./gradlew :app:assembleDebug
```
