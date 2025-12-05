package com.example.huertabeja.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.Order
import com.example.huertabeja.data.OrderStatus
import com.example.huertabeja.data.api.RetrofitClient
import com.example.huertabeja.data.model.Sale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class OrdersViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private val salesApiService = RetrofitClient.salesService

    fun loadOrders(userId: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                Log.d("OrdersViewModel", "Loading orders for user: $userId")
                
                val response = if (userId != null) {
                    salesApiService.getSalesByClienteId(userId)
                } else {
                    salesApiService.getAllSales()
                }

                if (response.isSuccessful) {
                    val sales = response.body() ?: emptyList()
                    // Convertir Sales a Orders (adaptación temporal)
                    val orders = sales.map { sale -> saleToOrder(sale) }
                    
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

    private fun saleToOrder(sale: Sale): Order {
        // Convertir Sale (backend) a Order (UI)
        val status = when (sale.status.lowercase()) {
            "pendiente", "pending" -> OrderStatus.PENDING
            "procesando", "processing" -> OrderStatus.PROCESSING
            "enviado", "shipped" -> OrderStatus.SHIPPED
            "entregado", "delivered" -> OrderStatus.DELIVERED
            "cancelado", "cancelled" -> OrderStatus.CANCELLED
            "paid", "pagado" -> OrderStatus.DELIVERED
            else -> OrderStatus.PENDING
        }
        
        return Order(
            id = sale.clienteId, // Usamos clienteId como id temporal
            date = java.util.Date(), // No tenemos fecha en el backend actual
            items = emptyList(), // El backend no tiene items
            total = sale.total.toDouble(),
            status = status
        )
    }

    fun refresh(userId: String?) {
        loadOrders(userId)
    }
}
