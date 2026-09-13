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

package de.frpeters.actua11y.ui.topic.textfieldlabelling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Naive implementation. Same "Name" field as [TextFieldLabellingBetter], same layout, same
 * behaviour for a sighted touch user — the only difference is accessibility semantics.
 */
@Composable
fun TextFieldLabellingNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.text_field_labelling_pane_title)
    var name by remember { mutableStateOf("Alex") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.text_field_labelling_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.text_field_labelling_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Contact details ───────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.text_field_labelling_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // NAIVE: placeholder-as-label. OutlinedTextField only composes its placeholder while the
        // field's text is empty (confirmed by reading TextFieldImpl.kt:
        // "if (placeholder != null && transformedText.isEmpty() && showPlaceholder)") — once any
        // text is entered, the placeholder composable leaves the tree entirely, not just fades
        // out visually. With "Alex" already typed, this field has no name at all.
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text(text = stringResource(R.string.text_field_labelling_field_hint)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("text_field_labelling_field"),
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.text_field_labelling_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun TextFieldLabellingNaivePreview() {
    MaterialTheme {
        TextFieldLabellingNaive()
    }
}
