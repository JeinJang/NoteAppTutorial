package com.example.noteapptutorial.domain

sealed class DrawOperation {
    data class Draw(val stroke: StrokeData) : DrawOperation()
    data class Erase(val stroke: StrokeData) : DrawOperation()
    data class EraseAll(val strokes: List<StrokeData>) : DrawOperation()
}