import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import screen.*

@Composable
@Preview
fun App() {
    val screen by Controller.currentScreen.collectAsState()

    MaterialTheme {
        when (screen) {
            Screen.Main -> MainScreen()
            Screen.OilSystem -> OilSystemScreen()
            Screen.CoolingSystem -> CoolingSystemScreen()
            Screen.AirSystem -> AirSystemScreen()
            Screen.FuelSystem -> FuelSystemScreen()
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, state = WindowState().also { it.size = DpSize(1200.dp, 600.dp) }, resizable = false, title = "Контроллер двигателя") {
        App()
    }
}
