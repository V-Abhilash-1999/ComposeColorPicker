package com.abhilash.apps.composecolorpicker.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.abhilash.apps.composecolorpicker.ui.theme.ComposeColorPickerTheme

@Composable
fun App() {
    ComposeColorPickerTheme {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val hsv = remember {
                val hsv = floatArrayOf(0f, 0f, 0f)
                AndroidColor.colorToHSV(Color.Blue.toArgb(), hsv)

                mutableStateOf(
                    Triple(hsv[0], hsv[1], hsv[2])
                )
            }
            val backgroundColor = remember(hsv.value) {
                mutableStateOf(Color.hsv(hsv.value.first, hsv.value.second, hsv.value.third))
            }

            SatValPanel(hue = hsv.value.first) { sat, value ->
                hsv.value = Triple(hsv.value.first, sat, value)
            }

            Spacer(modifier = Modifier.height(32.dp))

            HueBar { hue ->
                hsv.value = Triple(hue, hsv.value.second, hsv.value.third)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(backgroundColor.value)
            )
        }
    }
}
