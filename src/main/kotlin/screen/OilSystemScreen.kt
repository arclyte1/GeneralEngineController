package screen

import Controller
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun OilSystemScreen() {
    val oilSystemActive by Controller.oilSystemActive.collectAsState()

    Box(
        Modifier.fillMaxSize()
    ) {
        IconButton(onClick = { Controller.currentScreen.value = Screen.Main }) {
            Icon(Icons.Default.ArrowBack, null)
        }
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { Controller.oilSystemActive.value = true }, enabled = !oilSystemActive) {
                Text("Пуск")
            }
            Button(onClick = { Controller.oilSystemActive.value = false }, enabled = oilSystemActive) {
                Text("Стоп")
            }
        }
    }
}