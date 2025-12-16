package com.example.huertabeja.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.Product
import com.example.huertabeja.data.remote.ApiConfig
import com.example.huertabeja.data.model.CrearProductoRequest
import com.example.huertabeja.data.model.ActualizarProductoRequest
import com.example.huertabeja.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para operaciones de administración de productos
 * NOTA: Las categorías válidas en Railway son:
 * Electrónica, Ropa, Alimentos, Hogar, Deportes, Libros, Juguetes, Otros
 */
class AdminProductViewModel(application: Application) : AndroidViewModel(application) {
    
    private val sessionManager = SessionManager(application)
    
    private val productApiService = ApiConfig.getProductosService()
    
    // Mapeo de categorías de la app a categorías del backend
    private fun mapCategory(appCategory: String): String {
        return when(appCategory.lowercase()) {
            "interior", "plantas" -> "Hogar"
            "exterior" -> "Hogar"
            "decoración", "decoracion" -> "Hogar"
            else -> "Otros"
        }
    }
    
    // Estado de la operación administrativa
    private val _adminState = MutableStateFlow<AdminState>(AdminState.Idle)
    val adminState: StateFlow<AdminState> = _adminState.asStateFlow()
    
    // Producto seleccionado para edición
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()
    
    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Mensaje de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * Crear un nuevo producto
     */
    fun createProduct(
        title: String,
        price: Int,
        priceOffer: Int = 0,
        image: String,
        description: String,
        stock: Int,
        category: String,
        home: Boolean = false,
        slug: String,
        token: String? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _adminState.value = AdminState.Loading
                _errorMessage.value = null
                
                val mappedCategory = mapCategory(category)
                
                val request = CrearProductoRequest(
                    nombre = title,
                    descripcion = description,
                    precio = price.toDouble(),
                    categoria = mappedCategory,
                    stock = stock,
                    imagenes = listOf(image),
                    marca = null,
                    descuento = priceOffer,
                    disponible = true
                )
                
                Log.d("AdminProductViewModel", "=== CREAR PRODUCTO (Railway) ===")
                Log.d("AdminProductViewModel", "Nombre: $title")
                Log.d("AdminProductViewModel", "Precio: $price")
                Log.d("AdminProductViewModel", "Stock: $stock")
                Log.d("AdminProductViewModel", "Categoría original: $category")
                Log.d("AdminProductViewModel", "Categoría mapeada: $mappedCategory")
                Log.d("AdminProductViewModel", "Request completo: $request")
                
                val authToken = token ?: ""
                val response = productApiService.crearProducto("Bearer $authToken", request)
                
                Log.d("AdminProductViewModel", "=== RESPUESTA ===")
                Log.d("AdminProductViewModel", "Response code: ${response.code()}")
                Log.d("AdminProductViewModel", "Response message: ${response.message()}")
                Log.d("AdminProductViewModel", "Response body: ${response.body()}")
                Log.d("AdminProductViewModel", "Response raw: ${response.raw()}")
                
                if (response.isSuccessful) {
                    val producto = response.body()
                    if (producto != null) {
                        _adminState.value = AdminState.Success("Producto creado exitosamente")
                        // Producto de Railway es diferente a Product de la app
                        Log.d("AdminProductViewModel", "Product created successfully: ${producto.nombre}")
                    } else {
                        val error = "Respuesta vacía del servidor"
                        _adminState.value = AdminState.Error(error)
                        _errorMessage.value = error
                        Log.e("AdminProductViewModel", error)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val error = when (response.code()) {
                        405 -> "Error 405: La API no permite crear productos. Verifica la URL o contacta al backend."
                        403 -> "Error 403: No tienes permisos para crear productos"
                        400 -> "Error 400: Datos inválidos - $errorBody"
                        else -> "Error ${response.code()}: ${response.message()} - $errorBody"
                    }
                    _adminState.value = AdminState.Error(error)
                    _errorMessage.value = error
                    Log.e("AdminProductViewModel", "Error creating product: ${response.code()} - ${response.message()}")
                    Log.e("AdminProductViewModel", "Error body: $errorBody")
                }
                
            } catch (e: Exception) {
                val error = "Error de red: ${e.message}"
                _adminState.value = AdminState.Error(error)
                _errorMessage.value = error
                Log.e("AdminProductViewModel", "Exception creating product", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Actualizar un producto existente
     */
    fun updateProduct(
        productoId: String,
        nombre: String? = null,
        descripcion: String? = null,
        precio: Double? = null,
        categoria: String? = null,
        stock: Int? = null,
        imagenes: List<String>? = null,
        marca: String? = null,
        descuento: Int? = null,
        disponible: Boolean? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _adminState.value = AdminState.Loading
                _errorMessage.value = null
                
                val token = sessionManager.getAuthToken()
                if (token.isNullOrBlank()) {
                    _adminState.value = AdminState.Error("Token no disponible")
                    _errorMessage.value = "Token no disponible"
                    return@launch
                }
                
                // Mapear categoría de UI a backend
                val categoriaBackend = when(categoria) {
                    "Interior" -> "Hogar"
                    "Exterior" -> "Deportes"
                    else -> categoria
                }
                
                val request = ActualizarProductoRequest(
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio,
                    categoria = categoriaBackend,
                    stock = stock,
                    imagenes = imagenes,
                    marca = marca,
                    descuento = descuento,
                    disponible = disponible
                )
                
                Log.d("AdminProductViewModel", "Updating product: $productoId")
                
                val productosService = ApiConfig.getProductosService()
                val response = productosService.actualizarProducto(productoId, "Bearer $token", request)
                
                Log.d("AdminProductViewModel", "Response code: ${response.code()}")
                Log.d("AdminProductViewModel", "Response body: ${response.body()}")
                
                if (response.isSuccessful) {
                    val producto = response.body()
                    if (producto != null) {
                        _adminState.value = AdminState.Success("Producto actualizado exitosamente")
                        Log.d("AdminProductViewModel", "Product updated successfully: ${producto.nombre}")
                    } else {
                        val error = "Respuesta vacía del servidor"
                        _adminState.value = AdminState.Error(error)
                        _errorMessage.value = error
                        Log.e("AdminProductViewModel", error)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val error = when (response.code()) {
                        403 -> "Error 403: No tienes permisos para actualizar productos"
                        404 -> "Error 404: Producto no encontrado"
                        400 -> "Error 400: Datos inválidos - $errorBody"
                        else -> "Error ${response.code()}: ${response.message()} - $errorBody"
                    }
                    _adminState.value = AdminState.Error(error)
                    _errorMessage.value = error
                    Log.e("AdminProductViewModel", "Error updating product: ${response.code()} - ${response.message()}")
                    Log.e("AdminProductViewModel", "Error body: $errorBody")
                }
                
            } catch (e: Exception) {
                val error = "Error de red: ${e.message}"
                _adminState.value = AdminState.Error(error)
                _errorMessage.value = error
                Log.e("AdminProductViewModel", "Exception updating product", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Eliminar un producto
     */
    fun deleteProduct(productoId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _adminState.value = AdminState.Loading
                _errorMessage.value = null
                
                val token = sessionManager.getAuthToken()
                if (token.isNullOrBlank()) {
                    _adminState.value = AdminState.Error("Token no disponible")
                    _errorMessage.value = "Token no disponible"
                    return@launch
                }
                
                Log.d("AdminProductViewModel", "Deleting product: $productoId")
                
                val productosService = ApiConfig.getProductosService()
                val response = productosService.eliminarProducto(productoId, "Bearer $token")
                
                Log.d("AdminProductViewModel", "Response code: ${response.code()}")
                Log.d("AdminProductViewModel", "Response body: ${response.body()}")
                
                if (response.isSuccessful) {
                    val deleteResponse = response.body()
                    // Backend devuelve Map<String, String> con mensaje
                    val mensaje = deleteResponse?.get("message") ?: deleteResponse?.get("mensaje")
                    if (mensaje != null) {
                        _adminState.value = AdminState.Success(mensaje)
                        _selectedProduct.value = null
                        Log.d("AdminProductViewModel", "Product deleted successfully: $mensaje")
                    } else {
                        _adminState.value = AdminState.Success("Producto eliminado exitosamente")
                        _selectedProduct.value = null
                        Log.d("AdminProductViewModel", "Product deleted successfully")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val error = when (response.code()) {
                        403 -> "Error 403: No tienes permisos para eliminar productos"
                        404 -> "Error 404: Producto no encontrado"
                        400 -> "Error 400: Datos inválidos - $errorBody"
                        else -> "Error ${response.code()}: ${response.message()} - $errorBody"
                    }
                    _adminState.value = AdminState.Error(error)
                    _errorMessage.value = error
                    Log.e("AdminProductViewModel", "Error deleting product: ${response.code()} - ${response.message()}")
                    Log.e("AdminProductViewModel", "Error body: $errorBody")
                }
                
            } catch (e: Exception) {
                val error = "Error de red: ${e.message}"
                _adminState.value = AdminState.Error(error)
                _errorMessage.value = error
                Log.e("AdminProductViewModel", "Exception deleting product", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Seleccionar un producto para edición
     */
    fun selectProduct(product: Product) {
        _selectedProduct.value = product
    }
    
    /**
     * Limpiar la selección de producto
     */
    fun clearSelectedProduct() {
        _selectedProduct.value = null
    }
    
    /**
     * Limpiar el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Resetear el estado
     */
    fun resetState() {
        _adminState.value = AdminState.Idle
        _errorMessage.value = null
    }
}

/**
 * Estados posibles de las operaciones administrativas
 */
sealed class AdminState {
    object Idle : AdminState()
    object Loading : AdminState()
    data class Success(val message: String) : AdminState()
    data class Error(val message: String) : AdminState()
}
