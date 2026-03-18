package com.example.casasapp.ui.pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.casasapp.data.Casa
import com.example.casasapp.ui.components.FilterChips
import com.example.casasapp.ui.components.SearchBar
import com.example.casasapp.ui.theme.AlquilerColor
import com.example.casasapp.ui.theme.VentaColor
import com.example.casasapp.viewmodel.CasaViewModel
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

/**
 * Pantalla de Galería - Diseño Clean & Bold
 * Listado de propiedades con tarjetas anchas y alto contraste.
 *
 * Criterio F: Cada [PropertyCard] se envuelve en [AnimatedVisibility]
 * con efecto combinado de [fadeIn] y [slideInVertically] con delay escalonado
 * para crear un efecto de cascada al cargar la lista.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGaleria(navController: NavController) {
    val viewModel: CasaViewModel = viewModel()
    val casas by viewModel.casas.collectAsStateWithLifecycle()
    val filtroTipo by viewModel.filtroTipo.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()
    
    // Estado para controlar la animación escalonada de las tarjetas
    var itemsVisibles by remember { mutableStateOf(false) }
    
    LaunchedEffect(casas) {
        // Reset y relanzar la animación cuando cambian los datos
        itemsVisibles = false
        delay(100)
        itemsVisibles = true
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Propiedades",
                        fontWeight = FontWeight.Bold,
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("formulario") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publicar", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de búsqueda
            SearchBar(
                query = busqueda,
                onQueryChange = { viewModel.setBusqueda(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Chips de filtro
            FilterChips(
                filtroSeleccionado = filtroTipo,
                onFiltroChange = { viewModel.setFiltroTipo(it) },
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Contador de resultados
            Text(
                text = "${casas.size} ${if (casas.size == 1) "propiedad encontrada" else "propiedades encontradas"}",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Resultados
            if (casas.isEmpty()) {
                EmptyState(filtroActivo = filtroTipo != null || busqueda.isNotBlank())
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(casas, key = { _, casa -> casa.id }) { index, casa ->
                        /**
                         * AnimatedVisibility con fadeIn + slideInVertically (Criterio F).
                         *
                         * Cada tarjeta de propiedad se envuelve en [AnimatedVisibility]
                         * para una entrada animada combinando:
                         * - [fadeIn]: Transición de opacidad progresiva (400ms).
                         * - [slideInVertically]: Deslizamiento desde abajo con delay
                         *   escalonado (index * 80ms) para efecto cascada.
                         *
                         * Esto mejora la experiencia del usuario al presentar
                         * los resultados de búsqueda de forma dinámica y atractiva.
                         */
                        AnimatedVisibility(
                            visible = itemsVisibles,
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = index * 80
                                )
                            ) + slideInVertically(
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = index * 80
                                ),
                                initialOffsetY = { it / 3 }
                            )
                        ) {
                            PropertyCard(
                                casa = casa,
                                onClick = { navController.navigate("detalle/${casa.id}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta de propiedad grande - Diseño Clean & Bold
 */
@Composable
private fun PropertyCard(casa: Casa, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Imagen grande
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = casa.imagenes.firstOrNull() ?: "",
                    contentDescription = casa.nombre,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Badge de tipo (Venta/Alquiler)
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(8.dp),
                    color = if (casa.tipo == "Venta") VentaColor else AlquilerColor
                ) {
                    Text(
                        text = casa.tipo,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                // Botón favorito
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ) {
                    IconButton(
                        onClick = { /* TODO: Toggle favorito */ },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            
            // Información
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Precio - Grande y azul
                Text(
                    text = formatPrecio(casa.precio, casa.tipo),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Título - Negro y negrita
                Text(
                    text = casa.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Ubicación con icono
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = casa.ubicacion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Descripción breve
                if (casa.descripcion.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = casa.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Estado vacío mejorado
 */
@Composable
private fun EmptyState(filtroActivo: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(60.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (filtroActivo) "Sin resultados" else "¡Aún no hay propiedades!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (filtroActivo) 
                "Prueba a cambiar los filtros o buscar otra cosa" 
            else 
                "Sé el primero en publicar una propiedad y alcanza a miles de compradores",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
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