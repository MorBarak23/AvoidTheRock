package com.mor.avoidtherock

class Car (
    private var currentCol: Int
){

    fun getCurrentCol(): Int {return currentCol}

    fun setCurrentCol(col : Int) {
        currentCol = col
    }
    fun moveRight(size: Int) {
        if(currentCol < size -1) {
            currentCol++
        }
    }

    fun moveLeft() {
        if(currentCol > 0) {
            currentCol--
        }
    }


}

