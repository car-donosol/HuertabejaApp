package com.example.huertabeja.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.model.*
import com.example.huertabeja.data.repository.PedidosRepository
import kotlinx.coroutines.launch

class PedidosViewModel : ViewModel() {
    
    private val repository = PedidosRepository()
    
    private val _pedidos = MutableLiveData<Result<List<Pedido>>>()
    val pedidos: LiveData<Result<List<Pedido>>> = _pedidos
    
    private val _pedidoDetalle = MutableLiveData<Result<Pedido>>()
    val pedidoDetalle: LiveData<Result<Pedido>> = _pedidoDetalle
    
    private val _crearPedidoResult = MutableLiveData<Result<PedidoResponse>>()
    val crearPedidoResult: LiveData<Result<PedidoResponse>> = _crearPedidoResult
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun crearPedido(token: String, request: CrearPedidoRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.crearPedido(token, request)
            _crearPedidoResult.value = result
            _isLoading.value = false
        }
    }
    
    fun obtenerPedidosPorUsuario(usuarioId: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.obtenerPedidosPorUsuario(usuarioId, token)
            _pedidos.value = result
            _isLoading.value = false
        }
    }
    
    fun obtenerPedidoPorId(pedidoId: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.obtenerPedidoPorId(pedidoId, token)
            _pedidoDetalle.value = result
            _isLoading.value = false
        }
    }
    
    fun obtenerTodosPedidos(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.obtenerTodosPedidos(token)
            _pedidos.value = result
            _isLoading.value = false
        }
    }
    
    fun cancelarPedido(pedidoId: String, token: String, motivo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.cancelarPedido(pedidoId, token, motivo)
            _pedidoDetalle.value = result
            _isLoading.value = false
        }
    }
    
    fun actualizarEstado(pedidoId: String, token: String, estado: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.actualizarEstado(pedidoId, token, estado)
            _pedidoDetalle.value = result
            _isLoading.value = false
        }
    }
}
