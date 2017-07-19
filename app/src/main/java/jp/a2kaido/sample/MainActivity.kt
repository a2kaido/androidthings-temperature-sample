package jp.a2kaido.sample

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.DynamicSensorCallback
import android.os.Bundle
import android.util.Log
import com.google.android.things.contrib.driver.bmx280.Bmx280SensorDriver
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
class MainActivity : Activity(), SensorEventListener {
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

    val sensorDriver: Bmx280SensorDriver by lazy {
        RainbowHat.createSensorDriver()
    }

    val segment: AlphanumericDisplay by lazy {
        RainbowHat.openDisplay()
    }

    val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ledRed.value = false
        ledBlue.value = false
        ledGreen.value = false

        sensorManager.registerDynamicSensorCallback(object : DynamicSensorCallback() {
            override fun onDynamicSensorConnected(sensor: Sensor) {
                if (sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                    Log.i(TAG, "Temperature sensor connected")
                    sensorManager.registerListener(this@MainActivity,
                            sensor, SensorManager.SENSOR_DELAY_NORMAL)
                }
            }
        })
        sensorDriver.registerTemperatureSensor()

        val button = RainbowHat.openButtonA()
        button.setOnButtonEventListener { button, pressed ->

            if (pressed) {
                ledRed.value = true

                segment.setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX)
                segment.display("HOGE")
                segment.setEnabled(true)

            } else {
                ledRed.value = false
                segment.setEnabled(false)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        segment.close()
        sensorDriver.close()
        ledRed.close()
        ledGreen.close()
        ledBlue.close()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.getOrNull(0)?.let {
            segment.display(it.toDouble())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.i(TAG, "sensor accuracy changed: " + accuracy);
    }
}
