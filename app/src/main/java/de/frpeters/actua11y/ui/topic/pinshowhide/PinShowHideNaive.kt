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

package de.frpeters.actua11y.ui.topic.pinshowhide

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

private const val PIN_LENGTH = 6
private val PIN_BOX_SIZE = 48.dp

/**
 * Naive implementation. Same six-box PIN layout and the same show/hide control as
 * [PinShowHideBetter] — the accessibility work was simply never done.
 */
@Composable
fun PinShowHideNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.pin_show_hide_pane_title)
    var revealed by remember { mutableStateOf(false) }
    val digits = remember { List(PIN_LENGTH) { mutableStateOf("") } }
    val focusRequesters = remember { List(PIN_LENGTH) { FocusRequester() } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.pin_show_hide_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.pin_show_hide_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── PIN field ──────────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.pin_show_hide_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // NAIVE: six independent text fields, one digit each, wired together only by focus
        // order. TalkBack has to be swiped through all six individually to reach or review the
        // PIN, and each one exposes a full text-editing action set (select all, move cursor,
        // cut, copy, paste) that makes little sense for a single character.
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            digits.forEachIndexed { index, digitState ->
                var digit by digitState
                // NAIVE: masking is done by hand — the field's own displayed text is swapped
                // between the digit and a bullet character. No PasswordVisualTransformation is
                // ever used, so isPassword (see CoreTextField.kt: derived automatically from
                // PasswordVisualTransformation) never becomes true here. TalkBack has no signal
                // this is sensitive content, and a password manager has nothing to autofill.
                BasicTextField(
                    value = if (revealed || digit.isEmpty()) digit else "•",
                    onValueChange = { newValue ->
                        val newDigit = newValue.filter { it.isDigit() }.takeLast(1)
                        digit = newDigit
                        if (newDigit.isNotEmpty() && index < PIN_LENGTH - 1) {
                            focusRequesters[index + 1].requestFocus()
                        }
                    },
                    modifier = Modifier
                        .testTag("pin_show_hide_box_$index")
                        .focusRequester(focusRequesters[index])
                        .size(PIN_BOX_SIZE)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                        .padding(top = 12.dp),
                    textStyle = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = if (index < PIN_LENGTH - 1) ImeAction.Next else ImeAction.Done,
                    ),
                )
            }
        }

        // NAIVE: this button's own visible label swaps between "Show PIN" and "Hide PIN" to
        // communicate state, instead of using a control TalkBack recognises as a two-state
        // toggle. And because each box's displayed text is what actually changes when this is
        // pressed — real digits replacing bullet characters, or the reverse — toggling fires an
        // ordinary text-changed accessibility event carrying the real value as its before/after
        // text, once per box, since none of these fields carry the isPassword marker that would
        // let Compose recognise and suppress that event.
        Box(
            modifier = Modifier
                .testTag("pin_show_hide_toggle")
                .clickable { revealed = !revealed }
                .padding(vertical = 4.dp),
        ) {
            Text(
                text = stringResource(
                    if (revealed) {
                        R.string.pin_show_hide_naive_toggle_hide
                    } else {
                        R.string.pin_show_hide_naive_toggle_show
                    },
                ),
            )
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.pin_show_hide_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun PinShowHideNaivePreview() {
    MaterialTheme {
        PinShowHideNaive()
    }
}
