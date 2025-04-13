import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import screen.Screen

object Controller {

    val currentScreen = MutableStateFlow(Screen.Main)

    val power = MutableStateFlow(true)
    val emergencyPower = MutableStateFlow(false)

    val oilSystemActive = MutableStateFlow(false)
    val coolingSystemActive = MutableStateFlow(false)
    val airSystemActive = MutableStateFlow(false)
    val fuelSystemActive = MutableStateFlow(false)

    val parameters = MutableStateFlow(Parameters())

    const val DEFAULT_TIMER_VALUE = 15 * 60
    val isStarted = MutableStateFlow(false)
    val startTime = MutableStateFlow(DEFAULT_TIMER_VALUE)
    var timerJob: Job? = null

    val isCanStartEngine = MutableStateFlow(false)
    val isEngineStarted = MutableStateFlow(false)

    init {
        GlobalScope.launch {
            combine(
                oilSystemActive,
                coolingSystemActive,
                airSystemActive,
                fuelSystemActive,
                isEngineStarted
            ) { oilActive, coolingActive, airActive, fuelActive, engineStarted ->
                parameters.value.copy(
                    oilTemperature = if (oilActive) 10.0 else DEFAULT_OIL_TEMPERATURE,
                )
            }.collectLatest {
                parameters.value = it
            }
        }
    }

    fun isCanStart(): Boolean {
        return oilSystemActive.value
                && coolingSystemActive.value
                && airSystemActive.value
    }

    fun toggleStart() {
        if (isStarted.value) {
            isStarted.value = false
            timerJob?.cancel()
            startTime.value = DEFAULT_TIMER_VALUE
        } else {
            isStarted.value = true
            startTime.value = DEFAULT_TIMER_VALUE
            timerJob = GlobalScope.launch {
                while (startTime.value > 0) {
                    delay(1000)
                    if (!isCanStart()) {
                        break
                    }
                    startTime.value -= 1
                }
                if (!isCanStart()) {
                    startTime.value = DEFAULT_TIMER_VALUE
                } else {
                    isCanStartEngine.value = true
                }
                isStarted.value = false
            }
        }
    }


    const val DEFAULT_ROTATION_SPEED = 1.0
    const val DEFAULT_WATER_TEMPERATURE = 2.0
    const val DEFAULT_WATER_PRESSURE = 3.0
    const val DEFAULT_OIL_TEMPERATURE = 4.0
    const val DEFAULT_OIL_PRESSURE = 5.0
    const val DEFAULT_FUEL_PRESSURE = 6.0
    data class Parameters(
        val rotationSpeed: Double = DEFAULT_ROTATION_SPEED,
        val waterTemperature: Double = DEFAULT_WATER_TEMPERATURE,
        val waterPressure: Double = DEFAULT_WATER_PRESSURE,
        val oilTemperature: Double = DEFAULT_OIL_TEMPERATURE,
        val oilPressure: Double = DEFAULT_OIL_PRESSURE,
        val fuelPressure: Double = DEFAULT_FUEL_PRESSURE,
    )
}