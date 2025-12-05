package com.example.huertabeja.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.Product
import com.example.huertabeja.data.api.RetrofitClient
import com.example.huertabeja.data.model.CreateProductRequest
import com.example.huertabeja.data.model.UpdateProductRequest
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
                
                Log.d("AdminProductViewModel", "Creating product: $title")
                
                val response = productApiService.createProduct(request)
                
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse?.success == true) {
                        _adminState.value = AdminState.Success("Producto creado exitosamente")
                        _selectedProduct.value = productResponse.product
                        Log.d("AdminProductViewModel", "Product created successfully")
                    } else {
                        val error = productResponse?.message ?: "Error al crear el producto"
                        _adminState.value = AdminState.Error(error)
                        _errorMessage.value = error
                        Log.e("AdminProductViewModel", error)
                    }
                } else {
                    val error = when (response.code()) {
                        405 -> "La API no permite crear productos. Contacta al administrador del servidor."
                        403 -> "No tienes permisos para crear productos"
                        else -> "Error ${response.code()}: ${response.message()}"
                    }
                    _adminState.value = AdminState.Error(error)
                    _errorMessage.value = error
                    Log.e("AdminProductViewModel", "Error creating product: ${response.code()} - ${response.message()}")
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
                
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse?.success == true) {
                        _adminState.value = AdminState.Success("Producto actualizado exitosamente")
                        _selectedProduct.value = productResponse.product
                        Log.d("AdminProductViewModel", "Product updated successfully")
                    } else {
                        val error = productResponse?.message ?: "Error al actualizar el producto"
                        _adminState.value = AdminState.Error(error)
                        _errorMessage.value = error
                        Log.e("AdminProductViewModel", error)
                    }
                } else {
                    val error = when (response.code()) {
                        405 -> "La API no permite actualizar productos. Contacta al administrador del servidor."
                        403 -> "No tienes permisos para actualizar productos"
                        404 -> "Producto no encontrado"
                        else -> "Error ${response.code()}: ${response.message()}"
                    }
                    _adminState.value = AdminState.Error(error)
                    _errorMessage.value = error
                    Log.e("AdminProductViewModel", "Error updating product: ${response.code()} - ${response.message()}")
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
                
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse?.success == true) {
                        _adminState.value = AdminState.Success("Producto eliminado exitosamente")
                        _selectedProduct.value = null
                        Log.d("AdminProductViewModel", "Product deleted successfully")
                    } else {
                        val error = productResponse?.message ?: "Error al eliminar el producto"
                        _adminState.value = AdminState.Error(error)
                        _errorMessage.value = error
                        Log.e("AdminProductViewModel", error)
                    }
                } else {
                    val error = when (response.code()) {
                        405 -> "La API no permite eliminar productos. Contacta al administrador del servidor."
                        403 -> "No tienes permisos para eliminar productos"
                        404 -> "Producto no encontrado"
                        else -> "Error ${response.code()}: ${response.message()}"
                    }
                    _adminState.value = AdminState.Error(error)
                    _errorMessage.value = error
                    Log.e("AdminProductViewModel", "Error deleting product: ${response.code()} - ${response.message()}")
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
