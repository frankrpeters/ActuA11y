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

package de.frpeters.actua11y.ui.topic.reducedmotion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

// WHY: shared with ReducedMotionBetter.kt so a reader can see both files use the identical
// starting-point duration and only the scaling behaviour differs.
private const val BaseDurationMillis = 400

/**
 * Naive implementation. Same "Show shipping details" disclosure as [ReducedMotionBetter], same
 * layout, same behaviour for a user with no motion sensitivity — the only difference is that this
 * version always animates at a fixed duration. See the developer note.
 */
@Composable
fun ReducedMotionNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.reduced_motion_pane_title)
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.reduced_motion_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.reduced_motion_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        Text(
            text = stringResource(R.string.reduced_motion_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .semantics(mergeDescendants = true) { role = Role.Button }
                .testTag("reduced_motion_toggle"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.reduced_motion_toggle_label),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(text = if (expanded) "▲" else "▼")
        }
        // NAIVE: BaseDurationMillis is used directly, unconditionally, ignoring
        // Settings.Global.ANIMATOR_DURATION_SCALE entirely. A user who has turned system
        // animations off (accessibility settings, or Developer Options' "Window/Transition/
        // Animator scale" set to 0.5x/off) to avoid motion-triggered discomfort still gets the
        // full animated expand/collapse here, since nothing in this file ever reads that setting.
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(tween(BaseDurationMillis)) + fadeIn(tween(BaseDurationMillis)),
            exit = shrinkVertically(tween(BaseDurationMillis)) + fadeOut(tween(BaseDurationMillis)),
        ) {
            Text(
                text = stringResource(R.string.reduced_motion_details_text),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.reduced_motion_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun ReducedMotionNaivePreview() {
    MaterialTheme {
        ReducedMotionNaive()
    }
}
