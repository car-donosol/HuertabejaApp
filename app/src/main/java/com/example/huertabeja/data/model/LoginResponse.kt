package com.example.huertabeja.data.model

import com.google.gson.annotations.SerializedName

// El backend devuelve directamente el usuario con el token incluido
data class LoginResponse(
    @SerializedName("_id")
    val id: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String? = null,
    val direccion: Direccion? = null,
    val rol: String,
    val token: String
)
