package com.example.huertabeja.data.api

import com.example.huertabeja.data.Order
import com.example.huertabeja.data.Product
import com.example.huertabeja.data.model.LoginRequest
import com.example.huertabeja.data.model.LoginResponse
import com.example.huertabeja.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Interface completa (mantiene compatibilidad con código existente)
interface ApiService {
    
    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    
    @GET("users")
    suspend fun getUsers(): Response<List<User>>
    
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") userId: String): Response<User>
}

// Interface específica para usuarios
interface UserApiService {
    
    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    
    @GET("users")
    suspend fun getUsers(): Response<List<User>>
    
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") userId: String): Response<User>
    
    @POST("users")
    suspend fun registerUser(@Body userRegisterRequest: com.example.huertabeja.data.model.UserRegisterRequest): Response<User>
}

// Interface específica para productos y pedidos
interface ProductApiService {
    @GET(".")  // El endpoint raíz devuelve todos los productos
    suspend fun getAllProducts(): Response<List<Product>>

    @GET("{slug}")  // Obtener producto por slug en lugar de ID
    suspend fun getProductBySlug(@Path("slug") slug: String): Response<Product>
    
    // Operaciones de administración de productos
    @POST(".")
    suspend fun createProduct(@Body product: com.example.huertabeja.data.model.CreateProductRequest): Response<com.example.huertabeja.data.model.ProductResponse>
    
    @retrofit2.http.PUT("{slug}")
    suspend fun updateProduct(
        @Path("slug") slug: String,
        @Body product: com.example.huertabeja.data.model.UpdateProductRequest
    ): Response<com.example.huertabeja.data.model.ProductResponse>
    
    @retrofit2.http.DELETE("{slug}")
    suspend fun deleteProduct(@Path("slug") slug: String): Response<com.example.huertabeja.data.model.ProductResponse>
}

// Interface específica para ventas
interface SalesApiService {
    
    /**
     * Registrar una nueva venta
     * POST /api/sales
     */
    @POST("api/sales")
    suspend fun createSale(@Body saleRequest: com.example.huertabeja.data.model.CreateSaleRequest): Response<com.example.huertabeja.data.model.CreateSaleResponse>
    
    /**
     * Obtener todas las ventas
     * GET /api/sales
     */
    @GET("api/sales")
    suspend fun getAllSales(): Response<com.example.huertabeja.data.model.SalesResponse>
    
    /**
     * Obtener ventas por usuario
     * GET /api/sales/user/{userId}
     */
    @GET("api/sales/user/{userId}")
    suspend fun getSalesByUserId(@Path("userId") userId: String): Response<com.example.huertabeja.data.model.SalesResponse>
    
    /**
     * Obtener una venta específica por ID
     * GET /api/sales/{id}
     */
    @GET("api/sales/{id}")
    suspend fun getSaleById(@Path("id") saleId: String): Response<com.example.huertabeja.data.model.Sale>
}