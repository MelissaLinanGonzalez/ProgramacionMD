package com.example.casasapp.data

/**
 * Repositorio Mock con datos de prueba realistas.
 * Proporciona propiedades de ejemplo con múltiples imágenes y precios realistas.
 */
object RepositorioCasasMock {
    
    /**
     * Lista de casas de prueba con datos realistas
     */
    val casasMock: List<Casa> = listOf(
        Casa(
            id = 1,
            nombre = "Ático de lujo con terraza panorámica",
            descripcion = "Espectacular ático en el centro de la ciudad con vistas de 360°. Acabados de primera calidad, domótica integrada, aire acondicionado por zonas. Terraza de 80m² con jacuzzi privado. Garaje para 2 coches y trastero incluidos.",
            precio = 485000.0,
            ubicacion = "Paseo de la Castellana, Madrid",
            tipo = "Venta",
            imagenes = listOf(
                "https://picsum.photos/seed/atico1/800/600",
                "https://picsum.photos/seed/atico2/800/600",
                "https://picsum.photos/seed/atico3/800/600",
                "https://picsum.photos/seed/atico4/800/600"
            ),
            comentarios = listOf(
                Comentario(
                    usuario = "María García",
                    texto = "Impresionante propiedad, las vistas son espectaculares. ¿Está negociable el precio?",
                    fecha = System.currentTimeMillis() - 86400000 * 2
                ),
                Comentario(
                    usuario = "Carlos López",
                    texto = "Me interesa mucho, ¿cuándo podría hacer una visita?",
                    fecha = System.currentTimeMillis() - 86400000
                )
            ),
            propietarioId = "user_001"
        ),
        Casa(
            id = 2,
            nombre = "Chalet independiente con piscina",
            descripcion = "Magnífico chalet de 350m² en parcela de 1.200m². 5 dormitorios, 4 baños, salón de 60m² con chimenea. Piscina climatizada, jardín con riego automático. Zona muy tranquila con excelentes comunicaciones.",
            precio = 650000.0,
            ubicacion = "Pozuelo de Alarcón, Madrid",
            tipo = "Venta",
            imagenes = listOf(
                "https://picsum.photos/seed/chalet1/800/600",
                "https://picsum.photos/seed/chalet2/800/600",
                "https://picsum.photos/seed/chalet3/800/600",
                "https://picsum.photos/seed/chalet4/800/600",
                "https://picsum.photos/seed/chalet5/800/600"
            ),
            comentarios = listOf(
                Comentario(
                    usuario = "Ana Martínez",
                    texto = "La piscina es genial para el verano. ¿Incluye los muebles?",
                    fecha = System.currentTimeMillis() - 86400000 * 5
                )
            ),
            propietarioId = "user_002"
        ),
        Casa(
            id = 3,
            nombre = "Piso reformado en el centro",
            descripcion = "Bonito piso totalmente reformado en finca clásica rehabilitada. 3 dormitorios, 2 baños, cocina americana equipada. Suelos de parquet, techos con molduras originales. Muy luminoso, orientación sur.",
            precio = 1850.0,
            ubicacion = "Barrio de Salamanca, Madrid",
            tipo = "Alquiler",
            imagenes = listOf(
                "https://picsum.photos/seed/piso1/800/600",
                "https://picsum.photos/seed/piso2/800/600",
                "https://picsum.photos/seed/piso3/800/600"
            ),
            comentarios = emptyList(),
            propietarioId = "user_003"
        ),
        Casa(
            id = 4,
            nombre = "Loft industrial en zona trendy",
            descripcion = "Espectacular loft de 120m² en antigua fábrica rehabilitada. Techos de 5 metros, vigas vistas, grandes ventanales. Espacio diáfano con posibilidad de personalizar. Incluye plaza de garaje.",
            precio = 295000.0,
            ubicacion = "Poblenou, Barcelona",
            tipo = "Venta",
            imagenes = listOf(
                "https://picsum.photos/seed/loft1/800/600",
                "https://picsum.photos/seed/loft2/800/600",
                "https://picsum.photos/seed/loft3/800/600",
                "https://picsum.photos/seed/loft4/800/600"
            ),
            comentarios = listOf(
                Comentario(
                    usuario = "Luis Fernández",
                    texto = "El estilo industrial es increíble. ¿Tiene certificado energético?",
                    fecha = System.currentTimeMillis() - 86400000 * 3
                ),
                Comentario(
                    usuario = "Sara Jiménez",
                    texto = "Perfecto para un estudio de fotografía, ¡me encanta!",
                    fecha = System.currentTimeMillis() - 86400000 * 2
                )
            ),
            propietarioId = "user_001"
        ),
        Casa(
            id = 5,
            nombre = "Apartamento con vistas al mar",
            descripcion = "Precioso apartamento en primera línea de playa. 2 dormitorios, 1 baño, terraza con vistas al mar. Piscina comunitaria y acceso directo a la playa. Ideal para vacaciones o inversión turística.",
            precio = 1200.0,
            ubicacion = "Marbella, Málaga",
            tipo = "Alquiler",
            imagenes = listOf(
                "https://picsum.photos/seed/playa1/800/600",
                "https://picsum.photos/seed/playa2/800/600",
                "https://picsum.photos/seed/playa3/800/600"
            ),
            comentarios = listOf(
                Comentario(
                    usuario = "Pedro Ruiz",
                    texto = "¿Está disponible para agosto? Somos una familia de 4.",
                    fecha = System.currentTimeMillis() - 86400000
                )
            ),
            propietarioId = "user_004"
        ),
        Casa(
            id = 6,
            nombre = "Casa rural con encanto",
            descripcion = "Preciosa casa rural restaurada del siglo XVIII. 4 dormitorios, 3 baños, cocina rústica con horno de leña. Jardín de 2.000m² con árboles frutales. Perfecta para escapadas o residencia permanente.",
            precio = 189000.0,
            ubicacion = "Sierra de Gredos, Ávila",
            tipo = "Venta",
            imagenes = listOf(
                "https://picsum.photos/seed/rural1/800/600",
                "https://picsum.photos/seed/rural2/800/600",
                "https://picsum.photos/seed/rural3/800/600",
                "https://picsum.photos/seed/rural4/800/600"
            ),
            comentarios = emptyList(),
            propietarioId = "user_005"
        ),
        Casa(
            id = 7,
            nombre = "Estudio moderno bien comunicado",
            descripcion = "Estudio de 45m² completamente equipado. Cocina americana con electrodomésticos nuevos, baño con ducha. Ideal para profesionales o parejas. A 5 minutos del metro.",
            precio = 750.0,
            ubicacion = "Chamberí, Madrid",
            tipo = "Alquiler",
            imagenes = listOf(
                "https://picsum.photos/seed/estudio1/800/600",
                "https://picsum.photos/seed/estudio2/800/600"
            ),
            comentarios = listOf(
                Comentario(
                    usuario = "Elena Sánchez",
                    texto = "¿Se admiten mascotas pequeñas?",
                    fecha = System.currentTimeMillis() - 86400000 * 4
                )
            ),
            propietarioId = "user_003"
        ),
        Casa(
            id = 8,
            nombre = "Dúplex con jardín privado",
            descripcion = "Fantástico dúplex de 180m² con jardín privado de 50m². Salón-comedor muy amplio, 4 dormitorios, 3 baños. Cocina totalmente equipada. Urbanización con piscina y pádel.",
            precio = 375000.0,
            ubicacion = "Las Rozas, Madrid",
            tipo = "Venta",
            imagenes = listOf(
                "https://picsum.photos/seed/duplex1/800/600",
                "https://picsum.photos/seed/duplex2/800/600",
                "https://picsum.photos/seed/duplex3/800/600"
            ),
            comentarios = listOf(
                Comentario(
                    usuario = "Roberto Díaz",
                    texto = "El jardín privado es un plus enorme. ¿Cuántos años tiene la construcción?",
                    fecha = System.currentTimeMillis() - 86400000 * 6
                )
            ),
            propietarioId = "user_002"
        )
    )
    
    /**
     * Obtiene una casa por su ID
     */
    fun getCasaById(id: Int): Casa? {
        return casasMock.find { it.id == id }
    }
    
    /**
     * Filtra casas por tipo (Venta/Alquiler)
     */
    fun getCasasByTipo(tipo: String): List<Casa> {
        return casasMock.filter { it.tipo == tipo }
    }
    
    /**
     * Busca casas por nombre o ubicación
     */
    fun buscarCasas(query: String): List<Casa> {
        val queryLower = query.lowercase()
        return casasMock.filter {
            it.nombre.lowercase().contains(queryLower) ||
            it.ubicacion.lowercase().contains(queryLower) ||
            it.descripcion.lowercase().contains(queryLower)
        }
    }
}
