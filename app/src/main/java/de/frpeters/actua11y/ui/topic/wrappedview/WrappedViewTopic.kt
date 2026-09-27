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

package de.frpeters.actua11y.ui.topic.wrappedview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WrappedViewTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) WrappedViewNaive(modifier) else WrappedViewBetter(modifier)
}

internal const val MAX_RATING = 5
internal const val INITIAL_RATING = 3

/**
 * Stands in for a legacy custom View — the kind of class a hybrid codebase carries along from
 * before Compose, often from a library or another team, and cannot simply edit. Both versions of
 * this topic wrap the *same* class, unchanged; the Better version repairs its accessibility from
 * the outside.
 *
 * WHY: deliberately built the way such Views usually are, which is also why this topic is the one
 * exception to this project's Compose-only rule (requirements §3.7). It draws five stars on a
 * Canvas and handles touch itself. It does call performClick(), so it is not *broken* — but it
 * exposes nothing about what it is, what its value is, or how to change that value without
 * tapping a precise spot on the screen.
 */
internal class LegacyStarRatingView(context: Context) : View(context) {

    var rating: Int = 0
        set(value) {
            val clamped = value.coerceIn(0, MAX_RATING)
            if (clamped == field) return
            field = clamped
            invalidate()
            onRatingChanged?.invoke(clamped)
        }

    var onRatingChanged: ((Int) -> Unit)? = null
    var filledColor: Int = Color.BLACK
    var emptyColor: Int = Color.LTGRAY

    private val density = resources.displayMetrics.density
    private val starSizePx = 40 * density
    private val gapPx = 8 * density
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val starPath = Path()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (MAX_RATING * starSizePx + (MAX_RATING - 1) * gapPx).toInt() +
            paddingLeft + paddingRight
        val desiredHeight = starSizePx.toInt() + paddingTop + paddingBottom
        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec),
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until MAX_RATING) {
            val left = paddingLeft + i * (starSizePx + gapPx)
            buildStar(centreX = left + starSizePx / 2, centreY = paddingTop + starSizePx / 2)
            paint.color = if (i < rating) filledColor else emptyColor
            canvas.drawPath(starPath, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                val index = ((event.x - paddingLeft) / (starSizePx + gapPx)).toInt()
                rating = index.coerceIn(0, MAX_RATING - 1) + 1
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean = super.performClick()

    private fun buildStar(centreX: Float, centreY: Float) {
        val outer = starSizePx / 2
        val inner = outer * 0.45f
        starPath.reset()
        for (point in 0 until 10) {
            val radius = if (point % 2 == 0) outer else inner
            val angle = -PI / 2 + point * PI / 5
            val x = centreX + (radius * cos(angle)).toFloat()
            val y = centreY + (radius * sin(angle)).toFloat()
            if (point == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
        }
        starPath.close()
    }
}
