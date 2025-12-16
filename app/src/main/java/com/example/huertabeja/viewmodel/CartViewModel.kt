package com.example.huertabeja.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.Product
import com.example.huertabeja.data.model.*
import com.example.huertabeja.data.repository.PedidosRepository
import com.example.huertabeja.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartUiState(
    val products: Map<Product, Int> = emptyMap(),
    val totalPrice: Double = 0.0,
    val isCreatingOrder: Boolean = false,
    val orderCreated: Boolean = false,
    val orderError: String? = null
)
class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()
    
    private val sessionManager = SessionManager(application)
    private val pedidosRepository = PedidosRepository()

    fun addProduct(product: Product) {
        _uiState.update { currentState ->
            val newProducts = currentState.products.toMutableMap()
            newProducts[product] = (newProducts[product] ?: 0) + 1

            currentState.copy(
                products = newProducts,
                totalPrice = calculateTotalPrice(newProducts)
            )
        }
    }

    fun decreaseQuantity(product: Product) {
        _uiState.update { currentState ->
            val newProducts = currentState.products.toMutableMap()
            val currentQuantity = newProducts[product] ?: 0
            if (currentQuantity > 1) {
                newProducts[product] = currentQuantity - 1
            } else {
                newProducts.remove(product)
            }

            currentState.copy(
                products = newProducts,
                totalPrice = calculateTotalPrice(newProducts)
            )
        }
    }

    fun removeProductFromCart(product: Product) {
        _uiState.update { currentState ->
            val newProducts = currentState.products.toMutableMap()
            newProducts.remove(product)

            currentState.copy(
                products = newProducts,
                totalPrice = calculateTotalPrice(newProducts)
            )
        }
    }

    fun clearCart() {
        _uiState.value = CartUiState()
    }

    private fun calculateTotalPrice(products: Map<Product, Int>): Double {
        var totalPrice = 0.0
        for ((product, quantity) in products) {
            totalPrice += product.price * quantity
        }
        return totalPrice
    }
    
    fun createOrder(direccionEntrega: Direccion, metodoPago: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isCreatingOrder = true, orderError = null) }
                
                val token = sessionManager.getAuthToken()
                val usuarioId = sessionManager.getUserId()
                
                if (token.isNullOrBlank() || usuarioId.isNullOrBlank()) {
                    _uiState.update { 
                        it.copy(
                            isCreatingOrder = false, 
                            orderError = "No hay sesión activa"
                        ) 
                    }
                    return@launch
                }
                
                // Convertir productos del carrito a ProductoCarrito
                val productos = _uiState.value.products.mapNotNull { (product, quantity) ->
                    val productoId = product.id ?: product.slug
                    Log.d("CartViewModel", "Product: ${product.title}, ID: $productoId, Quantity: $quantity")
                    
                    if (productoId.isNullOrBlank()) {
                        Log.e("CartViewModel", "Product ${product.title} has no valid ID")
                        null
                    } else {
                        ProductoCarrito(
                            productoId = productoId,
                            cantidad = quantity
                        )
                    }
                }
                
                if (productos.isEmpty()) {
                    _uiState.update { 
                        it.copy(
                            isCreatingOrder = false,
                            orderError = "No hay productos válidos en el carrito"
                        ) 
                    }
                    return@launch
                }
                
                val request = CrearPedidoRequest(
                    usuarioId = usuarioId,
                    productos = productos,
                    direccionEntrega = direccionEntrega,
                    metodoPago = metodoPago,
                    notas = null
                )
                
                Log.d("CartViewModel", "Creating order with ${productos.size} products for user $usuarioId")
                Log.d("CartViewModel", "Request: usuarioId=$usuarioId, productos=${productos.map { "id=${it.productoId}, qty=${it.cantidad}" }}, metodoPago=$metodoPago")
                
                val result = pedidosRepository.crearPedido(token, request)
                
                if (result.isSuccess) {
                    Log.d("CartViewModel", "Order created successfully: ${result.getOrNull()}")
                    _uiState.update { 
                        it.copy(
                            isCreatingOrder = false,
                            orderCreated = true,
                            products = emptyMap(),
                            totalPrice = 0.0
                        ) 
                    }
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    Log.e("CartViewModel", "Error creating order: $error", result.exceptionOrNull())
                    _uiState.update { 
                        it.copy(
                            isCreatingOrder = false,
                            orderError = error
                        ) 
                    }
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Exception creating order", e)
                _uiState.update { 
                    it.copy(
                        isCreatingOrder = false,
                        orderError = "Error de conexión: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun resetOrderState() {
        _uiState.update { it.copy(orderCreated = false, orderError = null) }
    }
}