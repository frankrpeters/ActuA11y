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

package de.frpeters.actua11y.ui.topic.reducedmotion

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.math.roundToInt

@Composable
fun ReducedMotionTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) {
        ReducedMotionNaive(modifier)
    } else {
        ReducedMotionBetter(modifier)
    }
}

// WHY: shared so both the Better implementation and its test apply exactly the same rule.
// Settings.Global.ANIMATOR_DURATION_SCALE is a multiplier the platform's own View animators
// already respect (0 = off, 1 = normal, 2 = "extra slow animations" in Developer Options) —
// Compose animations do not read it automatically, so honouring it here means applying the same
// multiplier by hand, not just snapping to instant-or-not.
internal fun scaledAnimationDurationMillis(animatorDurationScale: Float, baseDurationMillis: Int): Int =
    (baseDurationMillis * animatorDurationScale).roundToInt()
