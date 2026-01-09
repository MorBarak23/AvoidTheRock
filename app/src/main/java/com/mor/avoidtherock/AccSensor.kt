package com.mor.avoidtherock

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

interface AccSensorCallBack {
    fun data(x: Float, y: Float, z: Float)
}

class AccSensorApi(context: Context, private val accSensorCallBack: AccSensorCallBack) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private lateinit var sensorEventListener: SensorEventListener

    init {
        initEventListener()
    }

    private fun initEventListener() {
        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not used in this game context
            }

            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                accSensorCallBack.data(x, y, z)
            }
        }
    }

    fun start() {
        if (sensor != null) {
            sensorManager.registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_GAME // Changed to GAME for smoother response
            )
        }
    }

    fun stop() {
        if (sensor != null) {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }
}