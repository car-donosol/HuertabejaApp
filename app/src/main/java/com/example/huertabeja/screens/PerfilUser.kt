package com.example.huertabeja.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.huertabeja.R
import com.example.huertabeja.navigation.AppScreens
import com.example.huertabeja.ui.viewmodel.PerfilUiState
import com.example.huertabeja.ui.viewmodel.PerfilViewModel
import com.example.huertabeja.utils.SessionManager
import com.example.huertabeja.utils.AdminUtils
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: PerfilViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val localUser = sessionManager.getUser()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Si no hay usuario en sesión, redirigir al login
    LaunchedEffect(localUser) {
        if (localUser == null) {
            navController.navigate(AppScreens.LoginScreen.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        } else {
            // Cargar datos del usuario desde la API
            viewModel.loadUserProfile(localUser.id ?: "")
        }
    }
    
    // Manejar errores
    LaunchedEffect(uiState) {
        if (uiState is PerfilUiState.Error) {
            Toast.makeText(
                context,
                (uiState as PerfilUiState.Error).message,
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    // Obtener datos del usuario (de la API o del local como fallback)
    val displayUser = when (val state = uiState) {
        is PerfilUiState.Success -> state.user
        else -> localUser
    }
    
    // Formatear fecha de registro
    val formattedDate = displayUser?.fechareg?.let { dateString ->
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { "Miembro desde: ${outputFormat.format(it)}" } ?: "Miembro desde $dateString"
        } catch (e: Exception) {
            "Miembro desde: $dateString"
        }
    } ?: "Miembro desde: --/--/----"
    
    val userName = displayUser?.pnombre ?: "Usuario"
    val userEmail = displayUser?.email ?: "email@example.com"
    val isLoading = uiState is PerfilUiState.Loading
    
    // Verificar si el usuario es administrador
    val isAdmin = AdminUtils.isAdmin(displayUser)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFBF8F0))
            .padding(16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(color = Color(0xFFDED8CA)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Imagen de perfil
        Image(
            painter = painterResource(id = R.drawable.emoji), // Usando el logo como placeholder
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nombre de usuario
        Text(
            text = userName,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Correo electrónico del usuario
        Text(
            text = userEmail,
            fontSize = 18.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = formattedDate,
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.weight(1f))

        // Botones de acción
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Botón de Pedidos
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFC4BEB0))
                        .clickable {
                            navController.navigate(AppScreens.OrdersScreen.route)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📦",
                        fontSize = 32.sp,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mis pedidos",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Botón de Administración (solo para usuarios con @huertabeja.com)
            if (isAdmin) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF4CAF50))
                            .clickable {
                                navController.navigate(AppScreens.AdminScreen.route)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚙️",
                            fontSize = 32.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Admin",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        // Botón de Cerrar Sesión
        Button(
            onClick = {
                navController.navigate(AppScreens.LoginScreen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text("Cerrar Sesión", color = Color.White)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
