package com.example.huertabeja.data.model

import com.google.gson.annotations.SerializedName

data class ImageUploadResponse(
    val mensaje: String,
    val url: String,
    @SerializedName("public_id")
    val publicId: String
)

data class ImageDeleteResponse(
    val mensaje: String
)
