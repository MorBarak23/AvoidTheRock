package com.mor.avoidtherock
import kotlin.random.Random

class GameManager(val rows: Int, val cols: Int) {

    val car: Car = Car(cols/2)
    val matrix: Array<IntArray> = Array(rows) { IntArray(cols) }
    var lifeCounter: Int = 3
    private var tickCount = 0 // counter for when to bring a rock
    var wasCrash: Boolean = false
    var score: Int = 0;
    var isGameOver: Boolean = false
        private set // everyone can get, only gameManager can set.

    fun moveCarRight() {
        car.moveRight(cols)
    }

    fun moveCarLeft() {
        car.moveLeft()
    }

    fun reduceLife() {
        lifeCounter--
        if (lifeCounter <= 0) {
            lifeCounter = 0
            isGameOver = true
        }
    }

    fun updateGame() {
        if (isGameOver) return

        tickCount++

        for (i in rows - 1 downTo 1) {
            for (j in 0 until cols) {
                matrix[i][j] = matrix[i - 1][j] // promote the rock
            }
        }

        for (j in 0 until cols) {
            matrix[0][j] = 0 // clean the first row
        }

        // bring a rock every two ticks
        if (tickCount % 2 == 0) {
            val randomCol = Random.nextInt(cols)
            matrix[0][randomCol] = 1
            score++
        }

        checkCollision()
    }

    // function that check if there is a crash
    private fun checkCollision() {
        val carRow = rows - 1
        val carCol = car.getCurrentCol()

        if (matrix[carRow][carCol] == 1) { // check's if there is a rock in the car location
            reduceLife()
            wasCrash = true
        }
    }


}
