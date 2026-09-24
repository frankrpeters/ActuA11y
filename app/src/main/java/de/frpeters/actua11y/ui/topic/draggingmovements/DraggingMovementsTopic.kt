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

package de.frpeters.actua11y.ui.topic.draggingmovements

import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import de.frpeters.actua11y.R

@Composable
fun DraggingMovementsTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) DraggingMovementsNaive(modifier) else DraggingMovementsBetter(modifier)
}

internal val PackingItemResIds: List<Int> = listOf(
    R.string.dragging_movements_item_passport,
    R.string.dragging_movements_item_charger,
    R.string.dragging_movements_item_jacket,
    R.string.dragging_movements_item_boots,
    R.string.dragging_movements_item_first_aid,
)

/**
 * The order of the packing list, shared by both versions. Items are string resource IDs, so the
 * order survives a locale change and each row has a stable key.
 */
@Stable
internal class ReorderState(initial: List<Int>) {
    val items = mutableStateListOf(*initial.toTypedArray())
    var draggedItem by mutableStateOf<Int?>(null)
    var dragOffset by mutableFloatStateOf(0f)

    fun move(from: Int, to: Int) {
        if (from in items.indices && to in items.indices) items.add(to, items.removeAt(from))
    }
}

/**
 * Long-press-and-drag reordering, identical in both versions. The dragged row follows the finger;
 * once it has travelled more than half a row, it swaps places with its neighbour.
 *
 * WHY: this is the path-based pointer gesture WCAG 2.2 SC 2.5.7 is about. It works well for a
 * sighted touch user with steady hands, and not at all for anyone who cannot perform it — a
 * switch-access user, a screen reader user (TalkBack's own touch-exploration consumes the long
 * press and drag), or someone with a tremor.
 */
internal fun Modifier.dragToReorder(state: ReorderState, @StringRes item: Int): Modifier = this
    .zIndex(if (state.draggedItem == item) 1f else 0f)
    .graphicsLayer { translationY = if (state.draggedItem == item) state.dragOffset else 0f }
    .pointerInput(item) {
        val rowHeight = size.height.toFloat()
        detectDragGesturesAfterLongPress(
            onDragStart = {
                state.draggedItem = item
                state.dragOffset = 0f
            },
            onDragEnd = {
                state.draggedItem = null
                state.dragOffset = 0f
            },
            onDragCancel = {
                state.draggedItem = null
                state.dragOffset = 0f
            },
            onDrag = { change, dragAmount ->
                change.consume()
                state.dragOffset += dragAmount.y
                val index = state.items.indexOf(item)
                if (state.dragOffset > rowHeight / 2 && index < state.items.lastIndex) {
                    state.move(index, index + 1)
                    state.dragOffset -= rowHeight
                } else if (state.dragOffset < -rowHeight / 2 && index > 0) {
                    state.move(index, index - 1)
                    state.dragOffset += rowHeight
                }
            },
        )
    }
