package com.example.huertabeja.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.remote.ApiConfig
import com.example.huertabeja.data.model.LoginRequest
import com.example.huertabeja.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val usuario: Usuario, val token: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = LoginUiState.Loading
                
                val response = ApiConfig.getUsuariosService().loginUsuario(
                    LoginRequest(email, password)
                )
                
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        // Convertir LoginResponse a Usuario
                        val usuario = Usuario(
                            id = loginResponse.id,
                            nombre = loginResponse.nombre,
                            apellido = loginResponse.apellido,
                            email = loginResponse.email,
                            telefono = loginResponse.telefono,
                            direccion = loginResponse.direccion,
                            rol = loginResponse.rol,
                            token = loginResponse.token,
                            createdAt = null,
                            updatedAt = null
                        )
                        _uiState.value = LoginUiState.Success(usuario, loginResponse.token)
                    } else {
                        _uiState.value = LoginUiState.Error("Error al procesar la respuesta del servidor")
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Credenciales incorrectas"
                        404 -> "Usuario no encontrado"
                        500 -> "Error del servidor. Intenta más tarde"
                        else -> "Error de conexión. Código: ${response.code()}"
                    }
                    _uiState.value = LoginUiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(
                    "Error de red: ${e.message ?: "Verifica tu conexión a internet"}"
                )
            }
        }
    }
    
    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
