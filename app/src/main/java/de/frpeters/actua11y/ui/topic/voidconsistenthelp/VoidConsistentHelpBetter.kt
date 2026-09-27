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

package de.frpeters.actua11y.ui.topic.voidconsistenthelp

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * The sole implementation for this topic — see [VoidConsistentHelpTopic] for why there is no Naive
 * counterpart. There is no interactive demonstration: the "demonstration" is the three headed
 * sections themselves, which TalkBack users can move between with heading navigation.
 */
@Composable
fun VoidConsistentHelpBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.void_consistent_help_pane_title)

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.void_consistent_help_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.void_consistent_help_no_naive_label),
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = stringResource(R.string.void_consistent_help_no_naive_explanation),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = stringResource(R.string.void_consistent_help_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── What the criterion asks ───────────────────────────────────────────────
        Text(
            text = stringResource(R.string.void_consistent_help_criterion_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.void_consistent_help_criterion_body),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Why it does not apply here ────────────────────────────────────────────
        Text(
            text = stringResource(R.string.void_consistent_help_scope_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.void_consistent_help_scope_body),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── What to check instead ─────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.void_consistent_help_instead_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.void_consistent_help_instead_body),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.void_consistent_help_developer_note))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun VoidConsistentHelpBetterPreview() {
    MaterialTheme {
        VoidConsistentHelpBetter()
    }
}
