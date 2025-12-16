package com.example.huertabeja.ui.viewmodel

import android.app.Application
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.remote.ApiConfig
import com.example.huertabeja.data.model.User
import com.example.huertabeja.data.model.Usuario
import com.example.huertabeja.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed class PerfilUiState {
    object Idle : PerfilUiState()
    object Loading : PerfilUiState()
    data class Success(val user: User) : PerfilUiState()
    data class Error(val message: String) : PerfilUiState()
}

class PerfilViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow<PerfilUiState>(PerfilUiState.Idle)
    val uiState: StateFlow<PerfilUiState> = _uiState
    
    private val sessionManager = SessionManager(application)
    private val usuariosApiService = ApiConfig.getUsuariosService()
    
    private fun decodeJWT(token: String): String? {
        try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = JSONObject(payload)
            return json.optString("id")
        } catch (e: Exception) {
            return null
        }
    }
    
    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _uiState.value = PerfilUiState.Loading
                
                val token = sessionManager.getAuthToken()
                if (token.isNullOrBlank()) {
                    _uiState.value = PerfilUiState.Error("No hay sesión activa")
                    return@launch
                }
                
                // Decodificar el JWT para obtener el ID real del usuario
                val userId = decodeJWT(token)
                if (userId.isNullOrBlank()) {
                    _uiState.value = PerfilUiState.Error("Token inválido")
                    return@launch
                }
                
                val response = usuariosApiService.obtenerPerfil(userId, "Bearer $token")
                
                if (response.isSuccessful) {
                    val usuario = response.body()
                    if (usuario != null) {
                        // Convertir Usuario (Railway) a User (app local)
                        val user = usuarioToUser(usuario)
                        _uiState.value = PerfilUiState.Success(user)
                    } else {
                        _uiState.value = PerfilUiState.Error("No se pudo cargar el perfil del usuario")
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "Usuario no encontrado"
                        500 -> "Error del servidor. Intenta más tarde"
                        else -> "Error al cargar perfil. Código: ${response.code()}"
                    }
                    _uiState.value = PerfilUiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _uiState.value = PerfilUiState.Error(
                    "Error de red: ${e.message ?: "Verifica tu conexión a internet"}"
                )
            }
        }
    }
    
    private fun usuarioToUser(usuario: Usuario): User {
        // Convertir createdAt de ISO 8601 a yyyy-MM-dd
        val fechaRegistro = usuario.createdAt?.let {
            try {
                // Formato: 2024-12-15T21:23:33.760Z -> 2024-12-15
                it.split("T").firstOrNull() ?: ""
            } catch (e: Exception) {
                ""
            }
        } ?: ""
        
        return User(
            id = usuario.id,
            run = 0, // No disponible en Railway
            dv = 0, // No disponible en Railway
            pnombre = usuario.nombre,
            snombre = null, // No disponible en Railway
            appaterno = usuario.apellido,
            apmaterno = "", // No disponible en Railway
            email = usuario.email,
            telefono = usuario.telefono?.toIntOrNull() ?: 0,
            fechareg = fechaRegistro,
            password = "" // No se devuelve desde Railway
        )
    }
    
    fun resetState() {
        _uiState.value = PerfilUiState.Idle
    }
}
