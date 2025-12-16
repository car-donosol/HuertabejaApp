package com.example.huertabeja.data.model

import com.google.gson.annotations.SerializedName

data class Producto(
    @SerializedName("_id")
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val categoria: String,
    val stock: Int,
    val imagenes: List<String>?,
    val marca: String?,
    val descuento: Int = 0,
    val disponible: Boolean,
    val valoracion: Valoracion?
)

data class Valoracion(
    val promedio: Double,
    val numeroReviews: Int
)

data class ProductosResponse(
    val productos: List<Producto>,
    val paginaActual: Int,
    val totalPaginas: Int,
    val totalProductos: Int
)

data class CrearProductoRequest(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val categoria: String,
    val stock: Int,
    val imagenes: List<String>?,
    val marca: String?,
    val descuento: Int = 0,
    val disponible: Boolean = true
)

data class ActualizarProductoRequest(
    val nombre: String?,
    val descripcion: String?,
    val precio: Double?,
    val categoria: String?,
    val stock: Int?,
    val imagenes: List<String>?,
    val marca: String?,
    val descuento: Int?,
    val disponible: Boolean?
)
