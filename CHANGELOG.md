# NHT Fitness stabilization and redesign

## Fixed

- Replaced generic authentication failures with safe, actionable Firebase error messages.
- Preserved Firebase sessions at startup and moved cloud recovery to the activity-scoped session layer so navigation cannot cancel it.
- Added password reset, stronger account validation, bounded cloud-recovery time, and persisted-session routing.
- Made Firestore restore tolerant of malformed legacy documents and safe across multiple local user accounts.
- Added owner-only, field-bounded Firestore rules.
- Corrected nutrition totals to use each food's real serving weight instead of treating a serving as 100 g.
- Prevented food updates from using SQLite `REPLACE`, which could cascade-delete existing food logs.
- Prevented repeated edits to an already-completed workout set from repeatedly awarding consistency points.
- Added range and session validation for onboarding, profile, weight, food servings, cardio, hydration, body measurements, and workout sets.
- Replaced fixed 24-hour date arithmetic with local calendar-day calculations where it affected dashboards.
- Added bounded retry/network behavior and removed build settings associated with the supplied JVM/Kotlin memory crashes.

## Food search

- Added 409 bundled foods, including Bangladeshi meals and practical fitness staples.
- Search-as-you-type is now entirely local and ranked by exact name, prefix, brand, category, and local relevance.
- Online search is explicit, limited, validated, deduplicated, stored locally, and cached for seven days (one day for empty results).
- Open Food Facts remains available without configuration; USDA is optional through `USDA_API_KEY`.

## Design and cleanup

- Rebranded the visible app, launcher, splash screen, theme, and Gradle project as **NHT Fitness**.
- Reworked login, dashboard header/recovery card, bottom navigation, food search, food logging, and nutrition meal cards.
- Replaced the harsh neon palette with a restrained midnight, mint, lime, and periwinkle system.
- Removed guest mode, fake AI plate scanning, placeholder photo-journal UI, fake stealth mode, unsolicited reminders, automatic notification prompts, and the automatic daily weight dialog.
- Removed unused Analytics, Coil, Vico, and HTTP logging dependencies.

## Validation

- Added unit tests for serving-size nutrition, food ranking/cache policy, and BMI invalid-input handling.
- Source Kotlin delimiters were checked and all source XML/JSON files parse successfully.
- Stale/deleted-feature references, unsafe non-null assertions, unmanaged coroutine scopes, debug stack traces, and removed permissions were scanned.
- The Firebase package in `google-services.json` matches `com.aegisfit.app`.
- A full Gradle compile could not be started in the work sandbox because Gradle 9.5 was not cached and its distribution host was unreachable. Run `./gradlew testDebugUnitTest assembleDebug` with network access before release.
