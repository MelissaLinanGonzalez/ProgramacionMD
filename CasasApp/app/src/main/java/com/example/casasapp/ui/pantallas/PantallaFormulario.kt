package com.example.casasapp.ui.pantallas

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.casasapp.ui.theme.AlquilerColor
import com.example.casasapp.ui.theme.VentaColor
import com.example.casasapp.util.ImageProcessor
import com.example.casasapp.viewmodel.FormularioViewModel

/**
 * Pantalla de formulario con diseño profesional.
 * Multi-imagen, captura directa con cámara, validación visual y todos los campos.
 *
 * Criterios cubiertos:
 * - B: Captura de foto directa usando [ActivityResultContracts.TakePicturePreview]
 * - C: Conversión de formato mediante [ImageProcessor.comprimirAJpeg]
 * - D: Transformación geométrica mediante [ImageProcessor.redimensionarBitmap]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormulario(navController: NavController) {
    val viewModel: FormularioViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Efecto para navegación después de guardar
    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            navController.popBackStack()
        }
    }
    
    // Launcher para seleccionar múltiples imágenes desde la galería
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            viewModel.agregarImagen(uri.toString())
        }
    }
    
    /**
     * Launcher para capturar una foto directa con la cámara del dispositivo.
     *
     * Utiliza [ActivityResultContracts.TakePicturePreview] que devuelve un [Bitmap]
     * en miniatura capturado por la cámara del sistema. Este contrato:
     * - Abre la aplicación de cámara nativa del dispositivo.
     * - Devuelve un [Bitmap] con la foto capturada (resolución de preview).
     * - No requiere crear un archivo temporal previo (a diferencia de TakePicture).
     *
     * Al recibir el bitmap:
     * 1. Se redimensiona a max 800x800px con [ImageProcessor.redimensionarBitmap]
     *    (transformación geométrica — Criterio D).
     * 2. Se comprime a JPEG 80% con [ImageProcessor.comprimirAJpeg]
     *    (conversión de formato — Criterio C).
     * 3. Se guarda como archivo con [ImageProcessor.guardarBitmapComoArchivo].
     * 4. La URI del archivo se agrega al ViewModel para mostrar la imagen en la lista.
     */
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            // Procesamiento de imagen (Criterios B, C y D)
            val archivo = ImageProcessor.guardarBitmapComoArchivo(context, it)
            archivo?.let { file ->
                viewModel.agregarImagen(Uri.fromFile(file).toString())
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Propiedad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección de imágenes
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Create,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Fotos de la propiedad",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Vista previa de imágenes
                    if (uiState.imagenes.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(uiState.imagenes) { index, uri ->
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = "Imagen ${index + 1}",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    // Botón eliminar
                                    IconButton(
                                        onClick = { viewModel.eliminarImagen(index) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(28.dp)
                                            .background(
                                                Color.Black.copy(alpha = 0.5f),
                                                RoundedCornerShape(14.dp)
                                            )
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            "Eliminar",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            
                            // Botón añadir más desde galería
                            item {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                        .clickable { imageLauncher.launch("image/*") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        "Añadir foto",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Estado sin imágenes
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (uiState.errorImagenes) 
                                        MaterialTheme.colorScheme.errorContainer 
                                    else 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                )
                                .clickable { imageLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    "Añadir fotos",
                                    tint = if (uiState.errorImagenes) 
                                        MaterialTheme.colorScheme.error 
                                    else 
                                        MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Toca para añadir fotos de la galería",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (uiState.errorImagenes) 
                                        MaterialTheme.colorScheme.error 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    if (uiState.errorImagenes) {
                        Text(
                            "Añade al menos una foto",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // ── Botón para capturar foto con la cámara (Criterio B) ──
                    BotonCapturarFoto(
                        onClick = { cameraLauncher.launch(null) }
                    )
                }
            }
            
            // Tipo de propiedad (Venta/Alquiler)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Tipo de operación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TipoChip(
                            label = "Venta",
                            selected = uiState.tipo == "Venta",
                            color = VentaColor,
                            onClick = { viewModel.updateTipo("Venta") },
                            modifier = Modifier.weight(1f)
                        )
                        TipoChip(
                            label = "Alquiler",
                            selected = uiState.tipo == "Alquiler",
                            color = AlquilerColor,
                            onClick = { viewModel.updateTipo("Alquiler") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Campos de texto
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = { viewModel.updateNombre(it) },
                label = { Text("Título de la propiedad") },
                placeholder = { Text("Ej: Piso luminoso en el centro") },
                isError = uiState.errorNombre,
                supportingText = if (uiState.errorNombre) {
                    { Text("El título es obligatorio") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            OutlinedTextField(
                value = uiState.precio,
                onValueChange = { viewModel.updatePrecio(it) },
                label = { Text("Precio (€)") },
                placeholder = { Text("Ej: 150000") },
                isError = uiState.errorPrecio,
                supportingText = if (uiState.errorPrecio) {
                    { Text("Introduce un precio válido") }
                } else null,
                leadingIcon = {
                    Icon(Icons.Default.Star, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            OutlinedTextField(
                value = uiState.ubicacion,
                onValueChange = { viewModel.updateUbicacion(it) },
                label = { Text("Ubicación") },
                placeholder = { Text("Ej: Madrid, Centro") },
                isError = uiState.errorUbicacion,
                supportingText = if (uiState.errorUbicacion) {
                    { Text("La ubicación es obligatoria") }
                } else null,
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            OutlinedTextField(
                value = uiState.descripcion,
                onValueChange = { viewModel.updateDescripcion(it) },
                label = { Text("Descripción") },
                placeholder = { Text("Describe la propiedad...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Botón guardar
            Button(
                onClick = { viewModel.validarYGuardar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Publicar Propiedad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Botón para capturar una foto directamente con la cámara del dispositivo.
 *
 * Utiliza un diseño de botón outlined con icono de cámara para diferenciarse
 * visualmente del selector de galería. Al pulsarlo, se lanza el contrato
 * [ActivityResultContracts.TakePicturePreview] que abre la cámara nativa.
 *
 * La foto capturada se procesará posteriormente con [ImageProcessor]:
 * - Redimensionado geométrico a 800x800px máximo (Criterio D)
 * - Compresión a formato JPEG al 80% de calidad (Criterio C)
 *
 * @param onClick Callback invocado al pulsar el botón, que lanza el launcher de cámara.
 */
@Composable
private fun BotonCapturarFoto(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            Icons.Default.AccountBox,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "📷 Tomar foto con cámara",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Chip de selección de tipo.
 */
@Composable
private fun TipoChip(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = if (selected) color else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = ButtonDefaults.outlinedButtonBorder.takeIf { !selected }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}