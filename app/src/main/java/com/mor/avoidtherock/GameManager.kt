package com.mor.avoidtherock
import kotlin.random.Random

class GameManager(val rows: Int, val cols: Int) {

    val car: Car = Car(cols/2)
    val matrix: Array<IntArray> = Array(rows) { IntArray(cols) }
    var lifeCounter: Int = 3
    var tickCounter: Int = 0
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

        for (i in rows - 1 downTo 1) {
            for (j in 0 until cols) {
                matrix[i][j] = matrix[i - 1][j] // promote the rock
            }
        }

        for (j in 0 until cols) {
            matrix[0][j] = 0 // clean the first row
        }

        val randomRockCol = Random.nextInt(cols)
        matrix[0][randomRockCol] = 1
        if(tickCounter % 5 == 0) {
            val randomCoinCol = Random.nextInt(cols)
            matrix[0][randomCoinCol] = 2
        }

        score++
        tickCounter++

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

        if (matrix[carRow][carCol] == 2) { // check's if there is a coin in the car location
            score += 5
        }
    }

    fun reset() {
        lifeCounter = 3
        score = 0
        tickCounter = 0
        isGameOver = false

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                matrix[i][j] = 0
            }
        }

    }


}
