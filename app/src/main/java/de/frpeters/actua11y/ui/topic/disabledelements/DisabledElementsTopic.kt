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

package de.frpeters.actua11y.ui.topic.disabledelements

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DisabledElementsTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) DisabledElementsNaive(modifier) else DisabledElementsBetter(modifier)
}
