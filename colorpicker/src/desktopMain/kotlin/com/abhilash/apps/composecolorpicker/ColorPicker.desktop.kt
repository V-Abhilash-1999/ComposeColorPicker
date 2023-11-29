@file:Suppress("NAME_SHADOWING")

package com.abhilash.apps.composecolorpicker

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect


actual fun DrawScope.initBitmapCanvasPanel(): Triple<Any, Any, Pair<Any, Int>> {
    val bitmap = Bitmap()
    bitmap.allocN32Pixels(size.width.toInt(), size.height.toInt(), false)
    val imageBitmap = bitmap.asComposeImageBitmap()
    val hueCanvas = Canvas(image = imageBitmap)
    val huePanel = Rect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
    return Triple(imageBitmap, hueCanvas, Pair(huePanel, (huePanel.width).toInt()))
}

actual fun drawColorLines(hueCanvas: Any, huePanel: Any, hueColors: IntArray) {
    val hueCanvas = hueCanvas as Canvas
    val huePanel = huePanel as Rect
    val linePaint = Paint()
    linePaint.strokeWidth = 0F
    for (i in hueColors.indices) {
        linePaint.color = Color(hueColors[i])
        hueCanvas.drawLine(Offset(i.toFloat(), 0F), Offset(i.toFloat(), huePanel.bottom), paint = linePaint)
    }
}


actual fun DrawScope.drawBitmap(bitmap: Any, panel: Any) {
    val bitmap = bitmap as ImageBitmap
    val panel = panel as Rect
    drawIntoCanvas {
        it.nativeCanvas.drawImageRect(Image.makeFromBitmap(bitmap.asSkiaBitmap()), panel)
    }
}

actual fun getHuePositioningFuntion(huePanel: Any): (Float) -> Float {
    val huePanel = huePanel as Rect
    val pointToHue: (pointX: Float) -> Float = { pointX ->
        val width = huePanel.width
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
    val satValPanel = satValPanel as Rect
    val satShader =  LinearGradientShader(
        Offset(satValPanel.left, satValPanel.top), Offset(satValPanel.right, satValPanel.top),
        listOf(Color(-0x1), Color(rgb)), null, TileMode.Clamp
    )
    val valShader =  LinearGradientShader(
        Offset(satValPanel.left, satValPanel.top), Offset(satValPanel.left, satValPanel.bottom),
        listOf(Color(0x7FFFFFFF), Color(0xFF000000)), null, TileMode.Clamp
    )
    return Pair(satShader, valShader)
}

fun Int.addAlphaToRGB(alpha: Int): Int {
    val red = (this shr 16) and 0xFF
    val green = (this shr 8) and 0xFF
    val blue = this and 0xFF

    val rgba = (red shl 24) or (green shl 16) or (blue shl 8) or (alpha and 0xFF)
    return rgba
}

actual fun drawRoundRect(
    canvas: Any, satValPanel: Any, cornerRadius: Float,
    satShader: Any, valShader: Any
) {
    val canvas = canvas as Canvas
    val satValPanel = satValPanel as Rect
    val satShader = satShader as Shader
    val valShader = valShader as Shader

    canvas.drawRoundRect(
        satValPanel.left,
        satValPanel.top,
        satValPanel.right,
        satValPanel.bottom,
        cornerRadius,
        cornerRadius,
        Paint().apply {
            shader = satShader
        }
    )
    canvas.drawRoundRect(
        satValPanel.left,
        satValPanel.top,
        satValPanel.right,
        satValPanel.bottom,
        cornerRadius,
        cornerRadius,
        Paint().apply {
            shader = valShader
        }
    )
}

actual fun getSatValPositioningFuntion(satValPanel: Any): (Float, Float) -> Pair<Float, Float> {
    val satValPanel = satValPanel as Rect
    val pointToSatVal: (pointX: Float, pointY: Float) -> Pair<Float, Float> = { pointX, pointY ->
        val width = satValPanel.width
        val height = satValPanel.height

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
