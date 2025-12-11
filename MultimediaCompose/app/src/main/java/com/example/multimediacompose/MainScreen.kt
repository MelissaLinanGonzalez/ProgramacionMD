package com.example.multimediacompose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MenuScreen(navController: NavController){

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text("Menú principal", style = MaterialTheme.typography.headlineMedium)

        Button(onClick = { navController.navigate(NavRoutes.Audio.route)}) {
            Text("Reproductor de Audio")
        }

        Button(onClick = { navController.navigate(NavRoutes.Video.route)}) {
            Text("Reproductor de Video")
        }

        Button(onClick = { navController.navigate(NavRoutes.Camara.route)}) {
            Text("Cámara (Intent)")
        }

        Button(onClick = { navController.navigate(NavRoutes.Recorder.route)}) {
            Text("Grabadora (Intent)")
        }
    }
}
