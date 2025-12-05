package com.example.huertabeja.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Modelo de datos para una venta
 */
data class Sale(
    @SerializedName("_id")
    val id: String? = null,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("items")
    val items: List<SaleItem>,
    
    @SerializedName("total")
    val total: Double,
    
    @SerializedName("status")
    val status: String = "PENDING",
    
    @SerializedName("payment_method")
    val paymentMethod: String? = null,
    
    @SerializedName("shipping_address")
    val shippingAddress: ShippingAddress? = null,
    
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

/**
 * Item individual de una venta
 */
data class SaleItem(
    @SerializedName("product_id")
    val productId: String,
    
    @SerializedName("product_name")
    val productName: String,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("subtotal")
    val subtotal: Double
)

/**
 * Dirección de envío
 */
data class ShippingAddress(
    @SerializedName("street")
    val street: String,
    
    @SerializedName("city")
    val city: String,
    
    @SerializedName("state")
    val state: String? = null,
    
    @SerializedName("postal_code")
    val postalCode: String,
    
    @SerializedName("country")
    val country: String = "México"
)

/**
 * Request para crear una nueva venta
 */
data class CreateSaleRequest(
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("items")
    val items: List<SaleItem>,
    
    @SerializedName("total")
    val total: Double,
    
    @SerializedName("payment_method")
    val paymentMethod: String? = null,
    
    @SerializedName("shipping_address")
    val shippingAddress: ShippingAddress? = null
)

/**
 * Response al crear una venta
 */
data class CreateSaleResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("sale")
    val sale: Sale? = null
)

/**
 * Response al consultar ventas
 */
data class SalesResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("sales")
    val sales: List<Sale>,
    
    @SerializedName("total_count")
    val totalCount: Int? = null
)
