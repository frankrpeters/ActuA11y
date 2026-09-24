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

package de.frpeters.actua11y.ui.topic.voidconsistenthelp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * No naive counterpart exists for this topic (requirements §4.5). It is a content-only note:
 * WCAG 2.2 SC 3.2.6 has no code pattern to get right or wrong in a native Compose app, so there is
 * nothing to build twice.
 */
@Composable
fun VoidConsistentHelpTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    VoidConsistentHelpBetter(modifier)
}
