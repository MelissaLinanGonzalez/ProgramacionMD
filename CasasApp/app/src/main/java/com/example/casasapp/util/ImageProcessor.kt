package com.example.casasapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Utilidad de procesamiento de imágenes para la aplicación CasasApp.
 *
 * Esta clase proporciona funciones estáticas para capturar, redimensionar,
 * comprimir y convertir imágenes de propiedades inmobiliarias.
 * Utiliza las clases multimedia del SDK de Android:
 * - [Bitmap]: Representación en memoria de una imagen rasterizada.
 * - [BitmapFactory]: Factoría para decodificar imágenes desde distintas fuentes.
 * - [ByteArrayOutputStream]: Stream de bytes para la conversión de formatos.
 * - [Bitmap.CompressFormat]: Enumeración de formatos de compresión (JPEG, WEBP).
 *
 * Se usa para cumplir los criterios de evaluación B (procesamiento),
 * C (conversión de formatos multimedia) y D (transformación geométrica).
 */
object ImageProcessor {

    /** Ancho máximo por defecto para redimensionado de imágenes */
    private const val MAX_ANCHO_DEFAULT = 800

    /** Alto máximo por defecto para redimensionado de imágenes */
    private const val MAX_ALTO_DEFAULT = 800

    /** Calidad de compresión por defecto (0-100) */
    private const val CALIDAD_DEFAULT = 80

    /**
     * Aplica una transformación geométrica de redimensionado a un [Bitmap].
     *
     * Escala la imagen proporcionalmente para que no exceda las dimensiones
     * máximas especificadas, manteniendo la relación de aspecto original.
     * Utiliza [Bitmap.createScaledBitmap] que internamente aplica un filtro
     * bilineal para una interpolación de calidad.
     *
     * @param bitmap El [Bitmap] original capturado desde la cámara.
     * @param maxAncho Ancho máximo permitido en píxeles. Por defecto 800px.
     * @param maxAlto Alto máximo permitido en píxeles. Por defecto 800px.
     * @return Un nuevo [Bitmap] redimensionado proporcionalmente, o el original
     *         si ya cumple con las dimensiones máximas.
     *
     * Ejemplo de uso:
     * ```kotlin
     * val bitmapOriginal: Bitmap = ... // desde cámara
     * val bitmapReducido = ImageProcessor.redimensionarBitmap(bitmapOriginal)
     * ```
     */
    fun redimensionarBitmap(
        bitmap: Bitmap,
        maxAncho: Int = MAX_ANCHO_DEFAULT,
        maxAlto: Int = MAX_ALTO_DEFAULT
    ): Bitmap {
        val anchoOriginal = bitmap.width
        val altoOriginal = bitmap.height

        // Si ya cumple las dimensiones, devolver el original
        if (anchoOriginal <= maxAncho && altoOriginal <= maxAlto) {
            return bitmap
        }

        // Calcular la escala manteniendo la relación de aspecto
        val ratioAncho = maxAncho.toFloat() / anchoOriginal.toFloat()
        val ratioAlto = maxAlto.toFloat() / altoOriginal.toFloat()
        val ratio = minOf(ratioAncho, ratioAlto)

        val nuevoAncho = (anchoOriginal * ratio).toInt()
        val nuevoAlto = (altoOriginal * ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, nuevoAncho, nuevoAlto, true)
    }

    /**
     * Comprime un [Bitmap] al formato JPEG y lo devuelve como [ByteArray].
     *
     * Utiliza [Bitmap.compress] con [Bitmap.CompressFormat.JPEG] para convertir
     * la imagen a formato JPEG. El resultado se escribe en un
     * [ByteArrayOutputStream] que permite obtener los bytes del archivo comprimido.
     *
     * @param bitmap El [Bitmap] a comprimir (idealmente ya redimensionado).
     * @param calidad Calidad de compresión entre 0 (mínima) y 100 (máxima).
     *                Por defecto 80%, equilibrio entre calidad y tamaño de archivo.
     * @return [ByteArray] con los datos de la imagen comprimida en formato JPEG.
     */
    fun comprimirAJpeg(bitmap: Bitmap, calidad: Int = CALIDAD_DEFAULT): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, calidad, outputStream)
        return outputStream.toByteArray()
    }

    /**
     * Comprime un [Bitmap] al formato WEBP y lo devuelve como [ByteArray].
     *
     * Utiliza [Bitmap.compress] con [Bitmap.CompressFormat.WEBP_LOSSY] (API 30+)
     * para convertir la imagen a formato WebP con compresión con pérdida.
     * WebP ofrece un tamaño de archivo ~25-34% menor que JPEG con calidad similar,
     * lo que es ideal para almacenar imágenes de propiedades ahorrando espacio.
     *
     * @param bitmap El [Bitmap] a comprimir (idealmente ya redimensionado).
     * @param calidad Calidad de compresión entre 0 (mínima) y 100 (máxima).
     *                Por defecto 80%.
     * @return [ByteArray] con los datos de la imagen comprimida en formato WEBP.
     */
    @Suppress("DEPRECATION")
    fun comprimirAWebp(bitmap: Bitmap, calidad: Int = CALIDAD_DEFAULT): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val formato = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            Bitmap.CompressFormat.WEBP
        }
        bitmap.compress(formato, calidad, outputStream)
        return outputStream.toByteArray()
    }

    /**
     * Procesa un [Bitmap] capturado de la cámara y lo guarda como archivo JPEG.
     *
     * Flujo completo de procesamiento multimedia:
     * 1. **Transformación geométrica**: Redimensiona la imagen a max 800x800px
     *    usando [redimensionarBitmap] para ahorrar espacio de almacenamiento.
     * 2. **Conversión de formato**: Comprime el bitmap a JPEG con calidad del 80%
     *    usando [comprimirAJpeg].
     * 3. **Persistencia**: Escribe los bytes resultantes en un archivo temporal
     *    en el directorio de caché de la aplicación usando [FileOutputStream].
     *
     * Se utiliza [Context.cacheDir] para almacenamiento temporal y se genera
     * un nombre de archivo único basado en el timestamp para evitar colisiones.
     *
     * @param context Contexto de la aplicación para acceder al directorio de caché.
     * @param bitmap El [Bitmap] original capturado desde la cámara con
     *               [ActivityResultContracts.TakePicturePreview].
     * @return [File] con la imagen procesada lista para ser mostrada o almacenada,
     *         o null si ocurre un error durante el procesamiento.
     */
    fun guardarBitmapComoArchivo(context: Context, bitmap: Bitmap): File? {
        return try {
            // Paso 1: Transformación geométrica (redimensionar)
            val bitmapRedimensionado = redimensionarBitmap(bitmap)

            // Paso 2: Conversión a JPEG con compresión del 80%
            val bytesJpeg = comprimirAJpeg(bitmapRedimensionado)

            // Paso 3: Guardar en archivo temporal
            val archivo = File(
                context.cacheDir,
                "casa_foto_${System.currentTimeMillis()}.jpg"
            )
            FileOutputStream(archivo).use { fos ->
                fos.write(bytesJpeg)
            }

            archivo
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
