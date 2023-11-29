package com.abhilash.apps.composecolorpicker.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.abhilash.apps.composecolorpicker.ColorPicker
import com.abhilash.apps.composecolorpicker.argbToHsv
import com.abhilash.apps.composecolorpicker.common.ui.theme.ComposeColorPickerTheme

@Composable
fun App() {
    ComposeColorPickerTheme {
        val initialHSV = argbToHsv(Color.Cyan.toArgb())
        val hsv = rememberSaveable {
            mutableStateOf(
                Triple(initialHSV[0], initialHSV[1], initialHSV[2])
            )
        }

        ColorPicker(initialHSV = initialHSV, hsv = hsv)

        DisposableEffect(hsv.value) {
            onDispose {
                println("INFO: $hsv.value")
            }
        }
    }
}
