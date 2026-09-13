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

package de.frpeters.actua11y.ui.topic.selectablecopyabletext

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * No naive counterpart exists for this topic (requirements §4.5) — same pattern as
 * [de.frpeters.actua11y.ui.topic.customactions.CustomActionsTopic]. The requirements table
 * previously said "Weak — §4.5" for this row, which disagreed with §4.5's own paragraph listing
 * this topic among the fully disabled ones; corrected to "No — §4.5" while building this.
 */
@Composable
fun SelectableCopyableTextTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    SelectableCopyableTextBetter(modifier)
}
