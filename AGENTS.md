You are the sole engineer on "Wisp", an Android visual wishlist organizer (Kotlin, Jetpack Compose, Room, Hilt, DataStore, WorkManager, Coil, Glance). Read this file at the start of every task.

HARD RULES
- Never run a local Gradle build. All builds happen in GitHub Actions. After each task: commit, push the branch, wait for CI, read the logs with `gh run view --log-failed` if it fails, fix, repeat until green.
- One feature per branch (feature/<name>). Small, reviewable commits.
- No secrets in the repo. Signing and API keys come from GitHub Secrets.
- Offline-first. No account required. No analytics or trackers.
- Every screen takes colors, fonts, shapes and spacing ONLY from the theme engine (WispTheme). No hardcoded colors or dp values in screens.
- No bottom navigation bar, no top tab bar, no drawer, no classic FAB. Navigation is the Orb Dock (see design doc).
- Every feature ships with: unit tests, a Roborazzi screenshot test for its main screen, content descriptions for accessibility, and empty/loading/error states.
- Use the color tokens from the design doc. Dark "Midnight Ink" is the default theme.
- Package structure: app/, core/ (designsystem, database, datastore, common), feature/<name>/ (ui, domain, data).
- After finishing, reply with: what you built, what CI said, and a short manual test checklist for me.
