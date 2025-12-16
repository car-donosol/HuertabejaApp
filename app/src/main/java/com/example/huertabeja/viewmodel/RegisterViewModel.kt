package com.example.huertabeja.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.remote.ApiConfig
import com.example.huertabeja.data.model.RegistroRequest
import com.example.huertabeja.data.model.Direccion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val message: String, val token: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {
    
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()
    
    fun registerUser(
        run: String,
        dv: String,
        nombres: String,
        apellidos: String,
        email: String,
        telefono: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                _registerState.value = RegisterState.Loading
                
                // Crear request según el formato del backend de Railway
                val request = RegistroRequest(
                    nombre = nombres,
                    apellido = apellidos,
                    email = email,
                    password = password,
                    telefono = telefono,
                    direccion = null // Opcional, puede agregarse después
                )
                
                val response = ApiConfig.getUsuariosService().registrarUsuario(request)
                
                if (response.isSuccessful) {
                    val registroResponse = response.body()
                    if (registroResponse != null) {
                        _registerState.value = RegisterState.Success(
                            registroResponse.mensaje ?: "Usuario registrado exitosamente",
                            registroResponse.token
                        )
                    } else {
                        _registerState.value = RegisterState.Error("Error al procesar la respuesta")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = when (response.code()) {
                        409 -> "El email ya está registrado"
                        400 -> "Datos inválidos. Verifica los campos"
                        else -> errorBody ?: "Error al registrar usuario: ${response.code()}"
                    }
                    _registerState.value = RegisterState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(
                    "Error de conexión: ${e.message ?: "Verifica tu conexión a internet"}"
                )
            }
        }
    }
    
    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}
