package com.example.headposecontroller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.round

class OverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        textSize = 42f
        style = Paint.Style.FILL
    }

    private var yaw: Float = 0f
    private var pitch: Float = 0f
    private var roll: Float = 0f
    private var command: String = "DIAM"

    fun setResults(yaw: Float, pitch: Float, roll: Float, command: String) {
        this.yaw = yaw
        this.pitch = pitch
        this.roll = roll
        this.command = command
        invalidate()
    }

    // kompat lama
    fun setResults(yaw: Float, pitch: Float, command: String) {
        setResults(yaw, pitch, 0f, command)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText("Yaw  : ${round(yaw)}°", 20f, 60f, paintText)
        canvas.drawText("Pitch: ${round(pitch)}°", 20f, 110f, paintText)
        canvas.drawText("Roll : ${round(roll)}°", 20f, 160f, paintText)
        canvas.drawText("Cmd  : $command", 20f, 210f, paintText)
    }
}
