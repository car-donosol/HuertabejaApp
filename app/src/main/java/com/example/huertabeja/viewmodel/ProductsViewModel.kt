package com.example.huertabeja.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.Product
import com.example.huertabeja.data.remote.ApiConfig
import com.example.huertabeja.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProductsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    private val productApiService = ApiConfig.getProductosService()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Railway backend devuelve ProductosResponse, no List<Product> directamente
                val response = productApiService.obtenerProductos(
                    pagina = 1,
                    limite = 100, // Obtener muchos productos para mostrar
                    disponible = true
                )

                if (response.isSuccessful) {
                    val productosResponse = response.body()
                    // Convertir Producto (Railway) a Product (app)
                    val products = productosResponse?.productos?.map { producto ->
                        Product(
                            id = producto.id,
                            slug = producto.id, // Usar ID como slug
                            title = producto.nombre,
                            price = producto.precio.toInt(),
                            price_offer = (producto.precio * (1 - producto.descuento / 100.0)).toInt(),
                            image = producto.imagenes?.firstOrNull() ?: "",
                            description = producto.descripcion,
                            stock = producto.stock,
                            category = producto.categoria,
                            home = producto.disponible
                        )
                    } ?: emptyList()
                    
                    _uiState.value = _uiState.value.copy(
                        products = products,
                        isLoading = false,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al cargar productos: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun refresh() {
        loadProducts()
    }
}
