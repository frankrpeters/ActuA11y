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

package de.frpeters.actua11y.ui.topic.fontscale

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a height cap (dp, unaffected by font scale) wrapped around text that is
 * sized in sp (affected by font scale): at a large enough system font scale, the Naive card's
 * content outgrows its fixed-height container while the Better card's container has no cap and
 * grows with it. Both cards render the identical string at the identical width, so the only
 * variable between them is the presence of `heightIn(max = ...)` — this isolates that one
 * difference rather than comparing two differently-styled screens.
 */
@RunWith(AndroidJUnit4::class)
class FontScaleTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun atLargeFontScale_naiveCardStaysCappedWhileBetterCardGrows() {
        composeTestRule.setContent {
            val scaledDensity = Density(
                density = LocalDensity.current.density,
                fontScale = 3f,
            )
            CompositionLocalProvider(LocalDensity provides scaledDensity) {
                MaterialTheme {
                    Column {
                        FontScaleNaive()
                        FontScaleBetter()
                    }
                }
            }
        }

        val cards = composeTestRule.onAllNodesWithTag("font_scale_card").fetchSemanticsNodes()
        val naiveHeightPx = cards[0].size.height
        val betterHeightPx = cards[1].size.height

        val capPx = with(composeTestRule.density) { FontScaleCardMaxHeight.roundToPx() }

        // The Naive card is structurally incapable of exceeding its fixed dp cap.
        assertTrue(
            "Naive card ($naiveHeightPx px) should not exceed its ${FontScaleCardMaxHeight} " +
                "cap ($capPx px)",
            naiveHeightPx <= capPx,
        )
        // The Better card has no such cap, so at 3x font scale the same text needs more room
        // than the cap allows — demonstrating the Naive card must be clipping its content.
        assertTrue(
            "Better card ($betterHeightPx px) should grow past the Naive cap ($capPx px) " +
                "at 3x font scale",
            betterHeightPx > capPx,
        )
    }
}
