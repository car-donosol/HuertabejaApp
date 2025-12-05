package com.example.huertabeja.data.api

import com.example.huertabeja.data.Order
import com.example.huertabeja.data.Product
import com.example.huertabeja.data.model.LoginRequest
import com.example.huertabeja.data.model.LoginResponse
import com.example.huertabeja.data.model.User
import com.example.huertabeja.data.model.UpdateProductResponse
import com.example.huertabeja.data.model.DeleteProductResponse
import com.example.huertabeja.data.model.Sale
import com.example.huertabeja.data.model.CreateSaleRequest
import com.example.huertabeja.data.model.UpdateSaleRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT
import retrofit2.http.DELETE

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

    @GET("{slug}")  // Obtener producto por slug
    suspend fun getProductBySlug(@Path("slug") slug: String): Response<Product>
    
    // Operaciones de administración de productos
    // Nota: El backend usa rutas específicas /add, /update, /delete
    @POST("add")
    suspend fun createProduct(@Body product: com.example.huertabeja.data.model.CreateProductRequest): Response<Product>
    
    @retrofit2.http.PUT("update/{slug}")
    suspend fun updateProduct(
        @Path("slug") slug: String,
        @Body product: com.example.huertabeja.data.model.UpdateProductRequest
    ): Response<UpdateProductResponse>
    
    @retrofit2.http.DELETE("delete/{slug}")
    suspend fun deleteProduct(@Path("slug") slug: String): Response<DeleteProductResponse>
}

// Interface específica para ventas/pedidos
interface SalesApiService {
    
    /**
     * Obtener todos los pedidos
     * GET /
     */
    @GET(".")
    suspend fun getAllSales(): Response<List<Sale>>
    
    /**
     * Crear un nuevo pedido
     * POST /
     */
    @POST(".")
    suspend fun createSale(@Body saleRequest: CreateSaleRequest): Response<Sale>
    
    /**
     * Obtener un pedido por ID
     * GET /{id}
     */
    @GET("{id}")
    suspend fun getSaleById(@Path("id") id: Int): Response<Sale>
    
    /**
     * Obtener pedidos de un cliente
     * GET /cliente/{clienteId}
     */
    @GET("cliente/{clienteId}")
    suspend fun getSalesByClienteId(@Path("clienteId") clienteId: String): Response<List<Sale>>
    
    /**
     * Actualizar un pedido
     * PUT /{id}
     */
    @PUT("{id}")
    suspend fun updateSale(@Path("id") id: Int, @Body updateRequest: UpdateSaleRequest): Response<Sale>
    
    /**
     * Eliminar un pedido
     * DELETE /{id}
     */
    @DELETE("{id}")
    suspend fun deleteSale(@Path("id") id: Int): Response<Unit>
}