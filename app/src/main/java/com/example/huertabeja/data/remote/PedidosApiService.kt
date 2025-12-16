package com.example.huertabeja.data.remote

import com.example.huertabeja.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface PedidosApiService {
    
    @POST("api/pedidos")
    suspend fun crearPedido(
        @Header("Authorization") token: String,
        @Body request: CrearPedidoRequest
    ): Response<PedidoResponse>
    
    @GET("api/pedidos/usuario/{usuarioId}")
    suspend fun obtenerPedidosPorUsuario(
        @Path("usuarioId") usuarioId: String,
        @Header("Authorization") token: String
    ): Response<List<Pedido>>
    
    @GET("api/pedidos/{id}")
    suspend fun obtenerPedidoPorId(
        @Path("id") pedidoId: String,
        @Header("Authorization") token: String
    ): Response<Pedido>
    
    @GET("api/pedidos")
    suspend fun obtenerTodosPedidos(
        @Header("Authorization") token: String
    ): Response<PedidosResponse>
    
    @PATCH("api/pedidos/{id}/cancelar")
    suspend fun cancelarPedido(
        @Path("id") pedidoId: String,
        @Header("Authorization") token: String,
        @Body request: CancelarPedidoRequest
    ): Response<Pedido>
    
    @PATCH("api/pedidos/{id}/estado")
    suspend fun actualizarEstado(
        @Path("id") pedidoId: String,
        @Header("Authorization") token: String,
        @Body request: ActualizarEstadoRequest
    ): Response<Pedido>
}
