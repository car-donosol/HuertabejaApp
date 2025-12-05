package com.example.huertabeja.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para un pedido (adaptado al backend)
 * NOTA: El backend actualmente NO devuelve id ni created_at en las respuestas
 */
data class Sale(
    @SerializedName("cliente_id")
    val clienteId: String,
    
    @SerializedName("status")
    val status: String = "pendiente",
    
    @SerializedName("direccion")
    val direccion: String,
    
    @SerializedName("total")
    val total: Int
)

/**
 * Request para crear un nuevo pedido
 */
data class CreateSaleRequest(
    @SerializedName("cliente_id")
    val clienteId: String,
    
    @SerializedName("status")
    val status: String = "pendiente",
    
    @SerializedName("direccion")
    val direccion: String,
    
    @SerializedName("total")
    val total: Int
)

/**
 * Request para actualizar un pedido
 */
data class UpdateSaleRequest(
    @SerializedName("status")
    val status: String? = null,
    
    @SerializedName("direccion")
    val direccion: String? = null,
    
    @SerializedName("total")
    val total: Int? = null
)
