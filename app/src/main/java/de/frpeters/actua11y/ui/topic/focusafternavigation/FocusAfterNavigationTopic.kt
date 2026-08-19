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

package de.frpeters.actua11y.ui.topic.focusafternavigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.frpeters.actua11y.R

// WHY: shared by both FocusAfterNavigationBetter and FocusAfterNavigationNaive — a top-level
// class cannot be redeclared per file (see PaneTitlesTopic.kt), so it lives here once.
internal enum class FocusAfterNavigationFrequency(@param:StringRes val labelRes: Int) {
    DAILY(R.string.focus_after_navigation_option_daily),
    WEEKLY(R.string.focus_after_navigation_option_weekly),
    MONTHLY(R.string.focus_after_navigation_option_monthly),
}

@Composable
fun FocusAfterNavigationTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) FocusAfterNavigationNaive(modifier) else FocusAfterNavigationBetter(modifier)
}
