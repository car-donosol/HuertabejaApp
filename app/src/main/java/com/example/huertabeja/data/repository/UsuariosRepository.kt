package com.example.huertabeja.data.repository

import com.example.huertabeja.data.model.*
import com.example.huertabeja.data.remote.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsuariosRepository {
    
    private val apiService = ApiConfig.getUsuariosService()
    
    suspend fun registrarUsuario(request: RegistroRequest): Result<RegistroResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.registrarUsuario(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Datos inválidos. Verifica los campos."
                        409 -> "El email ya está registrado."
                        else -> "Error al registrar: ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun loginUsuario(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(email, password)
                val response = apiService.loginUsuario(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Email o contraseña incorrectos."
                        404 -> "Usuario no encontrado."
                        else -> "Error al iniciar sesión: ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun obtenerPerfil(usuarioId: String, token: String): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerPerfil(usuarioId, "Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener perfil: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun actualizarPerfil(usuarioId: String, token: String, usuario: Usuario): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.actualizarPerfil(usuarioId, "Bearer $token", usuario)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al actualizar perfil: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun obtenerTodosUsuarios(token: String): Result<List<Usuario>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerTodosUsuarios("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener usuarios: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
}
