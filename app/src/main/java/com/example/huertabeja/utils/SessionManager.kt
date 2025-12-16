package com.example.huertabeja.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.huertabeja.data.model.User
import com.example.huertabeja.data.model.Usuario
import com.google.gson.Gson

class SessionManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    private val gson = Gson()
    
    companion object {
        private const val PREFS_NAME = "huertabeja_session"
        private const val KEY_USER = "user"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROL = "user_rol"
    }
    
    // ===== Métodos originales (mantienen compatibilidad) =====
    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().apply {
            putString(KEY_USER, userJson)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER, null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }
    
    // ===== Nuevos métodos para autenticación JWT =====
    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    
    fun getAuthToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }
    
    fun saveUsuario(usuario: Usuario, token: String) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_USER_ID, usuario.id)
            putString(KEY_USER_NAME, "${usuario.nombre} ${usuario.apellido}")
            putString(KEY_USER_EMAIL, usuario.email)
            putString(KEY_USER_ROL, usuario.rol)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }
    
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }
    
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }
    
    fun getUserRol(): String? {
        return prefs.getString(KEY_USER_ROL, null)
    }
    
    fun isAdmin(): Boolean {
        return getUserRol() == "admin"
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getAuthToken() != null
    }
    
    fun logout() {
        prefs.edit().apply {
            clear()
            apply()
        }
    }
    
    fun clearSession() {
        logout()
    }
}
