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

package de.frpeters.actua11y.ui.topic.focusnotobscured

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R

@Composable
fun FocusNotObscuredTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) FocusNotObscuredNaive(modifier) else FocusNotObscuredBetter(modifier)
}

// WHY: a fixed-height frame standing in for a checkout screen, so the pinned bar has something to
// be pinned to. The topic screen itself scrolls as normal around it.
internal val CheckoutFrameHeight = 320.dp

internal val PaymentFieldLabelResIds: List<Int> = listOf(
    R.string.focus_not_obscured_field_card_number,
    R.string.focus_not_obscured_field_name_on_card,
    R.string.focus_not_obscured_field_expiry,
    R.string.focus_not_obscured_field_security_code,
    R.string.focus_not_obscured_field_postcode,
)

// WHY: the fields and the bar are identical in both versions. Only where they are placed relative
// to each other — and to the on-screen keyboard — differs.
@Composable
internal fun PaymentField(labelRes: Int, index: Int) {
    var value by remember { mutableStateOf("") }
    OutlinedTextField(
        value = value,
        onValueChange = { value = it },
        label = { Text(text = stringResource(labelRes)) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("focus_not_obscured_field_$index"),
    )
}

@Composable
internal fun PayBar(modifier: Modifier = Modifier) {
    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 3.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("focus_not_obscured_pay_bar"),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.focus_not_obscured_total),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Button(onClick = { /* demonstration only */ }) {
                Text(text = stringResource(R.string.focus_not_obscured_pay))
            }
        }
    }
}
