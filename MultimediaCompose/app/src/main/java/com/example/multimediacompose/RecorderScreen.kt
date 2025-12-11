package com.example.multimediacompose

import android.Manifest
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.IOException

@Composable
fun RecorderScreen() {

    val context = LocalContext.current

    // Estado de permisos
    var permissionGranted by remember { mutableStateOf(false) }

    val requestPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (!granted) {
            Toast.makeText(context, "Permiso de micrófono denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Archivo de salida
    val fileName = remember {
        File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "recorded_audio.3gp").absolutePath
    }

    // Estados de grabación y reproducción
    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var mediaRecorder: MediaRecorder? by remember { mutableStateOf(null) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    // Función para iniciar grabación
    fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(fileName)
            try {
                prepare()
                start()
                isRecording = true
                Toast.makeText(context, "Grabando...", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Error al grabar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para detener grabación
    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        isRecording = false
        Toast.makeText(context, "Grabación terminada", Toast.LENGTH_SHORT).show()
    }

    // Función para reproducir audio
    fun startPlaying() {
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
                isPlaying = true
                setOnCompletionListener {
                    isPlaying = false
                    release()
                    mediaPlayer = null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Error al reproducir", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para detener reproducción
    fun stopPlaying() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        isPlaying = false
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Grabadora de audio", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        // Botón Grabar / Detener
        Button(onClick = {
            if (!isRecording) {
                if (!permissionGranted) {
                    requestPermission.launch(Manifest.permission.RECORD_AUDIO)
                } else {
                    startRecording()
                }
            } else {
                stopRecording()
            }
        }) {
            Text(if (isRecording) "Detener" else "Grabar")
        }

        Spacer(Modifier.height(10.dp))

        // Botón Reproducir / Detener reproducción
        Button(onClick = {
            if (!isPlaying) {
                startPlaying()
            } else {
                stopPlaying()
            }
        }) {
            Text(if (isPlaying) "Detener reproducción" else "Reproducir")
        }
    }
}
