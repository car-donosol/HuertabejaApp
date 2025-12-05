package com.example.huertabeja.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request para crear un nuevo producto
 */
data class CreateProductRequest(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("price")
    val price: Int,
    
    @SerializedName("price_offer")
    val priceOffer: Int = 0,
    
    @SerializedName("image")
    val image: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("stock")
    val stock: Int,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("home")
    val home: Boolean = false,
    
    @SerializedName("slug")
    val slug: String
)

/**
 * Request para actualizar un producto existente
 */
data class UpdateProductRequest(
    @SerializedName("title")
    val title: String? = null,
    
    @SerializedName("price")
    val price: Int? = null,
    
    @SerializedName("price_offer")
    val priceOffer: Int? = null,
    
    @SerializedName("image")
    val image: String? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("stock")
    val stock: Int? = null,
    
    @SerializedName("category")
    val category: String? = null,
    
    @SerializedName("home")
    val home: Boolean? = null
)

/**
 * Response genérica para operaciones de productos
 */
data class ProductResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("product")
    val product: com.example.huertabeja.data.Product? = null
)
