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

package de.frpeters.actua11y.ui.topic.verbatimstrings

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.VerbatimTtsAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Better implementation. Same reference code as [VerbatimStringsNaive], same layout, same
 * behaviour for a sighted touch user — the only difference is accessibility semantics.
 */
@Composable
fun VerbatimStringsBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.verbatim_strings_pane_title)
    val labelPrefix = stringResource(R.string.verbatim_strings_reference_prefix)
    val code = stringResource(R.string.verbatim_strings_reference_code)

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.verbatim_strings_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.verbatim_strings_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Order confirmation ────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.verbatim_strings_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // BETTER: withAnnotation(VerbatimTtsAnnotation(code)) marks just the code segment,
        // leaving the surrounding label text untouched — confirmed present in this Compose
        // version by reading ui-text's TtsAnnotation.kt and AnnotatedString.kt directly.
        // AnnotatedString.getTtsAnnotations() lets an instrumented test read this back.
        Text(
            text = buildAnnotatedString {
                append(labelPrefix)
                withAnnotation(VerbatimTtsAnnotation(code)) {
                    append(code)
                }
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("verbatim_strings_reference"),
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.verbatim_strings_developer_note_better))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun VerbatimStringsBetterPreview() {
    MaterialTheme {
        VerbatimStringsBetter()
    }
}
