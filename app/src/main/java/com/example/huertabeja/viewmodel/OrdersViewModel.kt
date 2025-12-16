package com.example.huertabeja.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.Order
import com.example.huertabeja.data.OrderItem
import com.example.huertabeja.data.OrderStatus
import com.example.huertabeja.data.Product
import com.example.huertabeja.data.remote.ApiConfig
import com.example.huertabeja.data.model.Pedido
import com.example.huertabeja.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class OrdersViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private val sessionManager = SessionManager(application)
    private val pedidosApiService = ApiConfig.getPedidosService()

    fun loadOrders(userId: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                Log.d("OrdersViewModel", "Loading orders for user: $userId")
                
                val token = sessionManager.getAuthToken()
                if (token.isNullOrBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No hay sesión activa"
                    )
                    return@launch
                }
                
                val response = if (userId != null) {
                    pedidosApiService.obtenerPedidosPorUsuario(userId, "Bearer $token")
                } else {
                    pedidosApiService.obtenerTodosPedidos("Bearer $token")
                }

                if (response.isSuccessful) {
                    val pedidosResponse = response.body()
                    val pedidos = pedidosResponse?.pedidos ?: emptyList()
                    // Convertir Pedido (backend Railway) a Order (UI app)
                    val orders = pedidos.map { pedido -> pedidoToOrder(pedido) }
                    
                    _uiState.value = _uiState.value.copy(
                        orders = orders,
                        isLoading = false,
                        error = null
                    )
                    Log.d("OrdersViewModel", "Loaded ${orders.size} orders")
                } else {
                    val error = if (response.code() == 500) {
                        "El servicio de pedidos está temporalmente fuera de servicio. Intenta más tarde."
                    } else {
                        "Error al cargar los pedidos: ${response.code()}"
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error
                    )
                    Log.e("OrdersViewModel", error)
                }
            } catch (e: Exception) {
                val error = "Error de conexión: ${e.message}"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error
                )
                Log.e("OrdersViewModel", "Exception loading orders", e)
            }
        }
    }

    private fun pedidoToOrder(pedido: Pedido): Order {
        // Convertir Pedido (backend Railway) a Order (UI app)
        val status = when (pedido.estado.lowercase()) {
            "pendiente", "pending" -> OrderStatus.PENDING
            "procesando", "processing", "en proceso" -> OrderStatus.PROCESSING
            "enviado", "shipped" -> OrderStatus.SHIPPED
            "entregado", "delivered" -> OrderStatus.DELIVERED
            "cancelado", "cancelled" -> OrderStatus.CANCELLED
            else -> OrderStatus.PENDING
        }
        
        // Convertir productos del pedido a OrderItems
        val items = pedido.productos.map { productoPedido ->
            val precioInt = productoPedido.precio.toInt()
            OrderItem(
                product = Product(
                    id = productoPedido.productoId,
                    title = productoPedido.nombre,
                    price = precioInt,
                    price_offer = precioInt,
                    image = "",
                    description = "",
                    rating = null,
                    stock = 0,
                    category = "",
                    home = false,
                    slug = productoPedido.productoId
                ),
                quantity = productoPedido.cantidad,
                price = productoPedido.precio
            )
        }
        
        // Parsear fecha del pedido
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = try {
            dateFormat.parse(pedido.fechaPedido) ?: Date()
        } catch (e: Exception) {
            Date()
        }
        
        return Order(
            id = pedido.id,
            date = date,
            items = items,
            total = pedido.total,
            status = status
        )
    }

    fun refresh(userId: String?) {
        loadOrders(userId)
    }
}
