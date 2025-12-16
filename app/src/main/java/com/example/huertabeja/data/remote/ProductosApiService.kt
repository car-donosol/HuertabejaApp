package com.example.huertabeja.data.remote

import com.example.huertabeja.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ProductosApiService {
    
    @GET("api/productos")
    suspend fun obtenerProductos(
        @Query("pagina") pagina: Int = 1,
        @Query("limite") limite: Int = 10,
        @Query("categoria") categoria: String? = null,
        @Query("disponible") disponible: Boolean? = null,
        @Query("buscar") buscar: String? = null
    ): Response<ProductosResponse>
    
    @GET("api/productos/{id}")
    suspend fun obtenerProductoPorId(@Path("id") productoId: String): Response<Producto>
    
    @GET("api/productos/categoria/{categoria}")
    suspend fun obtenerProductosPorCategoria(
        @Path("categoria") categoria: String
    ): Response<List<Producto>>
    
    @POST("api/productos")
    suspend fun crearProducto(
        @Header("Authorization") token: String,
        @Body producto: CrearProductoRequest
    ): Response<Producto>
    
    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") productoId: String,
        @Header("Authorization") token: String,
        @Body producto: ActualizarProductoRequest
    ): Response<Producto>
    
    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(
        @Path("id") productoId: String,
        @Header("Authorization") token: String
    ): Response<Map<String, String>>
    
    @GET("api/productos/buscar")
    suspend fun buscarProductos(
        @Query("q") query: String
    ): Response<List<Producto>>
    
    @Multipart
    @POST("api/productos/upload")
    suspend fun subirImagen(
        @Part imagen: MultipartBody.Part
    ): Response<ImageUploadResponse>
    
    @DELETE("api/productos/upload/{public_id}")
    suspend fun eliminarImagen(
        @Path("public_id") publicId: String
    ): Response<ImageDeleteResponse>
}
