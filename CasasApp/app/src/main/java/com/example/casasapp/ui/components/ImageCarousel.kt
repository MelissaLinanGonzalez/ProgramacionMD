package com.example.casasapp.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

/**
 * Carrusel de imágenes con indicadores y gestos de swipe.
 * Diseño moderno con navegación opcional.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(
    imagenes: List<String>,
    modifier: Modifier = Modifier,
    showNavigation: Boolean = true,
    onImageClick: ((Int) -> Unit)? = null
) {
    if (imagenes.isEmpty()) {
        // Estado vacío
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sin imágenes",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }
    
    val pagerState = rememberPagerState(pageCount = { imagenes.size })
    val scope = rememberCoroutineScope()
    
    Box(modifier = modifier.fillMaxWidth()) {
        // Pager horizontal
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            AsyncImage(
                model = imagenes[page],
                contentDescription = "Imagen ${page + 1} de ${imagenes.size}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .then(
                        if (onImageClick != null) {
                            Modifier.clickable { onImageClick(page) }
                        } else Modifier
                    ),
                contentScale = ContentScale.Crop
            )
        }
        
        // Flechas de navegación
        if (showNavigation && imagenes.size > 1) {
            // Flecha izquierda
            if (pagerState.currentPage > 0) {
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(8.dp)
                        .background(
                            Color.Black.copy(alpha = 0.4f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Anterior",
                        tint = Color.White
                    )
                }
            }
            
            // Flecha derecha
            if (pagerState.currentPage < imagenes.size - 1) {
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(8.dp)
                        .background(
                            Color.Black.copy(alpha = 0.4f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Siguiente",
                        tint = Color.White
                    )
                }
            }
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
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(
                        if (isSelected) 24.dp else 8.dp,
                        label = "indicatorWidth"
                    )
                    
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .height(8.dp)
                            .width(width)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else Color.White.copy(alpha = 0.5f)
                            )
                            .clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }
        }
        
        // Contador de imágenes
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.6f)
        ) {
            Text(
                text = "${pagerState.currentPage + 1}/${imagenes.size}",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
    }
}
