package com.abhilash.apps.composecolorpicker.common

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController


fun MainViewController(): UIViewController = ComposeUIViewController {
    App()
}
