package com.abhilash.apps.composecolorpicker.common

import androidx.compose.runtime.Composable
import com.abhilash.apps.composecolorpicker.ColorPicker
import com.abhilash.apps.composecolorpicker.common.ui.theme.ComposeColorPickerTheme

@Composable
fun App() {
    ComposeColorPickerTheme {
        ColorPicker()
    }
}
