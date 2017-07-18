package jp.a2kaido.sample

import android.app.Activity
import android.os.Bundle
import com.google.android.things.contrib.driver.bmx280.Bmx280
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay
import com.google.android.things.contrib.driver.ht16k33.Ht16k33
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat
import com.google.android.things.pio.Gpio


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {
    private val TAG = "HomeActivity"

    val ledRed: Gpio by lazy {
        RainbowHat.openLedRed()
    }

    val ledBlue: Gpio by lazy {
        RainbowHat.openLedBlue()
    }

    val ledGreen: Gpio by lazy {
        RainbowHat.openLedGreen()
    }

    val sensor: Bmx280 by lazy {
        RainbowHat.openSensor()
    }

    val segment: AlphanumericDisplay by lazy {
        RainbowHat.openDisplay()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ledRed.value = false
        ledBlue.value = false
        ledGreen.value = false

        val button = RainbowHat.openButtonA()
        button.setOnButtonEventListener { button, pressed ->

            if (pressed) {
                ledRed.value = true

                sensor.setTemperatureOversampling(Bmx280.OVERSAMPLING_1X)
                segment.setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX)
                segment.display(sensor.readTemperature().toDouble())
                segment.setEnabled(true)

            } else {
                ledRed.value = false
                segment.setEnabled(false)
            }
        }
    }
}
