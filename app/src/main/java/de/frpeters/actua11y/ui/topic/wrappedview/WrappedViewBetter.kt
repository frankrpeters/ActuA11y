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

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.SeekBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.RangeInfoCompat
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Better implementation. The same unmodified [LegacyStarRatingView] as [WrappedViewNaive],
 * wrapped the same way, looking and behaving identically for a sighted touch user — with its
 * accessibility supplied from outside the class, through the public View and AndroidX APIs.
 */
@Composable
fun WrappedViewBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.wrapped_view_pane_title)
    val filledColor = MaterialTheme.colorScheme.primary.toArgb()
    val emptyColor = MaterialTheme.colorScheme.outlineVariant.toArgb()

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.wrapped_view_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.wrapped_view_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Rate your delivery ────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.wrapped_view_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        val label = stringResource(R.string.wrapped_view_rating_label)
        AndroidView(
            factory = { context ->
                LegacyStarRatingView(context).apply {
                    rating = INITIAL_RATING
                    // BETTER: everything below is applied to the View *instance* from outside.
                    // LegacyStarRatingView itself is the same unmodified class the Naive
                    // version uses.
                    //
                    // A name, and the current value as a state description rather than folded
                    // into the name — see Topic 18 for why the two are kept apart.
                    contentDescription = label
                    ViewCompat.setStateDescription(this, ratingStateDescription(rating))
                    // BETTER: ViewCompat.setStateDescription also notifies accessibility
                    // services of the change, so TalkBack announces the new value when it
                    // changes, however it was changed.
                    onRatingChanged = { newRating ->
                        ViewCompat.setStateDescription(this, ratingStateDescription(newRating))
                    }
                    // BETTER: focusable, so a keyboard user can reach it, with the arrow keys
                    // changing the value — the View's own key handling does nothing.
                    isFocusable = true
                    setOnKeyListener { _, keyCode, event ->
                        if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_UP -> {
                                rating += 1
                                true
                            }
                            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_DOWN -> {
                                rating -= 1
                                true
                            }
                            else -> false
                        }
                    }
                    ViewCompat.setAccessibilityDelegate(this, StarRatingAccessibilityDelegate(this))
                }
            },
            update = { view ->
                view.filledColor = filledColor
                view.emptyColor = emptyColor
                view.invalidate()
            },
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.wrapped_view_developer_note_better))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun WrappedViewBetterPreview() {
    MaterialTheme {
        WrappedViewBetter()
    }
}

private fun View.ratingStateDescription(rating: Int): String =
    resources.getQuantityString(R.plurals.wrapped_view_rating_state, MAX_RATING, rating, MAX_RATING)

/**
 * BETTER: describes the View to accessibility services as an adjustable control, and handles the
 * adjustments. AccessibilityDelegateCompat is the standard hook for this: it is consulted every
 * time an accessibility service asks the View for its node info or asks it to perform an action,
 * without subclassing the View.
 */
private class StarRatingAccessibilityDelegate(
    private val view: LegacyStarRatingView,
) : AccessibilityDelegateCompat() {

    override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
        super.onInitializeAccessibilityNodeInfo(host, info)
        // WHY: accessibility services map class names to roles. Reporting SeekBar tells TalkBack
        // this is an adjustable control, which is what enables its "adjust value" gestures.
        // TODO(verify): the exact role TalkBack announces for this node, and that its adjust
        // gestures (swipe up/down in the default configuration) trigger the scroll actions below.
        info.className = SeekBar::class.java.name
        info.rangeInfo = RangeInfoCompat(
            RangeInfoCompat.RANGE_TYPE_INT,
            0f,
            MAX_RATING.toFloat(),
            view.rating.toFloat(),
        )
        if (view.rating < MAX_RATING) info.addAction(AccessibilityActionCompat.ACTION_SCROLL_FORWARD)
        if (view.rating > 0) info.addAction(AccessibilityActionCompat.ACTION_SCROLL_BACKWARD)
    }

    override fun performAccessibilityAction(host: View, action: Int, args: Bundle?): Boolean =
        when (action) {
            AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD -> {
                view.rating += 1
                true
            }
            AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD -> {
                view.rating -= 1
                true
            }
            else -> super.performAccessibilityAction(host, action, args)
        }
}
