package com.example.huertabeja.navigation

sealed class AppScreens(val route: String) {
    object HomeScreen : AppScreens("home_screen")
    object LoginScreen : AppScreens("login_screen")
    object RegisterScreen : AppScreens("register_screen")
    object ProductsScreen : AppScreens("products_screen")
    object CartScreen : AppScreens("cart_screen")
    object AboutScreen : AppScreens("about_screen")
    object PerfilUserScreen : AppScreens("perfil_user_screen")
    object PaymentScreen : AppScreens("payment_screen")
    object OrdersScreen : AppScreens("orders_screen")
    object AdminScreen : AppScreens("admin_screen")
    object AddProductImageScreen : AppScreens("add_product_image_screen")
}