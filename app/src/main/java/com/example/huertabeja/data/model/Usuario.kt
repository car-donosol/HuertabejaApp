package com.example.huertabeja.data.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("_id")
    val id: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String?,
    val direccion: Direccion?,
    val rol: String,
    val token: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class Direccion(
    val calle: String,
    val ciudad: String,
    val estado: String,
    val codigoPostal: String,
    val pais: String
)

data class RegistroRequest(
    val nombre: String,
    val apellido: String,
    val email: String,
    val password: String,
    val telefono: String?,
    val direccion: Direccion?
)

data class RegistroResponse(
    val mensaje: String?,
    val token: String,
    val usuario: Usuario?
)
