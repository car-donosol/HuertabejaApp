package com.example.huertabeja.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.Product
import com.example.huertabeja.data.api.RetrofitClient
import com.example.huertabeja.data.model.CreateProductRequest
import com.example.huertabeja.data.model.UpdateProductRequest
import com.example.huertabeja.data.model.UpdateProductResponse
import com.example.huertabeja.data.model.DeleteProductResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para operaciones de administración de productos
 */
class AdminProductViewModel : ViewModel() {
    
    private val productApiService = RetrofitClient.productService
    
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
        slug: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _adminState.value = AdminState.Loading
                _errorMessage.value = null
                
                val request = CreateProductRequest(
                    title = title,
                    price = price,
                    priceOffer = priceOffer,
                    image = image,
                    description = description,
                    stock = stock,
                    category = category,
                    home = home,
                    slug = slug
                )
                
                Log.d("AdminProductViewModel", "=== CREAR PRODUCTO ===")
                Log.d("AdminProductViewModel", "Title: $title")
                Log.d("AdminProductViewModel", "Price: $price")
                Log.d("AdminProductViewModel", "Stock: $stock")
                Log.d("AdminProductViewModel", "Slug: $slug")
                Log.d("AdminProductViewModel", "Category: $category")
                Log.d("AdminProductViewModel", "Request completo: $request")
                
                val response = productApiService.createProduct(request)
                
                Log.d("AdminProductViewModel", "=== RESPUESTA ===")
                Log.d("AdminProductViewModel", "Response code: ${response.code()}")
                Log.d("AdminProductViewModel", "Response message: ${response.message()}")
                Log.d("AdminProductViewModel", "Response body: ${response.body()}")
                Log.d("AdminProductViewModel", "Response raw: ${response.raw()}")
                
                if (response.isSuccessful) {
                    val product = response.body()
                    if (product != null) {
                        _adminState.value = AdminState.Success("Producto creado exitosamente")
                        _selectedProduct.value = product
                        Log.d("AdminProductViewModel", "Product created successfully: ${product.title}")
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
        slug: String,
        title: String? = null,
        price: Int? = null,
        priceOffer: Int? = null,
        image: String? = null,
        description: String? = null,
        stock: Int? = null,
        category: String? = null,
        home: Boolean? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _adminState.value = AdminState.Loading
                _errorMessage.value = null
                
                val request = UpdateProductRequest(
                    title = title,
                    price = price,
                    priceOffer = priceOffer,
                    image = image,
                    description = description,
                    stock = stock,
                    category = category,
                    home = home
                )
                
                Log.d("AdminProductViewModel", "Updating product: $slug")
                
                val response = productApiService.updateProduct(slug, request)
                
                Log.d("AdminProductViewModel", "Response code: ${response.code()}")
                Log.d("AdminProductViewModel", "Response body: ${response.body()}")
                
                if (response.isSuccessful) {
                    val updateResponse = response.body()
                    if (updateResponse?.success == true) {
                        _adminState.value = AdminState.Success(updateResponse.message)
                        Log.d("AdminProductViewModel", "Product updated successfully")
                    } else {
                        val error = updateResponse?.message ?: "Respuesta vacía del servidor"
                        _adminState.value = AdminState.Error(error)
                        _errorMessage.value = error
                        Log.e("AdminProductViewModel", error)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val error = when (response.code()) {
                        405 -> "Error 405: La API no permite actualizar productos. Verifica la URL o contacta al backend."
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
    fun deleteProduct(slug: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _adminState.value = AdminState.Loading
                _errorMessage.value = null
                
                Log.d("AdminProductViewModel", "Deleting product: $slug")
                
                val response = productApiService.deleteProduct(slug)
                
                Log.d("AdminProductViewModel", "Response code: ${response.code()}")
                Log.d("AdminProductViewModel", "Response body: ${response.body()}")
                
                if (response.isSuccessful) {
                    val deleteResponse = response.body()
                    if (deleteResponse?.success == true) {
                        _adminState.value = AdminState.Success(deleteResponse.message)
                        _selectedProduct.value = null
                        Log.d("AdminProductViewModel", "Product deleted successfully")
                    } else {
                        val error = deleteResponse?.message ?: "Error al eliminar el producto"
                        _adminState.value = AdminState.Error(error)
                        _errorMessage.value = error
                        Log.e("AdminProductViewModel", error)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val error = when (response.code()) {
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
