package com.example.huertabeja.data.repository

import com.example.huertabeja.data.model.*
import com.example.huertabeja.data.remote.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PedidosRepository {
    
    private val apiService = ApiConfig.getPedidosService()
    
    suspend fun crearPedido(token: String, request: CrearPedidoRequest): Result<PedidoResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.crearPedido("Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    val errorMessage = when (response.code()) {
                        400 -> "Datos del pedido inválidos. $errorBody"
                        404 -> "Producto no encontrado o sin stock. $errorBody"
                        else -> "Error al crear pedido: ${response.code()}. $errorBody"
                    }
                    android.util.Log.e("PedidosRepository", "Error creating order: Code=${response.code()}, Body=$errorBody")
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                android.util.Log.e("PedidosRepository", "Exception creating order", e)
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun obtenerPedidosPorUsuario(usuarioId: String, token: String): Result<List<Pedido>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerPedidosPorUsuario(usuarioId, "Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val pedidosResponse = response.body()!!
                    Result.success(pedidosResponse.pedidos)
                } else {
                    Result.failure(Exception("Error al obtener pedidos: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun obtenerPedidoPorId(pedidoId: String, token: String): Result<Pedido> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerPedidoPorId(pedidoId, "Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "Pedido no encontrado."
                        else -> "Error al obtener pedido: ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun obtenerTodosPedidos(token: String): Result<List<Pedido>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerTodosPedidos("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val pedidosResponse = response.body()!!
                    Result.success(pedidosResponse.pedidos)
                } else {
                    Result.failure(Exception("Error al obtener pedidos: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun cancelarPedido(pedidoId: String, token: String, motivo: String): Result<Pedido> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CancelarPedidoRequest(motivo)
                val response = apiService.cancelarPedido(pedidoId, "Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "No se puede cancelar el pedido en su estado actual."
                        404 -> "Pedido no encontrado."
                        else -> "Error al cancelar pedido: ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun actualizarEstado(pedidoId: String, token: String, estado: String): Result<Pedido> {
        return withContext(Dispatchers.IO) {
            try {
                val request = ActualizarEstadoRequest(estado)
                val response = apiService.actualizarEstado(pedidoId, "Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al actualizar estado: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
}
