package com.example.huertabeja.data.model

import com.google.gson.annotations.SerializedName

data class Pedido(
    @SerializedName("_id")
    val id: String,
    val usuarioId: String,
    val productos: List<ProductoPedido>,
    val direccionEntrega: Direccion,
    val total: Double,
    val estado: String,
    val metodoPago: String,
    val estadoPago: String,
    val fechaPedido: String,
    val fechaEntregaEstimada: String?
)

data class ProductoPedido(
    val productoId: String,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val subtotal: Double
)

data class CrearPedidoRequest(
    val usuarioId: String,
    val productos: List<ProductoCarrito>,
    val direccionEntrega: Direccion,
    val metodoPago: String,
    val notas: String?
)

data class ProductoCarrito(
    val productoId: String,
    val cantidad: Int
)

data class ActualizarEstadoRequest(
    val estado: String
)

data class CancelarPedidoRequest(
    val motivo: String
)

data class PedidoResponse(
    val mensaje: String,
    val pedido: Pedido
)

data class PedidosResponse(
    val pedidos: List<Pedido>,
    val paginaActual: Int,
    val totalPaginas: Int,
    val totalPedidos: Int
)
