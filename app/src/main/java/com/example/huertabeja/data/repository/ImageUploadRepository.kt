package com.example.huertabeja.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.huertabeja.data.model.ImageUploadResponse
import com.example.huertabeja.data.remote.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

class ImageUploadRepository(private val context: Context) {
    
    private val apiService = ApiConfig.getProductosService()
    
    suspend fun subirImagen(imageUri: Uri): Result<ImageUploadResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                
                if (bytes == null) {
                    return@withContext Result.failure(Exception("No se pudo leer la imagen"))
                }
                
                // Obtener nombre del archivo
                val fileName = getFileName(imageUri) ?: "image_${System.currentTimeMillis()}.jpg"
                
                // Crear RequestBody
                val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                
                // Crear MultipartBody.Part
                val imagePart = MultipartBody.Part.createFormData(
                    "imagen",
                    fileName,
                    requestBody
                )
                
                // Subir imagen
                val response = apiService.subirImagen(imagePart)
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Formato de imagen no válido"
                        413 -> "Imagen muy grande (máximo 5MB)"
                        else -> "Error al subir imagen: ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun eliminarImagen(publicId: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.eliminarImagen(publicId)
                
                if (response.isSuccessful) {
                    Result.success("Imagen eliminada exitosamente")
                } else {
                    Result.failure(Exception("Error al eliminar imagen: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        result = it.getString(nameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result
    }
}
