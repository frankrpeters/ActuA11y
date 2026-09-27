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

package de.frpeters.actua11y.ui.topic.wrappedview

import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a wrapped View. The Compose semantics tree cannot see inside an AndroidView,
 * so this test steps outside it: it finds the View in the Activity's hierarchy and asks it for
 * its AccessibilityNodeInfo — the same object an accessibility service receives, built through
 * the same delegate — and performs accessibility actions on the View directly.
 */
@RunWith(AndroidJUnit4::class)
class WrappedViewTopicTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun ratingView(): LegacyStarRatingView {
        composeTestRule.waitForIdle()
        return requireNotNull(
            composeTestRule.activity.window.decorView.findFirst<LegacyStarRatingView>(),
        )
    }

    private fun nodeInfo(view: View): AccessibilityNodeInfoCompat {
        var info: AccessibilityNodeInfoCompat? = null
        composeTestRule.runOnUiThread {
            info = AccessibilityNodeInfoCompat.wrap(view.createAccessibilityNodeInfo())
        }
        return requireNotNull(info)
    }

    @Test
    fun better_viewReportsAdjustableRoleRangeAndState() {
        composeTestRule.setContent {
            MaterialTheme { WrappedViewBetter() }
        }

        val info = nodeInfo(ratingView())
        assertEquals(SeekBar::class.java.name, info.className)
        assertNotNull(info.contentDescription)
        assertNotNull(info.stateDescription)
        assertEquals(INITIAL_RATING.toFloat(), info.rangeInfo?.current)
    }

    @Test
    fun better_scrollForwardActionRaisesRating() {
        composeTestRule.setContent {
            MaterialTheme { WrappedViewBetter() }
        }

        val view = ratingView()
        var handled = false
        composeTestRule.runOnUiThread {
            handled = view.performAccessibilityAction(
                AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD,
                null,
            )
        }

        assertTrue(handled)
        assertEquals(INITIAL_RATING + 1, view.rating)
    }

    @Test
    fun naive_viewReportsNothing() {
        composeTestRule.setContent {
            MaterialTheme { WrappedViewNaive() }
        }

        val view = ratingView()
        val info = nodeInfo(view)
        assertEquals(View::class.java.name, info.className)
        assertNull(info.contentDescription)
        assertNull(info.stateDescription)
        assertNull(info.rangeInfo)

        var handled = true
        composeTestRule.runOnUiThread {
            handled = view.performAccessibilityAction(
                AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD,
                null,
            )
        }
        assertFalse(handled)
        assertEquals(INITIAL_RATING, view.rating)
    }
}

/** Depth-first search of a View hierarchy for the first View of the given type. */
internal fun <T : View> View.findFirst(type: Class<T>): T? = when {
    type.isInstance(this) -> type.cast(this)
    this is ViewGroup -> (0 until childCount).firstNotNullOfOrNull { getChildAt(it).findFirst(type) }
    else -> null
}

internal inline fun <reified T : View> View.findFirst(): T? = findFirst(T::class.java)
