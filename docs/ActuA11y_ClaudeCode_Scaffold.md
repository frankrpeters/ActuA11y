# Claude Code — ActuA11y Initial Scaffolding

## Context

This is an Android project created with Android Studio's "No Activity" template using AGP 9.2.1. It has no Kotlin configuration, no UI framework, and no source files beyond the bare project structure. The requirements document is at `docs/ActuA11y_Requirements.md` — read it before proceeding.

## Your Task

Set up the complete project scaffold from which all future development will proceed. This means configuring the build system, adding all necessary dependencies, and creating a working skeleton app with two screens. Nothing should be hardcoded that should be a resource; everything should compile and run cleanly.

---

## Step 1 — Build System

### `libs.versions.toml`

Add the following to the existing version catalog, preserving any existing entries:

```toml
[versions]
kotlin = "2.0.21"
composeBom = "2025.05.00"
coreKtx = "1.16.0"
lifecycleRuntimeKtx = "2.9.0"
activityCompose = "1.10.1"
navigationCompose = "2.9.0"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

[plugins]
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

### Project-level `build.gradle.kts`

Add the Kotlin plugins alongside the existing AGP plugin declaration:

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
```

### App-level `app/build.gradle.kts`

Replace the contents with the following. Preserve the existing `namespace` and `applicationId` values exactly as generated.

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "de.frpeters.actua11y"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.frpeters.actua11y"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
```

---

## Step 2 — Source Files

All source files go under `app/src/main/java/de/frpeters/actua11y/`.

### `MainActivity.kt`

Single activity, sets up the navigation host. Full screen, no window decorations needed.

```kotlin
package de.frpeters.actua11y

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.frpeters.actua11y.navigation.ActuA11yNavHost
import de.frpeters.actua11y.ui.theme.ActuA11yTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ActuA11yTheme {
                ActuA11yNavHost()
            }
        }
    }
}
```

### `navigation/NavRoutes.kt`

Defines all navigation routes as a sealed class or object. Start with two routes: home and one example topic screen.

```kotlin
package de.frpeters.actua11y.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TopicContentDescriptions : Screen("topic/content_descriptions")
}
```

### `navigation/ActuA11yNavHost.kt`

Sets up the NavHost with the two initial destinations.

```kotlin
package de.frpeters.actua11y.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.frpeters.actua11y.ui.home.HomeScreen
import de.frpeters.actua11y.ui.topic.contentdescriptions.ContentDescriptionsScreen

@Composable
fun ActuA11yNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.TopicContentDescriptions.route) {
            ContentDescriptionsScreen(onBack = { navController.popBackStack() })
        }
    }
}
```

### `ui/theme/Theme.kt`

Standard Material3 theme. Use dynamic color on API 31+ with a sensible fallback palette. Keep it simple — the theme is not the point of this project.

### `ui/home/HomeScreen.kt`

The home screen lists accessibility categories. For now, one category ("TalkBack") with one item ("Content Descriptions") that navigates to the example topic screen.

Requirements for this screen's implementation (these are themselves reference patterns):
- The screen has an `accessibilityPaneTitle` set via `Modifier.semantics { paneTitle = "..." }`
- The category title is marked as a heading: `Modifier.semantics { heading() }`
- Each list item has a meaningful `contentDescription` that reads as a complete sentence to TalkBack, not just the label
- The `LazyColumn` items are individually focusable
- Minimum touch target size of 48dp for all interactive elements

Include a short explanatory text at the top of the screen (plain language, non-technical) describing what the app is and how to use it with TalkBack.

### `ui/topic/contentdescriptions/ContentDescriptionsScreen.kt`

The first topic screen, demonstrating correct content description usage. This screen serves as the template for all future topic screens.

Structure:
1. `TopAppBar` with a back button and the topic title. Back button must have a meaningful `contentDescription` (not just "back" — use "Navigate back" or similar).
2. Topic description paragraph (plain language).
3. At least two interactive demonstration components:
   - An `Image` with a meaningful content description (use a drawable from the project, or a simple `Canvas`-drawn shape if none exists)
   - An icon-only `IconButton` with a content description
   - A purely decorative image with `contentDescription = null` and `Role` set to indicate it is decorative
4. A collapsible "Developer note" section at the bottom explaining the `contentDescription` and `semantics` APIs used, written for a developer audience.
5. _(Debug only)_ A button in the `TopAppBar` actions area labeled "Bad version" that is only rendered when `BuildConfig.DEBUG` is true. For now it can show a `Toast` saying "Bad version not yet implemented" — the navigation destination will be added later.

---

## Step 3 — AndroidManifest.xml

Ensure the manifest declares `MainActivity` as the launcher activity with:
- `android:label` pointing to a string resource `@string/app_name` with value `ActuA11y`
- No hardcoded strings in the manifest

---

## Step 4 — Verify

After completing the above:
1. Confirm the project compiles without errors or warnings beyond expected "unused import" noise.
2. Confirm the app launches to the home screen.
3. Confirm navigation to the ContentDescriptions screen and back works.
4. Confirm the "Bad version" button appears in a debug build and is absent in a release build.

Do not proceed past any compilation error — fix it before moving on.

---

## Notes

- Do not add any dependencies beyond those listed above.
- Do not use `rememberCoroutineScope` or any async machinery — this scaffold is purely UI.
- String resources go in `res/values/strings.xml` — do not hardcode UI strings in Kotlin files.
- The bad-example screens will be added in a future session. Stub navigation entries are not needed yet.
