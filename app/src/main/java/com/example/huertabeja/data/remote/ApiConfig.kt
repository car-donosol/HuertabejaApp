package com.example.huertabeja.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    // ⚠️ CONFIGURACIÓN: Backend desplegado en Railway
    // Cambia USE_LOCAL_BACKEND a true si necesitas usar backend local para pruebas
    
    private const val USE_LOCAL_BACKEND = false  // false = usa Railway (PRODUCCIÓN)
    
    // URLs del backend LOCAL (solo para desarrollo local)
    private const val LOCAL_IP = "192.168.100.123"  // Tu IP local
    private const val BASE_URL_USUARIOS_LOCAL = "http://$LOCAL_IP:3001/"
    private const val BASE_URL_PRODUCTOS_LOCAL = "http://$LOCAL_IP:3002/"
    private const val BASE_URL_PEDIDOS_LOCAL = "http://$LOCAL_IP:3003/"
    
    // URLs del backend en RAILWAY (PRODUCCIÓN) ✅
    private const val BASE_URL_USUARIOS_REMOTE = "https://usuarios-service-production-7145.up.railway.app/"
    private const val BASE_URL_PRODUCTOS_REMOTE = "https://productos-service-production.up.railway.app/"
    private const val BASE_URL_PEDIDOS_REMOTE = "https://pedidos-service-production.up.railway.app/"
    private const val BASE_URL_PAGOS_REMOTE = "https://pagos-service-production.up.railway.app/"  // Mercado Pago
    
    // Selección automática según configuración
    private val BASE_URL_USUARIOS = if (USE_LOCAL_BACKEND) BASE_URL_USUARIOS_LOCAL else BASE_URL_USUARIOS_REMOTE
    private val BASE_URL_PRODUCTOS = if (USE_LOCAL_BACKEND) BASE_URL_PRODUCTOS_LOCAL else BASE_URL_PRODUCTOS_REMOTE
    private val BASE_URL_PEDIDOS = if (USE_LOCAL_BACKEND) BASE_URL_PEDIDOS_LOCAL else BASE_URL_PEDIDOS_REMOTE
    private val BASE_URL_PAGOS = BASE_URL_PAGOS_REMOTE  // Siempre Railway para pagos
    
    private fun getOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    fun getUsuariosService(): UsuariosApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_USUARIOS)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UsuariosApiService::class.java)
    }
    
    fun getProductosService(): ProductosApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_PRODUCTOS)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductosApiService::class.java)
    }
    
    fun getPedidosService(): PedidosApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_PEDIDOS)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PedidosApiService::class.java)
    }
    
    fun getPagosService(): PagosApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_PAGOS)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PagosApiService::class.java)
    }
}
