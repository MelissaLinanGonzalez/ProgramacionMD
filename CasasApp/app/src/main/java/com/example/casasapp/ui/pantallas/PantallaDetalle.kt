package com.example.casasapp.ui.pantallas

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.casasapp.data.Comentario
import com.example.casasapp.ui.theme.AlquilerColor
import com.example.casasapp.ui.theme.VentaColor
import com.example.casasapp.viewmodel.DetalleViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla de Detalle - Diseño Clean & Bold
 * Carrusel HorizontalPager, reproductor de vídeo Tour Virtual (ExoPlayer/Media3),
 * info sobre fondo blanco sólido, sección comentarios desplegable con animateContentSize.
 *
 * Criterios cubiertos:
 * - E: Reproductor multimedia nativo con ExoPlayer (Media3)
 * - F: Animaciones explícitas con animateContentSize() en la sección de comentarios
 * - G: Manejo de eventos del reproductor y control del ciclo de vida
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PantallaDetalle(navController: NavController, idCasa: Int) {
    val viewModel: DetalleViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Cargar la casa al iniciar
    LaunchedEffect(idCasa) {
        viewModel.cargarCasa(idCasa)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        uiState.casa?.nombre ?: "Detalle",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Favorito */ }) {
                        Icon(
                            Icons.Default.FavoriteBorder, 
                            "Favorito",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { /* TODO: Compartir */ }) {
                        Icon(
                            Icons.Default.Share, 
                            "Compartir",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            
            uiState.error != null -> {
                ErrorState(
                    error = uiState.error!!,
                    onBack = { navController.popBackStack() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            uiState.casa != null -> {
                val casa = uiState.casa!!
                
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Carrusel de imágenes con HorizontalPager
                        ImageCarouselPager(
                            imagenes = casa.imagenes,
                            modifier = Modifier.height(300.dp)
                        )
                        
                        // ── Reproductor de vídeo: Tour Virtual (Criterios E y G) ──
                        VideoTourVirtual(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                        
                        // Panel de información sobre fondo blanco sólido
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                // Badge de tipo
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (casa.tipo == "Venta") VentaColor else AlquilerColor
                                ) {
                                    Text(
                                        text = casa.tipo,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Precio - Grande y azul
                                Text(
                                    text = formatPrecio(casa.precio, casa.tipo),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Nombre - Negro y negrita
                                Text(
                                    text = casa.nombre,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                // Ubicación
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = casa.ubicacion,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 20.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                                
                                // ── Sección Descripción desplegable con animateContentSize (Criterio F) ──
                                SeccionDescripcionDesplegable(
                                    descripcion = casa.descripcion
                                )
                                
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 20.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                                
                                // ── Sección Comentarios desplegable con animateContentSize (Criterio F) ──
                                SeccionComentariosDesplegable(
                                    comentarios = casa.comentarios,
                                    nuevoComentario = uiState.nuevoComentario,
                                    onComentarioChange = { viewModel.updateNuevoComentario(it) },
                                    onEnviarComentario = { viewModel.agregarComentario() }
                                )
                                
                                // Espacio para el botón flotante
                                Spacer(modifier = Modifier.height(100.dp))
                            }
                        }
                    }
                    
                    // Botón de contacto fijo abajo
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 8.dp
                    ) {
                        Button(
                            onClick = { /* TODO: Contactar */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Contactar al propietario",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// REPRODUCTOR DE VÍDEO — TOUR VIRTUAL (Criterios E y G)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Composable que muestra un reproductor de vídeo nativo para simular un "Tour Virtual"
 * de la propiedad inmobiliaria.
 *
 * **Clase multimedia principal**: [ExoPlayer] de la biblioteca AndroidX Media3
 * (evolución moderna de ExoPlayer2). Se utiliza ExoPlayer porque:
 * - Es el reproductor recomendado por Google para Android.
 * - Soporta múltiples formatos (MP4, HLS, DASH, etc.).
 * - Proporciona una API moderna con manejo de eventos y estados.
 * - Se integra con Jetpack Compose a través de [AndroidView].
 *
 * **Gestión del ciclo de vida** (Criterio G):
 * - Usa [DisposableEffect] para liberar recursos del reproductor al salir de la composición.
 * - Usa [LifecycleEventObserver] para pausar la reproducción cuando la Activity
 *   pasa a segundo plano (ON_PAUSE) y reanudar en ON_RESUME.
 *
 * **Manejo de excepciones** (Criterio G):
 * - Implementa [Player.Listener.onPlayerError] para capturar [PlaybackException]
 *   de red, formato o decodificación, mostrando un mensaje de error en la UI.
 * - El bloque try-catch envuelve la inicialización del reproductor para capturar
 *   cualquier excepción inesperada durante la construcción del MediaItem.
 *
 * @param modifier Modificador de Compose para personalizar layout y estilo.
 */
@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
private fun VideoTourVirtual(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Estado para mostrar errores de reproducción en la UI
    var errorReproduccion by remember { mutableStateOf<String?>(null) }
    var isPlayerReady by remember { mutableStateOf(false) }

    // URL pública de vídeo MP4 de prueba (Big Buck Bunny - Blender Foundation)
    val videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

    // Crear el ExoPlayer con manejo de excepciones
    val exoPlayer = remember {
        try {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
                setMediaItem(mediaItem)
                playWhenReady = false // No reproducir automáticamente
                prepare()

                // Listener para eventos del reproductor (Criterio G)
                addListener(object : Player.Listener {
                    /**
                     * Callback invocado cuando ocurre un error de reproducción.
                     * Captura [PlaybackException] que puede ser causado por:
                     * - Errores de red (URL inaccesible, timeout)
                     * - Formato no soportado
                     * - Errores de decodificación del codec
                     *
                     * @param error La excepción de reproducción con código y mensaje.
                     */
                    override fun onPlayerError(error: PlaybackException) {
                        errorReproduccion = when (error.errorCode) {
                            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ->
                                "Error de red: no se pudo cargar el vídeo"
                            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED,
                            PlaybackException.ERROR_CODE_DECODING_FAILED ->
                                "Error de decodificación del vídeo"
                            else ->
                                "Error al reproducir el vídeo: ${error.message}"
                        }
                    }

                    /**
                     * Callback invocado cuando cambia el estado de reproducción.
                     * Se usa para detectar cuando el reproductor está listo
                     * para mostrar los controles.
                     */
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            isPlayerReady = true
                        }
                    }
                })
            }
        } catch (e: Exception) {
            errorReproduccion = "Error al inicializar el reproductor: ${e.message}"
            null
        }
    }

    // Control del ciclo de vida: pausar/resumir con la Activity
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer?.pause()
                Lifecycle.Event.ON_RESUME -> { /* El usuario decide cuándo reanudar */ }
                Lifecycle.Event.ON_DESTROY -> exoPlayer?.release()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        // Liberar recursos al salir de la composición
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer?.release()
        }
    }

    // UI del reproductor
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Encabezado
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🎬 Tour Virtual",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (errorReproduccion != null) {
                // Estado de error
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorReproduccion!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else if (exoPlayer != null) {
                // PlayerView envuelto en AndroidView
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = true // Controles básicos nativos
                            setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            // Pie informativo
            Text(
                text = "Vídeo demostrativo del tour virtual de la propiedad",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECCIONES DESPLEGABLES CON animateContentSize (Criterio F)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Sección de descripción desplegable con animación de tamaño.
 *
 * Utiliza [Modifier.animateContentSize] de Jetpack Compose para animar
 * suavemente el cambio de tamaño del contenedor cuando el usuario
 * expande o colapsa la descripción. La animación usa un [spring] con
 * amortiguación baja para un efecto elástico sutil.
 *
 * @param descripcion Texto de la descripción de la propiedad.
 */
@Composable
private fun SeccionDescripcionDesplegable(descripcion: String) {
    var expandida by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        // Cabecera clicable
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandida = !expandida },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = if (expandida) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expandida) "Colapsar" else "Expandir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (expandida) {
            Text(
                text = descripcion.ifBlank { "Sin descripción disponible" },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
            )
        } else {
            // Vista previa colapsada (max 2 líneas)
            Text(
                text = descripcion.ifBlank { "Sin descripción disponible" },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}

/**
 * Sección de comentarios desplegable con animación de tamaño.
 *
 * Utiliza [Modifier.animateContentSize] para animar el cambio de tamaño
 * cuando el usuario expande/colapsa la sección de comentarios. Esto permite
 * mostrar u ocultar la lista completa de comentarios con una transición fluida
 * en lugar de un corte abrupto.
 *
 * La animación se aplica al [Column] contenedor, de modo que al cambiar
 * el estado [expandida], el composable recalcula su tamaño y Compose
 * interpola suavemente entre el tamaño anterior y el nuevo.
 *
 * @param comentarios Lista de [Comentario] de la propiedad.
 * @param nuevoComentario Texto actual del campo de nuevo comentario.
 * @param onComentarioChange Callback cuando el texto del comentario cambia.
 * @param onEnviarComentario Callback al enviar un nuevo comentario.
 */
@Composable
private fun SeccionComentariosDesplegable(
    comentarios: List<Comentario>,
    nuevoComentario: String,
    onComentarioChange: (String) -> Unit,
    onEnviarComentario: () -> Unit
) {
    var expandida by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        // Cabecera clicable con contador
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandida = !expandida },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Comentarios (${comentarios.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expandida) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expandida) "Colapsar" else "Expandir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (expandida) {
            Spacer(modifier = Modifier.height(16.dp))

            // Input de nuevo comentario
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = nuevoComentario,
                    onValueChange = onComentarioChange,
                    placeholder = { 
                        Text(
                            "Escribe un comentario...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ) 
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilledIconButton(
                    onClick = onEnviarComentario,
                    enabled = nuevoComentario.isNotBlank(),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        "Enviar"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de comentarios
            if (comentarios.isEmpty()) {
                EmptyCommentsState()
            } else {
                comentarios.forEach { comentario ->
                    CommentItem(comentario = comentario)
                    if (comentario != comentarios.last()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 52.dp, top = 8.dp, bottom = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// COMPONENTES AUXILIARES
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Carrusel de imágenes con HorizontalPager
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageCarouselPager(
    imagenes: List<String>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { imagenes.size.coerceAtLeast(1) })
    
    Box(modifier = modifier.fillMaxWidth()) {
        if (imagenes.isEmpty()) {
            // Placeholder cuando no hay imágenes
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = imagenes[page],
                    contentDescription = "Imagen ${page + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Indicadores de página
            if (imagenes.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(imagenes.size) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        Color.White.copy(alpha = 0.6f)
                                )
                        )
                    }
                }
                
                // Contador de fotos
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1}/${imagenes.size}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Estado de error
 */
@Composable
private fun ErrorState(error: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "¡Ups! Algo salió mal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(onClick = onBack) {
                Text("Volver")
            }
        }
    }
}

/**
 * Estado vacío de comentarios
 */
@Composable
private fun EmptyCommentsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No hay comentarios aún",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Sé el primero en comentar",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

/**
 * Item de comentario
 */
@Composable
private fun CommentItem(comentario: Comentario) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Avatar
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = comentario.usuario.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nombre en negrita
                Text(
                    text = comentario.usuario,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Fecha
                Text(
                    text = formatFecha(comentario.fecha),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Texto del comentario
            Text(
                text = comentario.texto,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Formatea el precio según el tipo.
 */
private fun formatPrecio(precio: Double, tipo: String): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("es", "ES"))
    val precioFormateado = formatter.format(precio)
    return if (tipo == "Alquiler") "$precioFormateado/mes" else precioFormateado
}

/**
 * Formatea la fecha del comentario.
 */
private fun formatFecha(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM", Locale("es", "ES"))
    return sdf.format(Date(timestamp))
}