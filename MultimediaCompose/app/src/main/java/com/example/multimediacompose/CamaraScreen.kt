package com.example.multimediacompose

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun CamaraScreen() {

    val context = LocalContext.current

    // 1. Estado para guardar la imagen (Bitmap) capturada
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 2. Actualizamos el launcher para recoger el resultado
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // La cámara devuelve un thumbnail en los extras con la clave "data"
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            bitmap = imageBitmap
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Cámara", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        Button(onClick = {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            // Es recomendable envolver el launch en un try-catch por seguridad
            try {
                launcher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show()
            }

        }) {
            Text("Abrir cámara")
        }

        Spacer(Modifier.height(20.dp))

        // 3. Si tenemos una imagen capturada, la mostramos
        bitmap?.let { btm ->
            Image(
                bitmap = btm.asImageBitmap(),
                contentDescription = "Imagen capturada",
                modifier = Modifier
                    .size(300.dp) // Puedes ajustar el tamaño aquí
                    .padding(16.dp)
            )
        }
    }
}