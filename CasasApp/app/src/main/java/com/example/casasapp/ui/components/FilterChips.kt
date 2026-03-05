package com.example.casasapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.casasapp.ui.theme.AlquilerColor
import com.example.casasapp.ui.theme.VentaColor

/**
 * Opciones de filtro disponibles.
 */
enum class FiltroTipo(val label: String) {
    TODOS("Todos"),
    VENTA("Venta"),
    ALQUILER("Alquiler")
}

/**
 * Fila de chips para filtrar propiedades por tipo.
 * Diseño Clean & Bold con alto contraste.
 */
@Composable
fun FilterChips(
    filtroSeleccionado: String?,
    onFiltroChange: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            FilterChipItem(
                label = FiltroTipo.TODOS.label,
                selected = filtroSeleccionado == null,
                onClick = { onFiltroChange(null) },
                selectedColor = MaterialTheme.colorScheme.primary
            )
        }
        item {
            FilterChipItem(
                label = FiltroTipo.VENTA.label,
                selected = filtroSeleccionado == "Venta",
                onClick = { onFiltroChange("Venta") },
                selectedColor = VentaColor
            )
        }
        item {
            FilterChipItem(
                label = FiltroTipo.ALQUILER.label,
                selected = filtroSeleccionado == "Alquiler",
                onClick = { onFiltroChange("Alquiler") },
                selectedColor = AlquilerColor
            )
        }
    }
}

/**
 * Chip individual de filtro con estilo Clean & Bold.
 */
@Composable
private fun FilterChipItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedColor: androidx.compose.ui.graphics.Color
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = selectedColor.copy(alpha = 0.15f),
            selectedLabelColor = selectedColor
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = MaterialTheme.colorScheme.outline,
            selectedBorderColor = selectedColor,
            borderWidth = 1.dp,
            selectedBorderWidth = 2.dp,
            enabled = true,
            selected = selected
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Barra de búsqueda con estilos Clean & Bold.
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar por nombre o ubicación..."
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
