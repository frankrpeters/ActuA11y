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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Naive implementation. Same notice card as [FontScaleBetter], same layout, same behaviour for a
 * sighted touch user at 100% font scale — the only difference is accessibility semantics, though
 * see the developer note for a deliberate, documented exception to that rule at larger scales.
 */
@Composable
fun FontScaleNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.font_scale_pane_title)

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.font_scale_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.font_scale_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Delivery notice ───────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.font_scale_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // NAIVE: a hardcoded fontSize (ignoring MaterialTheme.typography, which is what actually
        // respects the system font scale) inside a container capped with heightIn(max = ...) —
        // a fixed dp value that cannot grow no matter how large the text becomes. clipToBounds()
        // makes the resulting overflow actually invisible rather than merely undefined, matching
        // what "clips at 200%" looks like in practice. Also ignores the system Bold Text setting
        // by hardcoding FontWeight.Normal.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = FontScaleCardMaxHeight)
                .clipToBounds()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .padding(12.dp)
                .testTag("font_scale_card"),
        ) {
            Text(
                text = stringResource(R.string.font_scale_notice_body),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            )
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.font_scale_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun FontScaleNaivePreview() {
    MaterialTheme {
        FontScaleNaive()
    }
}
