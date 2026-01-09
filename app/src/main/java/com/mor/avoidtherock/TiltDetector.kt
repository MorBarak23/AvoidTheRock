package com.mor.avoidtherock

import android.content.Context

class TiltDetector(context: Context, private val gameManager: GameManager) {

    private val sensorApi: AccSensorApi

    init {
        sensorApi = AccSensorApi(context, object : AccSensorCallBack {
            override fun data(x: Float, y: Float, z: Float) {
                calculateMove(x)
            }
        })
    }

    private fun calculateMove(x: Float) {
        val targetCol: Int = when {
            x > 4.0 -> 0
            x < -4.0 -> 4

            x > 1.5 -> 1
            x < -1.5 -> 3

            else -> 2
        }

        gameManager.car.setCurrentCol(targetCol)
    }

    fun start() {
        sensorApi.start()
    }

    fun stop() {
        sensorApi.stop()
    }
}