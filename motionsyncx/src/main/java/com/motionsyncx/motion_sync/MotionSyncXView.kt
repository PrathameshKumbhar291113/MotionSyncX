package com.motionsyncx.motion_sync

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.motionsyncx.motion_sync.utils.MotionSyncSensorHandler

class MotionSyncXView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private val sensorHandler = MotionSyncSensorHandler(context)

    private val positions = mutableListOf<PointF>()
    private val velocities = mutableListOf<PointF>()
    private val pointFPool = mutableListOf<PointF>()

    private var sizePx: Float = dpToPx(50f)
    private var spacingPx: Float = dpToPx(8f)
    private var speedMultiplier: Float = 1.0f

    init {
        sensorHandler.onSensorChanged = { x, y ->
            handleSensorChange(x, y)
        }
    }

    fun setup(
        size: Float,
        spacing: Float,
        speedMultiplier: Float
    ) {
        this.sizePx = dpToPx(size)
        this.spacingPx = dpToPx(spacing)
        this.speedMultiplier = speedMultiplier

        requestLayout()
    }

    private fun handleSensorChange(x: Float, y: Float) {
        if (positions.isEmpty() || velocities.isEmpty()) return

        val maxX = (width - sizePx).coerceAtLeast(0f)
        val maxY = (height - sizePx).coerceAtLeast(0f)

        val updatedVelocities = velocities.map { velocity ->
            PointF(
                (velocity.x - x * 0.05f * speedMultiplier).coerceIn(-5f, 5f),
                (velocity.y + y * 0.05f * speedMultiplier).coerceIn(-5f, 5f)
            )
        }

        val updatedPositions = positions.mapIndexed { index, pos ->
            PointF(
                (pos.x + updatedVelocities[index].x).coerceIn(0f, maxX),
                (pos.y + updatedVelocities[index].y).coerceIn(0f, maxY)
            )
        }

        positions.clear()
        positions.addAll(updatedPositions)
        velocities.clear()
        velocities.addAll(updatedVelocities)

        invalidate()
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(
            MarginLayoutParams.WRAP_CONTENT,
            MarginLayoutParams.WRAP_CONTENT
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        for (i in 0 until childCount) {
            measureChildWithMargins(getChildAt(i), widthMeasureSpec, 0, heightMeasureSpec, 0)
        }
        setMeasuredDimension(
            View.resolveSizeAndState(width, widthMeasureSpec, 0),
            View.resolveSizeAndState(height, heightMeasureSpec, 0)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val childCount = childCount

        positions.clear()
        velocities.clear()

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val params = child.layoutParams as MarginLayoutParams

            val position = getPointF().apply {
                x = params.leftMargin.toFloat()
                y = params.topMargin.toFloat()
            }
            positions.add(position)

            velocities.add(
                getPointF().apply {
                    x = 0f
                    y = 0f
                }
            )

            child.layout(
                params.leftMargin,
                params.topMargin,
                params.leftMargin + child.measuredWidth,
                params.topMargin + child.measuredHeight
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val pos = positions[i]
            child.draw(canvas)
            child.layout(
                pos.x.toInt(),
                pos.y.toInt(),
                (pos.x + child.measuredWidth).toInt(),
                (pos.y + child.measuredHeight).toInt()
            )
        }
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    private fun getPointF(): PointF {
        return if (pointFPool.isNotEmpty()) {
            pointFPool.removeAt(pointFPool.size - 1)
        } else {
            PointF()
        }
    }

    fun startListening() {
        sensorHandler.startListening()
    }

    fun stopListening() {
        sensorHandler.stopListening()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pointFPool.addAll(positions)
        pointFPool.addAll(velocities)
        positions.clear()
        velocities.clear()
    }
}
