package com.example.huertabeja.utils

import com.example.huertabeja.data.model.User

/**
 * Utilidades para verificar permisos de administrador
 */
object AdminUtils {
    
    private const val ADMIN_DOMAIN = "@huertabeja.com"
    
    /**
     * Verifica si un usuario es administrador basándose en su email
     */
    fun isAdmin(user: User?): Boolean {
        return user?.email?.endsWith(ADMIN_DOMAIN, ignoreCase = true) == true
    }
    
    /**
     * Verifica si un email pertenece a un administrador
     */
    fun isAdminEmail(email: String?): Boolean {
        return email?.endsWith(ADMIN_DOMAIN, ignoreCase = true) == true
    }
}
