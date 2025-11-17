# android-tv-streaming-hub-26035-26048

Android TV streaming hub demo app with Ocean Professional theme.

How to build
- Open the android_tv_frontend project in Android Studio (Giraffe+).
- Ensure JDK 17 is configured.
- Build/Run on an Android TV emulator or device (API 23+).

Navigation
- Left navigation (remote-friendly): Home, Live TV, On-Demand (routes to Home catalog), Search, Settings.
- Screens:
  - Home: Featured carousel + category rows (Trending, Continue Watching, Movies, Series, Kids).
  - Live TV: Simple guide with channels and now/next placeholders; press OK to play.
  - Search: Input field placeholder and results; select to open details.
  - Details: Poster, synopsis, Play/Watchlist actions with related items.
  - Player: Media3 ExoPlayer playback for mock HLS streams; saves resume position for VOD.
  - Settings: Placeholder options.

Theme
- Ocean Professional palette:
  - Primary #2563EB, Secondary #F59E0B, Error #EF4444, Background #F9FAFB, Surface #FFFFFF, Text #111827.
- Defined in ui/theme/Theme.kt with Material3 for Compose TV.

Mock data
- Served from data/MockContentRepository.kt and safe to replace later.

Notes
- No external services required; uses open sample HLS streams for testing.