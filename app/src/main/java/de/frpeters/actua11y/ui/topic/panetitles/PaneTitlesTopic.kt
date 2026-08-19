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

package de.frpeters.actua11y.ui.topic.panetitles

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// WHY: shared by both PaneTitlesBetter and PaneTitlesNaive. A top-level class always compiles
// to its own class file regardless of visibility, so — unlike top-level functions — it cannot
// be redeclared per file; it lives here once instead.
internal enum class PaneTitlesView { SUMMARY, DETAILS }

@Composable
fun PaneTitlesTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) PaneTitlesNaive(modifier) else PaneTitlesBetter(modifier)
}
