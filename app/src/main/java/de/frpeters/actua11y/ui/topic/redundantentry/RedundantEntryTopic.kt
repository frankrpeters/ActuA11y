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

package de.frpeters.actua11y.ui.topic.redundantentry

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import de.frpeters.actua11y.R

@Composable
fun RedundantEntryTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) RedundantEntryNaive(modifier) else RedundantEntryBetter(modifier)
}

/**
 * One step's worth of address fields. Shared by [RedundantEntryNaive] and [RedundantEntryBetter]
 * so that the two differ only in what they do with it, not in how it is shaped.
 */
internal data class AddressFormState(
    val name: String = "",
    val street: String = "",
    val city: String = "",
)

// WHY: identical in both versions — the fields themselves are labelled correctly either way. What
// this topic compares is what happens between the two steps, not the fields on each.
@Composable
internal fun AddressFields(
    state: AddressFormState,
    onChange: (AddressFormState) -> Unit,
    tagPrefix: String,
) {
    OutlinedTextField(
        value = state.name,
        onValueChange = { onChange(state.copy(name = it)) },
        label = { Text(text = stringResource(R.string.redundant_entry_name_label)) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("${tagPrefix}_name"),
    )
    OutlinedTextField(
        value = state.street,
        onValueChange = { onChange(state.copy(street = it)) },
        label = { Text(text = stringResource(R.string.redundant_entry_street_label)) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("${tagPrefix}_street"),
    )
    OutlinedTextField(
        value = state.city,
        onValueChange = { onChange(state.copy(city = it)) },
        label = { Text(text = stringResource(R.string.redundant_entry_city_label)) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("${tagPrefix}_city"),
    )
}
