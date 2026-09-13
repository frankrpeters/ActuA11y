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

package de.frpeters.actua11y.ui.topic.selectablecopyabletext

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.copyText
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote
import kotlinx.coroutines.launch

/**
 * The sole implementation for this topic — see [SelectableCopyableTextTopic] for why there is no
 * Naive counterpart.
 */
@Composable
fun SelectableCopyableTextBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.selectable_copyable_text_pane_title)
    val orderId = stringResource(R.string.selectable_copyable_text_order_id)
    val copyLabel = stringResource(R.string.selectable_copyable_text_copy_action_label)
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    var copied by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.selectable_copyable_text_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.selectable_copyable_text_no_naive_label),
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = stringResource(R.string.selectable_copyable_text_no_naive_explanation),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = stringResource(R.string.selectable_copyable_text_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Delivery notes ────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.selectable_copyable_text_notes_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // BETTER: SelectionContainer makes the text genuinely selectable and copyable, the same
        // way selecting text in any other app works. It has no clickable children inside it here
        // — see the developer note for what goes wrong when it does.
        SelectionContainer(modifier = Modifier.testTag("selectable_copyable_text_notes")) {
            Text(
                text = stringResource(R.string.selectable_copyable_text_notes_body),
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        HorizontalDivider()

        // ── Order reference ───────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.selectable_copyable_text_order_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // BETTER: this Card is not real selectable text — SelectionContainer would not help it —
        // but it still needs a copy affordance. Modifier.semantics { copyText(label) { ... } }
        // gives an accessibility service a copy action to offer without needing real text
        // selection underneath it.
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("selectable_copyable_text_order_card")
                .semantics(mergeDescendants = true) {
                    copyText(copyLabel) {
                        coroutineScope.launch {
                            clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(copyLabel, orderId)))
                        }
                        copied = true
                        true
                    }
                },
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.selectable_copyable_text_order_label),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(text = orderId, style = MaterialTheme.typography.titleMedium)
            }
        }
        Text(
            text = if (copied) {
                stringResource(R.string.selectable_copyable_text_copied_status)
            } else {
                stringResource(R.string.selectable_copyable_text_not_copied_status)
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("selectable_copyable_text_copy_status"),
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.selectable_copyable_text_developer_note))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun SelectableCopyableTextBetterPreview() {
    MaterialTheme {
        SelectableCopyableTextBetter()
    }
}
