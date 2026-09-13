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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FontScaleTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) {
        FontScaleNaive(modifier)
    } else {
        FontScaleBetter(modifier)
    }
}

// WHY: shared so both files use exactly the same cap — Naive relies on it to demonstrate
// clipping, the test relies on it to distinguish "capped" from "grows with content".
internal val FontScaleCardMaxHeight = 64.dp
