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

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.byValue
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

private const val PIN_LENGTH = 6
private val PIN_BOX_SIZE = 48.dp

/**
 * Better implementation. Same six-box PIN layout and the same show/hide control as
 * [PinShowHideNaive], same behaviour for a sighted touch user — the only difference is
 * accessibility semantics.
 */
@Composable
fun PinShowHideBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.pin_show_hide_pane_title)
    val fieldLabel = stringResource(R.string.pin_show_hide_field_label)
    val hiddenLabel = stringResource(R.string.pin_show_hide_state_hidden)
    val visibleLabel = stringResource(R.string.pin_show_hide_state_visible)

    var revealed by remember { mutableStateOf(false) }
    val state = rememberTextFieldState()
    // WHY: a plain digits-only filter chained with maxLength keeps the field itself unaware
    // of "PIN" as a concept — the same InputTransformation building blocks any BasicTextField
    // uses, not something bespoke to secure fields.
    val inputTransformation = remember {
        InputTransformation.byValue { _, proposed -> proposed.filter { it.isDigit() } }
            .maxLength(PIN_LENGTH)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            // WHY: paneTitle announces the screen name to TalkBack on arrival, since content
            // swaps below the persistent app bar don't trigger a full navigation announcement.
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
        // BETTER: one BasicSecureTextField bound to one TextFieldState, decorated to draw the
        // six-box appearance around it, instead of six independent boxes. TalkBack has exactly
        // one field to explain, focus, and edit — not six. It also unconditionally marks its
        // semantics node isPassword = true and sets contentType = ContentType.Password
        // (confirmed by reading BasicSecureTextField.kt), which is both the marker Android's
        // accessibility API exposes as AccessibilityNodeInfo.isPassword and the signal a
        // password manager's autofill looks for — neither happens automatically on a
        // hand-built row of plain text boxes, regardless of how it looks.
        BasicSecureTextField(
            state = state,
            modifier = Modifier
                .testTag("pin_show_hide_field")
                .semantics { contentDescription = fieldLabel },
            inputTransformation = inputTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = TextStyle(color = Color.Transparent),
            cursorBrush = SolidColor(Color.Transparent),
            textObfuscationMode = if (revealed) {
                TextObfuscationMode.Visible
            } else {
                TextObfuscationMode.Hidden
            },
            decorator = TextFieldDecorator { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (index in 0 until PIN_LENGTH) {
                            PinDigitBox(char = state.text.getOrNull(index), revealed = revealed)
                        }
                    }
                    // WHY: the real digits render (invisibly) here; the boxes above are pure
                    // decoration. This keeps the whole row as one focusable/editable target
                    // instead of splitting it across six.
                    innerTextField()
                }
            },
        )

        // BETTER: a Modifier.toggleable(role = Role.Switch) with an explicit stateDescription,
        // not a button whose own label swaps between "Show" and "Hide". State belongs on the
        // control that owns it, announced the way TalkBack expects a two-state control to
        // announce itself — not inferred from a verb, and not dependent on whatever event the
        // field happens to fire when its displayed value changes underneath it.
        Row(
            modifier = Modifier
                .testTag("pin_show_hide_toggle")
                .toggleable(
                    value = revealed,
                    onValueChange = { revealed = it },
                    role = Role.Switch,
                )
                .semantics { stateDescription = if (revealed) visibleLabel else hiddenLabel }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(checked = revealed, onCheckedChange = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.pin_show_hide_toggle_label))
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.pin_show_hide_developer_note_better))
    }
}

/** Purely decorative digit box — the real semantics live on the field this is drawn behind. */
@Composable
private fun PinDigitBox(char: Char?, revealed: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(PIN_BOX_SIZE)
            .border(
                width = 1.dp,
                color = if (char != null) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
                shape = RoundedCornerShape(8.dp),
            )
            // WHY: this box exists only so a sighted user sees six cells; TalkBack must never
            // land on it directly, or the field would once again be six elements instead of one.
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = when {
                char == null -> ""
                revealed -> char.toString()
                else -> "•"
            },
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun PinShowHideBetterPreview() {
    MaterialTheme {
        PinShowHideBetter()
    }
}
