package com.example.huertabeja.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {
    
    /**
     * Verifica si el dispositivo tiene conexión a Internet
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
    
    /**
     * Verifica si el backend está disponible en la URL especificada
     * @param baseUrl URL base del backend (ejemplo: "http://192.168.100.123:3001")
     * @return true si el backend responde, false en caso contrario
     */
    suspend fun checkBackendConnection(baseUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Intenta conectarse al endpoint de health o a la raíz
                val url = URL("$baseUrl/health")
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000
                }
                
                val responseCode = connection.responseCode
                connection.disconnect()
                
                responseCode in 200..299
            } catch (e: Exception) {
                false
            }
        }
    }
    
    /**
     * Verifica la conectividad con todos los microservicios
     * @return Map con el estado de cada servicio
     */
    suspend fun checkAllBackendServices(
        usuariosUrl: String,
        productosUrl: String,
        pedidosUrl: String
    ): Map<String, Boolean> {
        return withContext(Dispatchers.IO) {
            mapOf(
                "usuarios" to checkBackendConnection(usuariosUrl),
                "productos" to checkBackendConnection(productosUrl),
                "pedidos" to checkBackendConnection(pedidosUrl)
            )
        }
    }
    
    /**
     * Obtiene un mensaje de error amigable basado en el tipo de error de red
     */
    fun getNetworkErrorMessage(exception: Exception): String {
        return when {
            exception.message?.contains("Unable to resolve host") == true -> 
                "No se puede conectar al servidor. Verifica tu conexión a Internet."
            exception.message?.contains("Connection refused") == true -> 
                "El servidor no está disponible. Verifica que el backend esté ejecutándose."
            exception.message?.contains("timeout") == true -> 
                "La conexión tardó demasiado. Intenta nuevamente."
            exception.message?.contains("SSLHandshakeException") == true -> 
                "Error de seguridad en la conexión. Verifica la configuración SSL."
            else -> "Error de conexión: ${exception.message ?: "Desconocido"}"
        }
    }
    
    /**
     * Formatea una URL base eliminando barras finales innecesarias
     */
    fun formatBaseUrl(url: String): String {
        return url.trimEnd('/')
    }
    
    /**
     * Verifica si una URL es válida
     */
    fun isValidUrl(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (e: Exception) {
            false
        }
    }
}
