package com.example.noteapptutorial.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.noteapptutorial.domain.DrawOperation
import com.example.noteapptutorial.domain.StrokeData
import com.example.noteapptutorial.utils.drawStroke
import com.example.noteapptutorial.utils.getDistance

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun NoteCanvas() {
    var isEraserMode by remember { mutableStateOf(false) }
    val paths = remember { mutableStateListOf<StrokeData>() }

    val undoStack = remember { mutableStateListOf<DrawOperation>() }
    val redoStack = remember { mutableStateListOf<DrawOperation>() }
    var currentPath by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if (paths.isNotEmpty()) {
                    undoStack.add(DrawOperation.EraseAll(paths.toList()))
                    paths.clear()
                    redoStack.clear()
                }
                currentPath = emptyList()
            }) {
                Text("Erase All")
            }

            Button(onClick = {
                isEraserMode = !isEraserMode
            }) {
                Text(if (isEraserMode) "Draw" else "Erase")
            }

            Button(onClick = {
                if (undoStack.isNotEmpty()) {
                    val last = undoStack.removeLast()
                    when (last) {
                        is DrawOperation.Draw -> {
                            paths.removeIf { it.points == last.stroke.points }
                        }
                        is DrawOperation.Erase -> {
                            paths.add(last.stroke)
                        }
                        is DrawOperation.EraseAll -> {
                            paths.addAll(last.strokes)
                        }
                    }
                    redoStack.add(last)
                }
            }) {
                Text("Undo")
            }

            Button(onClick = {
                if (redoStack.isNotEmpty()) {
                    val last = redoStack.removeLast()
                    when (last) {
                        is DrawOperation.Draw -> {
                            paths.add(last.stroke)
                        }
                        is DrawOperation.Erase -> {
                            paths.removeIf { it.points == last.stroke.points }
                        }
                        is DrawOperation.EraseAll -> {
                            paths.removeAll { s ->
                                last.strokes.any { it.points == s.points }
                            }
                        }
                    }
                    undoStack.add(last)
                }
            }) {
                Text("Redo")
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(isEraserMode) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            if (!isEraserMode) {
                                currentPath = listOf(offset)
                            }
                        },
                        onDrag = { change, _ ->
                            val position = change.position
                            if (isEraserMode) {
                                val toErase = paths.find { stroke ->
                                    stroke.points.any { it.getDistance(position) < 40f }
                                }
                                if (toErase != null) {
                                    paths.remove(toErase)
                                    undoStack.add(DrawOperation.Erase(toErase))
                                    redoStack.clear()
                                }
                            } else {
                                currentPath = currentPath + position
                            }
                        },
                        onDragEnd = {
                            if (!isEraserMode && currentPath.isNotEmpty()) {
                                val stroke = StrokeData(currentPath)
                                paths.add(stroke)
                                undoStack.add(DrawOperation.Draw(stroke))
                                currentPath = emptyList()
                                redoStack.clear()
                            }
                        }
                    )
                }
        ) {
            for (stroke in paths) {
                drawStroke(stroke.points)
            }
            drawStroke(currentPath)
        }
    }
}