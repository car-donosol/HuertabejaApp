package com.example.huertabeja.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.api.RetrofitClient
import com.example.huertabeja.data.model.CreateSaleRequest
import com.example.huertabeja.data.model.UpdateSaleRequest
import com.example.huertabeja.data.model.Sale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar las operaciones de ventas/pedidos
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
     * Crear un nuevo pedido
     */
    fun createSale(
        clienteId: String,
        direccion: String,
        total: Int,
        status: String = "pendiente"
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _salesState.value = SalesState.Loading
                _errorMessage.value = null
                
                val request = CreateSaleRequest(
                    clienteId = clienteId,
                    direccion = direccion,
                    total = total,
                    status = status
                )
                
                Log.d("SalesViewModel", "Creating sale for cliente: $clienteId, total: $total")
                
                val response = salesApiService.createSale(request)
                
                if (response.isSuccessful) {
                    val sale = response.body()
                    if (sale != null) {
                        _salesState.value = SalesState.Success("Pedido creado exitosamente")
                        _selectedSale.value = sale
                        Log.d("SalesViewModel", "Sale created successfully")
                    } else {
                        val error = "Error al crear el pedido"
                        _salesState.value = SalesState.Error(error)
                        _errorMessage.value = error
                        Log.e("SalesViewModel", error)
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
     * Obtener todos los pedidos
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
                    val salesList = response.body()
                    if (salesList != null) {
                        _sales.value = salesList
                        _salesState.value = SalesState.Success("Pedidos cargados exitosamente")
                        Log.d("SalesViewModel", "Loaded ${salesList.size} sales")
                    } else {
                        val error = "Error al cargar los pedidos"
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
     * Obtener pedidos de un cliente específico
     */
    fun getSalesByClienteId(clienteId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _salesState.value = SalesState.Loading
                _errorMessage.value = null
                
                Log.d("SalesViewModel", "Fetching sales for cliente: $clienteId")
                
                val response = salesApiService.getSalesByClienteId(clienteId)
                
                if (response.isSuccessful) {
                    val salesList = response.body()
                    if (salesList != null) {
                        _sales.value = salesList
                        _salesState.value = SalesState.Success("Pedidos cargados exitosamente")
                        Log.d("SalesViewModel", "Loaded ${salesList.size} sales for cliente $clienteId")
                    } else {
                        val error = "Error al cargar los pedidos del cliente"
                        _salesState.value = SalesState.Error(error)
                        _errorMessage.value = error
                        Log.e("SalesViewModel", error)
                    }
                } else {
                    val error = "Error: ${response.code()} - ${response.message()}"
                    _salesState.value = SalesState.Error(error)
                    _errorMessage.value = error
                    Log.e("SalesViewModel", "HTTP error fetching cliente sales: $error")
                }
                
            } catch (e: Exception) {
                val error = "Error de red: ${e.message}"
                _salesState.value = SalesState.Error(error)
                _errorMessage.value = error
                Log.e("SalesViewModel", "Exception fetching cliente sales", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Obtener un pedido específico por ID
     */
    fun getSaleById(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _salesState.value = SalesState.Loading
                _errorMessage.value = null
                
                Log.d("SalesViewModel", "Fetching sale: $id")
                
                val response = salesApiService.getSaleById(id)
                
                if (response.isSuccessful) {
                    val sale = response.body()
                    if (sale != null) {
                        _selectedSale.value = sale
                        _salesState.value = SalesState.Success("Pedido cargado exitosamente")
                        Log.d("SalesViewModel", "Loaded sale: $id")
                    } else {
                        val error = "Pedido no encontrado"
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
     * Actualizar un pedido
     */
    fun updateSale(
        id: Int,
        status: String? = null,
        direccion: String? = null,
        total: Int? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _salesState.value = SalesState.Loading
                _errorMessage.value = null
                
                val request = UpdateSaleRequest(
                    status = status,
                    direccion = direccion,
                    total = total
                )
                
                Log.d("SalesViewModel", "Updating sale: $id")
                
                val response = salesApiService.updateSale(id, request)
                
                if (response.isSuccessful) {
                    val sale = response.body()
                    if (sale != null) {
                        _salesState.value = SalesState.Success("Pedido actualizado exitosamente")
                        _selectedSale.value = sale
                        Log.d("SalesViewModel", "Sale updated successfully")
                    } else {
                        val error = "Error al actualizar el pedido"
                        _salesState.value = SalesState.Error(error)
                        _errorMessage.value = error
                        Log.e("SalesViewModel", error)
                    }
                } else {
                    val error = "Error: ${response.code()} - ${response.message()}"
                    _salesState.value = SalesState.Error(error)
                    _errorMessage.value = error
                    Log.e("SalesViewModel", "HTTP error updating sale: $error")
                }
                
            } catch (e: Exception) {
                val error = "Error de red: ${e.message}"
                _salesState.value = SalesState.Error(error)
                _errorMessage.value = error
                Log.e("SalesViewModel", "Exception updating sale", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Eliminar un pedido
     */
    fun deleteSale(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _salesState.value = SalesState.Loading
                _errorMessage.value = null
                
                Log.d("SalesViewModel", "Deleting sale: $id")
                
                val response = salesApiService.deleteSale(id)
                
                if (response.isSuccessful) {
                    _salesState.value = SalesState.Success("Pedido eliminado exitosamente")
                    _selectedSale.value = null
                    Log.d("SalesViewModel", "Sale deleted successfully")
                } else {
                    val error = "Error: ${response.code()} - ${response.message()}"
                    _salesState.value = SalesState.Error(error)
                    _errorMessage.value = error
                    Log.e("SalesViewModel", "HTTP error deleting sale: $error")
                }
                
            } catch (e: Exception) {
                val error = "Error de red: ${e.message}"
                _salesState.value = SalesState.Error(error)
                _errorMessage.value = error
                Log.e("SalesViewModel", "Exception deleting sale", e)
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

