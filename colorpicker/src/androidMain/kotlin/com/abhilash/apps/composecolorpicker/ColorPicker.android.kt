@file:Suppress("NAME_SHADOWING")

package com.abhilash.apps.composecolorpicker

import android.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.core.graphics.toRect


actual fun DrawScope.initBitmapCanvasPanel(): Triple<Any, Any, Pair<Any, Int>> {
    val bitmap = Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
    val hueCanvas = Canvas(bitmap)

    val huePanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
    return Triple(bitmap, hueCanvas, Pair(huePanel, (huePanel.width()).toInt()))
}

actual fun drawColorLines(hueCanvas: Any, huePanel: Any, hueColors: IntArray) {
    val hueCanvas = hueCanvas as Canvas
    val huePanel = huePanel as RectF
    val linePaint = Paint()
    linePaint.strokeWidth = 0F
    for (i in hueColors.indices) {
        linePaint.color = hueColors[i]
        hueCanvas.drawLine(i.toFloat(), 0F, i.toFloat(), huePanel.bottom, linePaint)
    }
}

actual fun DrawScope.drawBitmap(bitmap: Any, panel: Any) {
    val bitmap = bitmap as android.graphics.Bitmap
    val panel = panel as RectF
    drawIntoCanvas {
        it.nativeCanvas.drawBitmap(
            bitmap,
            null,
            panel.toRect(),
            null
        )
    }
}

actual fun getHuePositioningFuntion(huePanel: Any): (Float) -> Float {
    val huePanel = huePanel as RectF
    val pointToHue: (pointX: Float) -> Float = { pointX ->
        val width = huePanel.width()
        val x = when {
            pointX < huePanel.left -> 0F
            pointX > huePanel.right -> width
            else -> pointX - huePanel.left
        }
        (x * 360f) / width
    }
    return pointToHue
}

actual fun getSaturationValueShader(rgb: Int, satValPanel: Any): Pair<Any, Any> {
    val satValPanel = satValPanel as RectF
    val satShader =  LinearGradient(
        satValPanel.left, satValPanel.top, satValPanel.right, satValPanel.top,
        -0x1, rgb, Shader.TileMode.CLAMP
    )
    val valShader = LinearGradient(
        satValPanel.left, satValPanel.top, satValPanel.left, satValPanel.bottom,
        -0x1, -0x1000000, Shader.TileMode.CLAMP
    )
    return Pair(satShader, valShader)
}

actual fun drawRoundRect(
    canvas: Any, satValPanel: Any, cornerRadius: Float,
    satShader: Any, valShader: Any
) {
    val canvas = canvas as Canvas
    val satValPanel = satValPanel as RectF
    val satShader = satShader as LinearGradient
    val valShader = valShader as LinearGradient
    canvas.drawRoundRect(
        satValPanel,
        cornerRadius,
        cornerRadius,
        Paint().apply {
            shader = ComposeShader(
                valShader,
                satShader,
                PorterDuff.Mode.MULTIPLY
            )
        }
    )
}

actual fun getSatValPositioningFuntion(satValPanel: Any): (Float, Float) -> Pair<Float, Float> {
    val satValPanel = satValPanel as RectF
    val pointToSatVal: (pointX: Float, pointY: Float) -> Pair<Float, Float> = { pointX, pointY ->
        val width = satValPanel.width()
        val height = satValPanel.height()

        val x = when {
            pointX < satValPanel.left -> 0f
            pointX > satValPanel.right -> width
            else -> pointX - satValPanel.left
        }

        val y = when {
            pointY < satValPanel.top -> 0f
            pointY > satValPanel.bottom -> height
            else -> pointY - satValPanel.top
        }

        val satPoint = 1f / width * x
        val valuePoint = 1f - 1f / height * y

        satPoint to valuePoint
    }
    return pointToSatVal
}
