package com.example.huertabeja.data.remote

import com.example.huertabeja.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface UsuariosApiService {
    
    @POST("api/usuarios/registro")
    suspend fun registrarUsuario(@Body request: RegistroRequest): Response<RegistroResponse>
    
    @POST("api/usuarios/login")
    suspend fun loginUsuario(@Body request: LoginRequest): Response<LoginResponse>
    
    @GET("api/usuarios/perfil/{id}")
    suspend fun obtenerPerfil(
        @Path("id") usuarioId: String,
        @Header("Authorization") token: String
    ): Response<Usuario>
    
    @PUT("api/usuarios/perfil/{id}")
    suspend fun actualizarPerfil(
        @Path("id") usuarioId: String,
        @Header("Authorization") token: String,
        @Body usuario: Usuario
    ): Response<Usuario>
    
    @GET("api/usuarios")
    suspend fun obtenerTodosUsuarios(
        @Header("Authorization") token: String
    ): Response<List<Usuario>>
    
    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(
        @Path("id") usuarioId: String,
        @Header("Authorization") token: String
    ): Response<Map<String, String>>
}
