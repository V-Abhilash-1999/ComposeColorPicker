
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.abhilash.apps.composecolorpicker.ColorPicker
import com.abhilash.apps.composecolorpicker.common.App


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComposeColorPicker",
        undecorated = false
    ) {

        App()
    }
}


@Preview
@Composable
fun ColorPickerPreview() {
    MaterialTheme {
        ColorPicker()
    }
}
