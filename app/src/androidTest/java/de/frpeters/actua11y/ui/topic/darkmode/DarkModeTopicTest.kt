/*
 * Copyright 2026 Frank R. Peters
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.frpeters.actua11y.ui.topic.darkmode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for "does this card's colour actually track the active theme" — sampled with
 * `captureToImage()` rather than a semantics-tree property, since colour is a rendered pixel
 * value, not something Compose exposes in the semantics tree at all. Both versions are rendered
 * side by side, each wrapped in its own forced `lightColorScheme()`/`darkColorScheme()`
 * `MaterialTheme` and given a fixed height (so the second copy in the Column doesn't collapse
 * to zero height behind the first `fillMaxSize()` one), so the comparison isolates the one
 * variable — which palette is active — from the emulator's own actual system theme setting.
 */
@RunWith(AndroidJUnit4::class)
class DarkModeTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun SemanticsNodeInteraction.centerPixelLuminance() =
        captureToImage().toPixelMap().let { pixels ->
            pixels[pixels.width / 2, pixels.height / 2].luminance()
        }

    @Test
    fun naive_cardColourIsIdenticalInLightAndDarkTheme() {
        composeTestRule.setContent {
            Column {
                MaterialTheme(colorScheme = lightColorScheme()) {
                    DarkModeNaive(modifier = Modifier.height(300.dp))
                }
                MaterialTheme(colorScheme = darkColorScheme()) {
                    DarkModeNaive(modifier = Modifier.height(300.dp))
                }
            }
        }

        val cards = composeTestRule.onAllNodesWithTag("dark_mode_card")
        val lightLuminance = cards[0].centerPixelLuminance()
        val darkLuminance = cards[1].centerPixelLuminance()
        // Same hardcoded Color.White in both cases — the surrounding theme has no effect.
        assertEquals(lightLuminance, darkLuminance, 0.01f)
    }

    @Test
    fun better_cardColourTracksTheActiveTheme() {
        composeTestRule.setContent {
            Column {
                MaterialTheme(colorScheme = lightColorScheme()) {
                    DarkModeBetter(modifier = Modifier.height(300.dp))
                }
                MaterialTheme(colorScheme = darkColorScheme()) {
                    DarkModeBetter(modifier = Modifier.height(300.dp))
                }
            }
        }

        val cards = composeTestRule.onAllNodesWithTag("dark_mode_card")
        val lightLuminance = cards[0].centerPixelLuminance()
        val darkLuminance = cards[1].centerPixelLuminance()
        // MaterialTheme.colorScheme.primaryContainer resolves to a distinctly different colour
        // under each scheme — the card visibly follows whichever theme wraps it.
        assertNotEquals(lightLuminance, darkLuminance, 0.05f)
    }
}
