package com.example.android.customfancontroller.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.example.android.customfancontroller.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private enum class FanSpeed(val label: Int) {
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);

    companion object {
        fun next(currentFanSpeed: FanSpeed) : FanSpeed {
            val values = values()
            return values[(currentFanSpeed.ordinal + 1) % values.size]
        }
    }

}

private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35

class DialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0

) : View(context, attrs, defStyleAttr) {

    private var radius = 0.0f
    private var fanSpeed = FanSpeed.OFF

    private val pointPosition = PointF(0.0f, 0.0f)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var fanSpeedLowColor = 0
    private var fanSpeedMediumColor = 0
    private var fanSpeedMaxColor = 0

    // Init
    init {
        // Make clickable
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.DialView) {
            fanSpeedLowColor = getColor(R.styleable.DialView_fanColor1, 0)
            fanSpeedMediumColor = getColor(R.styleable.DialView_fanColor2, 0)
            fanSpeedMaxColor = getColor(R.styleable.DialView_fanColor3, 0)
        }

    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        fanSpeed = FanSpeed.next(fanSpeed)
        contentDescription = resources.getString(fanSpeed.label)

        invalidate()

        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = (min(w, h) / 2.0 * 0.8).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas != null) {
            // Set background of canvas to transparent
            setBackgroundColor(0)

            // Change color
            paint.color = when (fanSpeed) {
                FanSpeed.OFF -> Color.GRAY
                FanSpeed.LOW -> fanSpeedLowColor
                FanSpeed.MEDIUM -> fanSpeedMediumColor
                FanSpeed.HIGH -> fanSpeedMaxColor
            }

            // Draw circle
            canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)

            // Draw indicator
            val markerRadius = radius + RADIUS_OFFSET_INDICATOR
            pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
            paint.color = Color.BLACK
            canvas.drawCircle(pointPosition.x, pointPosition.y, radius / 12, paint)

            // Draw text
            val labelRadius = radius + RADIUS_OFFSET_LABEL
            for (i in FanSpeed.values()) {
                pointPosition.computeXYForSpeed(i, labelRadius)
                val label = resources.getString(i.label)
                canvas.drawText(label, pointPosition.x, pointPosition.y, paint)
            }
        }



    }

    // Helpers
    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
        // Angles are in radians.
        val startAngle = Math.PI * (9 / 8.0)
        val angle = startAngle + pos.ordinal * (Math.PI / 4)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }

}

