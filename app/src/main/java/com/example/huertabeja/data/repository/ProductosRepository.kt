package com.example.huertabeja.data.repository

import com.example.huertabeja.data.model.*
import com.example.huertabeja.data.remote.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductosRepository {
    
    private val apiService = ApiConfig.getProductosService()
    
    suspend fun obtenerProductos(
        pagina: Int = 1,
        limite: Int = 10,
        categoria: String? = null,
        disponible: Boolean? = null,
        buscar: String? = null
    ): Result<ProductosResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerProductos(pagina, limite, categoria, disponible, buscar)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener productos: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun obtenerProductoPorId(productoId: String): Result<Producto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerProductoPorId(productoId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "Producto no encontrado."
                        else -> "Error al obtener producto: ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun obtenerProductosPorCategoria(categoria: String): Result<List<Producto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerProductosPorCategoria(categoria)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener productos: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun crearProducto(token: String, producto: CrearProductoRequest): Result<Producto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.crearProducto("Bearer $token", producto)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al crear producto: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun actualizarProducto(
        productoId: String,
        token: String,
        producto: ActualizarProductoRequest
    ): Result<Producto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.actualizarProducto(productoId, "Bearer $token", producto)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al actualizar producto: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun eliminarProducto(productoId: String, token: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.eliminarProducto(productoId, "Bearer $token")
                if (response.isSuccessful) {
                    Result.success("Producto eliminado exitosamente")
                } else {
                    Result.failure(Exception("Error al eliminar producto: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    
    suspend fun buscarProductos(query: String): Result<List<Producto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.buscarProductos(query)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al buscar productos: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
}
