package com.abhilash.apps.composecolorpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import androidx.compose.foundation.Canvas as CanvasComposable


/**
 * Color Picker Example
 */
@Composable
fun ColorPicker(
    modifier: Modifier = Modifier.fillMaxSize(),
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Column(modifier, verticalArrangement, horizontalAlignment) {
        val initialHSV = argbToHsv(Color.Cyan.toArgb())
        val hsv = remember {
            mutableStateOf(
                Triple(initialHSV[0], initialHSV[1], initialHSV[2])
            )
        }
        val backgroundColor = remember(hsv.value) {
            mutableStateOf(Color.hsv(hsv.value.first, hsv.value.second, hsv.value.third))
        }

        SaturationValuePanel(
            hue = hsv.value.first,
            modifier = Modifier,
            height = 300.dp,
            setSatVal = { sat, value ->
                hsv.value = Triple(hsv.value.first, sat, value)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        HueBar(
            modifier = Modifier.width(300.dp),
            initColor = initialHSV[0],
            setColor = { hue ->
                hsv.value = Triple(hue, hsv.value.second, hsv.value.third)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(backgroundColor.value)
        )
    }
}



/**
 * Hue Selector Bar
 * @see <a href="https://proandroiddev.com/color-picker-in-compose-f8c29744705">ComposeColorPicker Description</a>
 * @see <a href="https://github.com/V-Abhilash-1999/ComposeColorPicker">ComposeColorPicker Github</a>
 */
@Composable
fun HueBar(
    modifier: Modifier = Modifier.fillMaxWidth(),
    height: Dp = 80.dp,
    shape: Shape = RoundedCornerShape(32),
    selectorRadius: Dp = height / 4,
    selectorStroke: Dp = 10.dp,
    initColor: Float = 0f,
    setColor: (Float) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val pressOffset = rememberSaveable {
        mutableStateOf(0f)
    }
    val maxWidth = rememberSaveable {
        mutableStateOf(0)
    }
    CanvasComposable(
        modifier = modifier
            .height(height)
            .clip(shape)
            .emitDragGesture(interactionSource)
    ) {
        // Draw Hue Gradient Bitmap
        val drawScopeSize = size
        val (bitmap, hueCanvas, _huePanel) = initBitmapCanvasPanel()
        val (huePanel, panelWidth) = _huePanel

        // Resizable Picker Position
        if (maxWidth.value != 0 && maxWidth.value != panelWidth) {  // When Resized
            pressOffset.value = pressOffset.value * panelWidth.toFloat() / maxWidth.value.toFloat()
        }
        maxWidth.value = panelWidth
        if (pressOffset.value == 0f) {
            pressOffset.value = initColor / 360f * maxWidth.value.toFloat()
        }

        // Split these colors across the width of the huePanel
        val hueColors = getHueColorArray(panelWidth)

        // Draw each line of color in canvas
        drawColorLines(hueCanvas, huePanel, hueColors)

        // Draw into compose canvas
        drawBitmap(
            bitmap = bitmap,
            panel = huePanel
        )

        // Position Calculation
        val pointToHue = getHuePositioningFuntion(huePanel)

        // OnClick
        scope.collectForPress(interactionSource) { pressPosition ->
            val pressPos = pressPosition.x.coerceIn(0f..drawScopeSize.width)
            pressOffset.value = pressPos
            val selectedHue = pointToHue(pressPos)
            setColor(selectedHue)
        }

        // Draw Selection Slider
        drawCircle(
            Color.White,
            radius = selectorRadius.toPx(),
            center = Offset(pressOffset.value, size.height/2),
            style = Stroke(
                width = selectorStroke.toPx()
            )
        )
    }
}

expect fun DrawScope.initBitmapCanvasPanel(): Triple<Any, Any, Pair<Any, Int>>

private fun getHueColorArray(length: Int): IntArray {
    val hueColors = IntArray(length)
    var hue = 0f
    for (i in hueColors.indices) {
        hueColors[i] = hsvToArgb(hue, 1f, 1f)
        hue += 360f / hueColors.size
    }
    return hueColors
}

expect fun drawColorLines(hueCanvas: Any, huePanel: Any, hueColors: IntArray)

expect fun DrawScope.drawBitmap(bitmap: Any, panel: Any)

expect fun getHuePositioningFuntion(huePanel: Any): (Float) -> Float

fun hsvToArgbList(hue: Float, saturation: Float, value: Float, alpha: Float = 1f): IntArray {
    val c = value * saturation
    val x = c * (1 - abs((hue / 60) % 2 - 1))
    val m = value - c

    val (r, g, b) = when {
        0 <= hue && hue < 60 -> Triple(c, x, 0f)
        60 <= hue && hue < 120 -> Triple(x, c, 0f)
        120 <= hue && hue < 180 -> Triple(0f, c, x)
        180 <= hue && hue < 240 -> Triple(0f, x, c)
        240 <= hue && hue < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    val red = ((r + m) * 255).toInt()
    val green = ((g + m) * 255).toInt()
    val blue = ((b + m) * 255).toInt()

    val alphaInt = (alpha * 255).toInt()

    return intArrayOf(alphaInt, red, green, blue)
}

fun hsvToArgb(hue: Float, saturation: Float, value: Float, alpha: Float = 1f): Int {
    val color = hsvToArgbList(hue, saturation, value, alpha)
    return Color(color[1], color[2], color[3], color[0]).toArgb()
}

fun argbToHsv(argb: Int): FloatArray {
    val alpha = (argb ushr 24) and 0xFF
    val red = (argb ushr 16) and 0xFF
    val green = (argb ushr 8) and 0xFF
    val blue = argb and 0xFF

    val maxColor = maxOf(red, green, blue).toFloat()
    val minColor = minOf(red, green, blue).toFloat()
    val deltaColor = maxColor - minColor

    var hue = 0f
    val saturation = if (maxColor != 0f) deltaColor / maxColor else 0f
    val value = maxColor / 255f

    if (deltaColor != 0f) {
        hue = when (maxColor) {
            red.toFloat() -> (green - blue) / deltaColor + (if (green < blue) 6 else 0)
            green.toFloat() -> (blue - red) / deltaColor + 2
            blue.toFloat() -> (red - green) / deltaColor + 4
            else -> hue
        }
        hue *= 60
    }

    return floatArrayOf(hue, saturation, value, alpha / 255f)
}

fun CoroutineScope.collectForPress(
    interactionSource: InteractionSource,
    setOffset: (Offset) -> Unit
) {
    launch {
        interactionSource.interactions.collect { interaction ->
            (interaction as? PressInteraction.Press)
                ?.pressPosition
                ?.let(setOffset)
        }
    }
}

private fun Modifier.emitDragGesture(
    interactionSource: MutableInteractionSource
): Modifier = composed {
    val scope = rememberCoroutineScope()
    pointerInput(Unit) {
        detectDragGestures { input, _ ->
            scope.launch {
                interactionSource.emit(PressInteraction.Press(input.position))
            }
        }
    }.clickable(interactionSource, null) {}
}



/**
 * Saturation & Value Selector Panel
 * @see <a href="https://proandroiddev.com/color-picker-in-compose-f8c29744705">ComposeColorPicker Description</a>
 * @see <a href="https://github.com/V-Abhilash-1999/ComposeColorPicker">ComposeColorPicker Github</a>
 * @param height: The height of the Panel; if you use default modifier then width value will be ignored.
 * @param aspectRatio: height * aspectRatio => width
 */
@Composable
fun SatValPanel(
    hue: Float,
    modifier: Modifier = Modifier.fillMaxWidth(),
    height: Dp = 300.dp,
    aspectRatio: Float = 1f,
    cornerRadius: Dp = 32.dp,
    shape: Shape = RoundedCornerShape(cornerRadius),
    outerSelectorRadius: Dp = 8.dp,
    innerSelectorRadius: Dp = 2.dp,
    setSatVal: (Float, Float) -> Unit = { _, _ -> }
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val scope = rememberCoroutineScope()
    var sat: Float
    var value: Float

    val pressOffsetX = rememberSaveable {
        mutableStateOf(0f)
    }
    val pressOffsetY = rememberSaveable {
        mutableStateOf(0f)
    }
    val maxWidth = rememberSaveable {
        mutableStateOf(0f)
    }
    val maxHeight = rememberSaveable {
        mutableStateOf(0f)
    }

    CanvasComposable(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .height(height)
            .emitDragGesture(interactionSource)
            .clip(shape)
    ) {
        // Draw Saturation Gradient Bitmap
        val satValSize = size
        val (bitmap, canvas, _satValPanel) = initBitmapCanvasPanel()
        val (satValPanel, _) = _satValPanel

        // Resizable Picker Position
        if (maxWidth.value != 0f && maxWidth.value != satValSize.width) {  // When Resized
            pressOffsetX.value = pressOffsetX.value * satValSize.width / maxWidth.value
        }
        maxWidth.value = satValSize.width
        if (maxHeight.value != 0f && maxHeight.value != satValSize.height) {  // When Resized
            pressOffsetY.value = pressOffsetY.value * satValSize.height / maxHeight.value
        }
        maxHeight.value = satValSize.height

        // Create saturation gradient from top-left to top-right and value gradient from top-left to bottom-left
        val rgb = hsvToArgb(hue, 1f, 1f)
        val (satShader, valShader) = getSaturationValueShader(rgb, satValPanel)

        // Draw Panel
        drawRoundRect(canvas, satValPanel, cornerRadius.toPx(), satShader, valShader)

        // Draw into compose canvas
        drawBitmap(
            bitmap = bitmap,
            panel = satValPanel
        )

        // Position Calculation
        val pointToSatVal = getSatValPositioningFuntion(satValPanel)

        // OnClick
        scope.collectForPress(interactionSource) { pressPosition ->
            val pressPositionX = pressPosition.x.coerceIn(0f..satValSize.width)
            val pressPositionY = pressPosition.y.coerceIn(0f..satValSize.height)

            pressOffsetX.value = pressPositionX
            pressOffsetY.value = pressPositionY
            val (satPoint, valuePoint) = pointToSatVal(pressPositionX, pressPositionY)
            sat = satPoint
            value = valuePoint

            setSatVal(sat, value)
        }

        // Draw Selection Slider
        drawCircle(
            color = Color.White,
            radius = outerSelectorRadius.toPx(),
            center = Offset(pressOffsetX.value, pressOffsetY.value),
            style = Stroke(
                width = 2.dp.toPx()
            )
        )

        drawCircle(
            color = Color.White,
            radius = innerSelectorRadius.toPx(),
            center = Offset(pressOffsetX.value, pressOffsetY.value)
        )
    }
}

/**
 * Saturation & Value Selector Panel
 * @see <a href="https://proandroiddev.com/color-picker-in-compose-f8c29744705">ComposeColorPicker Description</a>
 * @see <a href="https://github.com/V-Abhilash-1999/ComposeColorPicker">ComposeColorPicker Github</a>
 * @param height: The height of the Panel; if you use default modifier then width value will be ignored.
 * @param aspectRatio: height * aspectRatio => width
 */
@Composable
fun SaturationValuePanel(
    hue: Float,
    modifier: Modifier = Modifier.fillMaxWidth(),
    height: Dp = 300.dp,
    aspectRatio: Float = 1f,
    cornerRadius: Dp = 32.dp,
    shape: Shape = RoundedCornerShape(cornerRadius),
    outerSelectorRadius: Dp = 8.dp,
    innerSelectorRadius: Dp = 2.dp,
    setSatVal: (Float, Float) -> Unit = { _, _ -> }
) {
    SatValPanel(hue, modifier, height, aspectRatio,
        cornerRadius, shape, outerSelectorRadius, innerSelectorRadius, setSatVal)
}

expect fun getSaturationValueShader(rgb: Int, satValPanel: Any): Pair<Any, Any>

expect fun drawRoundRect(
    canvas: Any, satValPanel: Any, cornerRadius: Float,
    satShader: Any, valShader: Any
)

expect fun getSatValPositioningFuntion(satValPanel: Any): (Float, Float) -> Pair<Float, Float>
