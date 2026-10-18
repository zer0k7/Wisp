# Wisp

A visual wishlist organizer for Android. Kotlin + Jetpack Compose, built only by GitHub Actions, shipped to the Play Store after full testing.

## Overview
Wisp lets people save things they want (products, outfits, gadgets, skincare, gifts) from anywhere, organise them visually into boards, track price drops, and share or export them. It is offline-first, private by default, has no account requirement, and is customizable to an unusual degree.

## Architecture
- **Multi-module Gradle (Kotlin DSL, Version Catalog)**:
  - `:app` - Application entry point, navigation and screens
  - `:core:designsystem` - Theme engine, design tokens, custom components
  - `:core:database` - Room database entities, DAOs, and migrations
  - `:core:datastore` - User preferences and theme persistence
  - `:core:common` - Common utilities, dispatchers, result types
  - `:feature:*` - Feature modules (ui, domain, data)

## Tech Stack
- Kotlin 2.0 + Jetpack Compose + Material 3
- Room, DataStore, Hilt
- Coil 3, Jsoup, WorkManager, Glance
- CI: GitHub Actions only (Detekt, JUnit, Roborazzi)
