package com.example.huertabeja.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.huertabeja.screens.*
import com.example.huertabeja.viewmodel.CartViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val cartViewModel: CartViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                CartViewModel(context.applicationContext as android.app.Application)
            }
        }
    )
    val cartUiState by cartViewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Cart,
        BottomNavItem.Orders,
        BottomNavItem.Login,
        BottomNavItem.About
    )

    val screenTitle = when (currentRoute) {
        AppScreens.HomeScreen.route -> "Inicio"
        AppScreens.ProductsScreen.route -> "Nuestros Productos"
        AppScreens.CartScreen.route -> "Carrito de Compras"
        AppScreens.AboutScreen.route -> "Sobre Nosotros"
        AppScreens.LoginScreen.route -> "Perfil"
        AppScreens.OrdersScreen.route -> "Mis Pedidos"
        AppScreens.AdminScreen.route -> "Administración de Productos"
        else -> "Huertabeja"
    }

    Scaffold(
        topBar = {
            if (currentRoute != AppScreens.LoginScreen.route && 
                currentRoute != AppScreens.RegisterScreen.route &&
                currentRoute != AppScreens.HomeScreen.route) {
                TopAppBar(
                    title = { Text(screenTitle) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF8DA356),
                        titleContentColor = Color.White
                    ),
                    navigationIcon = {
                        if (currentRoute != AppScreens.HomeScreen.route) {
                            IconButton(onClick = { 
                                if (navController.previousBackStackEntry != null) {
                                    navController.popBackStack()
                                } else {
                                    navController.navigate(AppScreens.HomeScreen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (currentRoute != AppScreens.LoginScreen.route && currentRoute != AppScreens.RegisterScreen.route) {
                NavigationBar(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    containerColor = Color(0xFFF0F4E3)
                ) {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF8E9B6B),
                                unselectedIconColor = Color.Gray,
                                selectedTextColor = Color(0xFF8E9B6B),
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreens.LoginScreen.route,
            modifier = Modifier
                .padding(innerPadding)
        ) {
            composable(route = AppScreens.HomeScreen.route) {
                HomeScreen(navController)
            }
            composable(route = AppScreens.LoginScreen.route) {
                LoginScreen(navController)
            }
            composable(route = AppScreens.RegisterScreen.route) {
                RegisterScreen(navController)
            }
            composable(route = AppScreens.ProductsScreen.route) {
                ProductsScreen(navController, cartViewModel)
            }
            composable(route = AppScreens.CartScreen.route) {
                CartScreen(navController, cartViewModel)
            }
            composable(route = AppScreens.AboutScreen.route) {
                AboutScreen(navController)
            }
            composable(route = AppScreens.PerfilUserScreen.route){
                PerfilScreen(navController)
            }
            composable(route = AppScreens.PaymentScreen.route){
                PaymentScreen(
                    navController = navController,
                    totalAmount = cartUiState.totalPrice,
                    cartUiState = cartUiState,
                    onCheckout = { calle, ciudad, estado, codigoPostal, pais, metodoPago ->
                        val direccionCompleta = com.example.huertabeja.data.model.Direccion(
                            calle = calle,
                            ciudad = ciudad,
                            estado = estado,
                            codigoPostal = codigoPostal,
                            pais = pais
                        )
                        cartViewModel.createOrder(direccionCompleta, metodoPago)
                    },
                    onOrderSuccess = {
                        cartViewModel.resetOrderState()
                        navController.navigate(AppScreens.OrdersScreen.route) {
                            popUpTo(AppScreens.HomeScreen.route)
                        }
                    }
                )
            }
            composable(route = AppScreens.OrdersScreen.route) {
                OrdersScreen(navController)
            }
            composable(route = AppScreens.AdminScreen.route) {
                AdminScreen(navController)
            }
        }
    }
}
