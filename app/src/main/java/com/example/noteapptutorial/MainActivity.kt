package com.example.noteapptutorial

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.noteapptutorial.ui.NoteCanvas

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteCanvas()
        }
    }
}

@Preview
@Composable
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
fun NotePreview() {
    NoteCanvas()
}