package com.example.huertabeja.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.model.ProductosResponse
import com.example.huertabeja.data.model.Producto
import com.example.huertabeja.data.repository.ProductosRepository
import kotlinx.coroutines.launch

class ProductosViewModel : ViewModel() {
    
    private val repository = ProductosRepository()
    
    private val _productos = MutableLiveData<Result<ProductosResponse>>()
    val productos: LiveData<Result<ProductosResponse>> = _productos
    
    private val _productoDetalle = MutableLiveData<Result<Producto>>()
    val productoDetalle: LiveData<Result<Producto>> = _productoDetalle
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun obtenerProductos(
        pagina: Int = 1,
        limite: Int = 10,
        categoria: String? = null,
        disponible: Boolean? = null,
        buscar: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.obtenerProductos(pagina, limite, categoria, disponible, buscar)
            _productos.value = result
            _isLoading.value = false
        }
    }
    
    fun obtenerProductoPorId(productoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.obtenerProductoPorId(productoId)
            _productoDetalle.value = result
            _isLoading.value = false
        }
    }
    
    fun obtenerProductosPorCategoria(categoria: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.obtenerProductosPorCategoria(categoria)
            result.onSuccess { listaProductos ->
                // Convertir lista a ProductosResponse
                val response = ProductosResponse(
                    productos = listaProductos,
                    paginaActual = 1,
                    totalPaginas = 1,
                    totalProductos = listaProductos.size
                )
                _productos.value = Result.success(response)
            }.onFailure { error ->
                _productos.value = Result.failure(error)
            }
            _isLoading.value = false
        }
    }
    
    fun buscarProductos(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.buscarProductos(query)
            result.onSuccess { listaProductos ->
                val response = ProductosResponse(
                    productos = listaProductos,
                    paginaActual = 1,
                    totalPaginas = 1,
                    totalProductos = listaProductos.size
                )
                _productos.value = Result.success(response)
            }.onFailure { error ->
                _productos.value = Result.failure(error)
            }
            _isLoading.value = false
        }
    }
}
