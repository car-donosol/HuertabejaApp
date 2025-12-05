package com.example.huertabeja.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.api.RetrofitClient
import com.example.huertabeja.data.model.CreateSaleRequest
import com.example.huertabeja.data.model.Sale
import com.example.huertabeja.data.model.SaleItem
import com.example.huertabeja.data.model.ShippingAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar las operaciones de ventas
 */
class SalesViewModel : ViewModel() {
    
    private val salesApiService = RetrofitClient.salesService
    
    // Estado de las ventas
    private val _salesState = MutableStateFlow<SalesState>(SalesState.Idle)
    val salesState: StateFlow<SalesState> = _salesState.asStateFlow()
    
    // Lista de ventas
    private val _sales = MutableStateFlow<List<Sale>>(emptyList())
    val sales: StateFlow<List<Sale>> = _sales.asStateFlow()
    
    // Venta seleccionada
    private val _selectedSale = MutableStateFlow<Sale?>(null)
    val selectedSale: StateFlow<Sale?> = _selectedSale.asStateFlow()
    
    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Mensaje de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * Registrar una nueva venta
     */
    fun createSale(
        userId: String,
        items: List<SaleItem>,
        paymentMethod: String? = null,
        shippingAddress: ShippingAddress? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _salesState.value = SalesState.Loading
                _errorMessage.value = null
                
                // Calcular el total
                val total = items.sumOf { it.subtotal }
                
                val request = CreateSaleRequest(
                    userId = userId,
                    items = items,
                    total = total,
                    paymentMethod = paymentMethod,
                    shippingAddress = shippingAddress
                )
                
                Log.d("SalesViewModel", "Creating sale for user: $userId, total: $total")
                
                val response = salesApiService.createSale(request)
                
                if (response.isSuccessful) {
                    val createSaleResponse = response.body()
                    if (createSaleResponse?.success == true && createSaleResponse.sale != null) {
                        _salesState.value = SalesState.Success("Venta registrada exitosamente")
                        _selectedSale.value = createSaleResponse.sale
                        Log.d("SalesViewModel", "Sale created successfully: ${createSaleResponse.sale.id}")
                    } else {
                        val error = createSaleResponse?.message ?: "Error al crear la venta"
                        _salesState.value = SalesState.Error(error)
                        _errorMessage.value = error
                        Log.e("SalesViewModel", "Error creating sale: $error")
                    }
                } else {
                    val error = "Error: ${response.code()} - ${response.message()}"
                    _salesState.value = SalesState.Error(error)
                    _errorMessage.value = error
                    Log.e("SalesViewModel", "HTTP error creating sale: $error")
                }
                
            } catch (e: Exception) {
                val error = "Error de red: ${e.message}"
                _salesState.value = SalesState.Error(error)
                _errorMessage.value = error
                Log.e("SalesViewModel", "Exception creating sale", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Obtener todas las ventas
     */
    fun getAllSales() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _salesState.value = SalesState.Loading
                _errorMessage.value = null
                
                Log.d("SalesViewModel", "Fetching all sales")
                
                val response = salesApiService.getAllSales()
                
                if (response.isSuccessful) {
                    val salesResponse = response.body()
                    if (salesResponse?.success == true) {
                        _sales.value = salesResponse.sales
                        _salesState.value = SalesState.Success("Ventas cargadas exitosamente")
                        Log.d("SalesViewModel", "Loaded ${salesResponse.sales.size} sales")
                    } else {
                        val error = "Error al cargar las ventas"
                        _salesState.value = SalesState.Error(error)
                        _errorMessage.value = error
                        Log.e("SalesViewModel", error)
                    }
                } else {
                    val error = "Error: ${response.code()} - ${response.message()}"
                    _salesState.value = SalesState.Error(error)
                    _errorMessage.value = error
                    Log.e("SalesViewModel", "HTTP error fetching sales: $error")
                }
                
            } catch (e: Exception) {
                val error = "Error de red: ${e.message}"
                _salesState.value = SalesState.Error(error)
                _errorMessage.value = error
                Log.e("SalesViewModel", "Exception fetching sales", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Obtener ventas de un usuario específico
     */
    fun getSalesByUserId(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _salesState.value = SalesState.Loading
                _errorMessage.value = null
                
                Log.d("SalesViewModel", "Fetching sales for user: $userId")
                
                val response = salesApiService.getSalesByUserId(userId)
                
                if (response.isSuccessful) {
                    val salesResponse = response.body()
                    if (salesResponse?.success == true) {
                        _sales.value = salesResponse.sales
                        _salesState.value = SalesState.Success("Ventas cargadas exitosamente")
                        Log.d("SalesViewModel", "Loaded ${salesResponse.sales.size} sales for user $userId")
                    } else {
                        val error = "Error al cargar las ventas del usuario"
                        _salesState.value = SalesState.Error(error)
                        _errorMessage.value = error
                        Log.e("SalesViewModel", error)
                    }
                } else {
                    val error = "Error: ${response.code()} - ${response.message()}"
                    _salesState.value = SalesState.Error(error)
                    _errorMessage.value = error
                    Log.e("SalesViewModel", "HTTP error fetching user sales: $error")
                }
                
            } catch (e: Exception) {
                val error = "Error de red: ${e.message}"
                _salesState.value = SalesState.Error(error)
                _errorMessage.value = error
                Log.e("SalesViewModel", "Exception fetching user sales", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Obtener una venta específica por ID
     */
    fun getSaleById(saleId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _salesState.value = SalesState.Loading
                _errorMessage.value = null
                
                Log.d("SalesViewModel", "Fetching sale: $saleId")
                
                val response = salesApiService.getSaleById(saleId)
                
                if (response.isSuccessful) {
                    val sale = response.body()
                    if (sale != null) {
                        _selectedSale.value = sale
                        _salesState.value = SalesState.Success("Venta cargada exitosamente")
                        Log.d("SalesViewModel", "Loaded sale: $saleId")
                    } else {
                        val error = "Venta no encontrada"
                        _salesState.value = SalesState.Error(error)
                        _errorMessage.value = error
                        Log.e("SalesViewModel", error)
                    }
                } else {
                    val error = "Error: ${response.code()} - ${response.message()}"
                    _salesState.value = SalesState.Error(error)
                    _errorMessage.value = error
                    Log.e("SalesViewModel", "HTTP error fetching sale: $error")
                }
                
            } catch (e: Exception) {
                val error = "Error de red: ${e.message}"
                _salesState.value = SalesState.Error(error)
                _errorMessage.value = error
                Log.e("SalesViewModel", "Exception fetching sale", e)
            } finally {
                _isLoading.value = false
            }
        }
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
        _salesState.value = SalesState.Idle
        _errorMessage.value = null
    }
}

/**
 * Estados posibles del proceso de ventas
 */
sealed class SalesState {
    object Idle : SalesState()
    object Loading : SalesState()
    data class Success(val message: String) : SalesState()
    data class Error(val message: String) : SalesState()
}
