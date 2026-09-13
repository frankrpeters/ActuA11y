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

package de.frpeters.actua11y.ui.topic.customactions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * No naive counterpart exists for this topic (requirements §4.5) — a naive version would simply
 * be a screen with no custom actions, which is a real failure but not an equivalent screen to
 * compare against. `showNaive` is accepted only to satisfy [de.frpeters.actua11y.navigation.Topic]'s
 * shared content signature; it has no effect here. `TopicRegistry`'s `supportsNaive = false` for
 * this topic already renders the app bar's toggle disabled, per the existing infrastructure in
 * `NaiveToggle.kt`/`AppScaffold.kt` — nothing new was needed there.
 */
@Composable
fun CustomActionsTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    CustomActionsBetter(modifier)
}
