package screen

import Controller
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*


@Composable
fun MainScreen() {
    val power by Controller.power.collectAsState()
    val emergencyPower by Controller.emergencyPower.collectAsState()

    Row {
        Column {
            RadioButtonWithTitle(power, "Питание")
            Spacer(Modifier.height(16.dp))
            RadioButtonWithTitle(emergencyPower, "ПА")
        }
        Spacer(Modifier.width(24.dp))
        SystemContols()
        Spacer(Modifier.width(24.dp))
        StartControls()
        Spacer(Modifier.width(24.dp))
        Column {
            Parameters()
            Spacer(Modifier.height(24.dp))
            EngineControls()
        }
    }
}

@Composable
fun SystemContols() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("Управление системами")
        Spacer(Modifier.height(32.dp))
        Button(onClick = { Controller.currentScreen.value = Screen.OilSystem }) {
            Text("Масляная система")
        }
        Button(onClick = { Controller.currentScreen.value = Screen.CoolingSystem }) {
            Text("Система охлаждения")
        }
        Button(onClick = { Controller.currentScreen.value = Screen.AirSystem }) {
            Text("Система пускового воздуха")
        }
        Button(onClick = { Controller.currentScreen.value = Screen.FuelSystem }) {
            Text("Топливная система")
        }
    }
}

@Composable
fun StartControls() {
    val timerValue by Controller.startTime.collectAsState()
    val timeString = SimpleDateFormat("mm:ss").format(Date.from(Instant.ofEpochSecond(timerValue.toLong())))
    val oilSystemActive by Controller.oilSystemActive.collectAsState()
    val coolingSystemActive by Controller.coolingSystemActive.collectAsState()
    val airSystemActive by Controller.airSystemActive.collectAsState()
    val isCanStart = oilSystemActive && coolingSystemActive && airSystemActive
    val isStarted by Controller.isStarted.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("Управление пуском")
        Spacer(Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { Controller.toggleStart() }, enabled = isCanStart) {
                Text(if (isStarted) "Стоп ВПУ" else "Пуск ВПУ")
            }
            Text(timeString, modifier = Modifier.padding(start = 32.dp), fontSize = 20.sp)
        }
        RadioButtonWithTitle(isStarted, "ВПУ в работе")
        Column(
            modifier = Modifier
                .padding(top = 24.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text("Готовность систем")
            Spacer(Modifier.height(16.dp))
            RadioButtonWithTitle(oilSystemActive, "Масляная система", modifier = Modifier.padding(bottom = 8.dp))
            RadioButtonWithTitle(coolingSystemActive, "Система охлаждения", modifier = Modifier.padding(bottom = 8.dp))
            RadioButtonWithTitle(airSystemActive, "Система пускового воздуха", modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}

enum class SelectedParameter {
    RotationSpeed,
    WaterTemperature,
    WaterPressure,
    OilTemperature,
    OilPressure,
    FuelPressure
}

@Composable
fun Parameters() {
    val parameters by Controller.parameters.collectAsState()
    var currentParameter by remember { mutableStateOf(SelectedParameter.RotationSpeed) }
    val currentParameterValue = when(currentParameter) {
        SelectedParameter.RotationSpeed -> parameters.rotationSpeed
        SelectedParameter.WaterTemperature -> parameters.waterTemperature
        SelectedParameter.WaterPressure -> parameters.waterPressure
        SelectedParameter.OilTemperature -> parameters.oilTemperature
        SelectedParameter.OilPressure -> parameters.oilPressure
        SelectedParameter.FuelPressure -> parameters.fuelPressure
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("Параметры")
        Spacer(Modifier.height(32.dp))

        Row {
            Text(String.format("%.2f", currentParameterValue), modifier = Modifier.width(100.dp).align(Alignment.CenterVertically), fontSize = 20.sp)
            Column {
                RadioButtonWithTitle(currentParameter == SelectedParameter.RotationSpeed, "Обороты, об/мин") { currentParameter = SelectedParameter.RotationSpeed }
                RadioButtonWithTitle(currentParameter == SelectedParameter.WaterTemperature, "t воды, C") { currentParameter = SelectedParameter.WaterTemperature }
                RadioButtonWithTitle(currentParameter == SelectedParameter.WaterPressure, "P воды, кгс/см2") { currentParameter = SelectedParameter.WaterPressure }
                RadioButtonWithTitle(currentParameter == SelectedParameter.OilTemperature, "t масла, C") { currentParameter = SelectedParameter.OilTemperature }
                RadioButtonWithTitle(currentParameter == SelectedParameter.OilPressure, "P масла, кгс/см2") { currentParameter = SelectedParameter.OilPressure }
                RadioButtonWithTitle(currentParameter == SelectedParameter.FuelPressure, "P топрива, кгс/см2") { currentParameter = SelectedParameter.FuelPressure }
            }
        }
    }
}

@Composable
fun EngineControls() {
    val isCanStart by Controller.isCanStartEngine.collectAsState()
    val isStarted by Controller.isEngineStarted.collectAsState()

    Row(
        modifier = Modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { Controller.isEngineStarted.value = true }, enabled = isCanStart && !isStarted) {
            Text("Пуск")
        }
        RadioButton(selected = isStarted, onClick = null, modifier = Modifier.padding(horizontal = 24.dp))
        Button(onClick = { Controller.isEngineStarted.value = false }, enabled = isStarted) {
            Text("Стоп")
        }
    }
}

@Composable
fun RadioButtonWithTitle(
    isActive: Boolean,
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        RadioButton(isActive, onClick = onClick, interactionSource = MutableInteractionSource())
        val textModifier = if (onClick == null) Modifier.padding(start = 8.dp) else Modifier
        Text(title, modifier = textModifier)
    }
}