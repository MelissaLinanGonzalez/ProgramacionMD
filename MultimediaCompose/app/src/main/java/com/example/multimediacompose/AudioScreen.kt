package com.example.multimediacompose

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun AudioScreen(){
    val context = LocalContext.current

    // 1. Variable de estado para el volumen (valor inicial 0.5f, es decir, 50%)
    var volume by remember { mutableFloatStateOf(0.5f) }

    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.audio).apply {
            setVolume(volume, volume) // Aplicar volumen inicial
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Reproductor de Audio", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

        // Controles de reproducción
        Button(onClick = { mediaPlayer.start() }) { Text("PLAY") }
        Button(onClick = { mediaPlayer.pause() }) { Text("PAUSE") }
        Button(onClick = {
            mediaPlayer.stop()
            mediaPlayer.prepare() // Necesario preparar de nuevo tras un stop() para poder volver a reproducir
        }) { Text("STOP") }

        Spacer(Modifier.height(30.dp))

        // 2. Control de Volumen (Slider)
        Text(text = "Volumen: ${(volume * 100).toInt()}%")

        Slider(
            value = volume,
            onValueChange = { newVolume ->
                volume = newVolume
                mediaPlayer.setVolume(volume, volume) // Actualiza el volumen del MediaPlayer
            },
            valueRange = 0f..1f, // El volumen en Android va de 0.0 a 1.0
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}