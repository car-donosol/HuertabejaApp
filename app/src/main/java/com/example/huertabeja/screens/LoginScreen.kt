package com.example.huertabeja.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.huertabeja.R
import com.example.huertabeja.navigation.AppScreens
import com.example.huertabeja.ui.viewmodel.LoginUiState
import com.example.huertabeja.ui.viewmodel.LoginViewModel
import com.example.huertabeja.utils.SessionManager

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var rememberMe by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is LoginUiState.Success -> {
                sessionManager.saveUsuario(state.usuario, state.token)
                
                Toast.makeText(
                    context,
                    "Bienvenido, ${state.usuario.nombre}!",
                    Toast.LENGTH_SHORT
                ).show()
                
                // Navegar a home y limpiar backstack
                navController.navigate(AppScreens.HomeScreen.route) {
                    popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                }
                
                viewModel.resetState()
            }
            is LoginUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .background(color = Color(0xFFFBF8F0))
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.logo_huertabeja),
            contentDescription = "Logo de Huertabeja",
            modifier = Modifier.size(300.dp),
            alignment = Alignment.BottomEnd
        )
        Spacer(modifier = Modifier.height(16.dp))
        customOutlinedTextField(
            value = email,
            onValueChange = { email = it },
            labelText = "Email",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(16.dp))
        customOutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null // Clear error on change
            },
            labelText = "Contraseña",
            icon = Icons.Default.Lock,
            isPassword = true,
            isError = passwordError != null
        )

        passwordError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "¿Olvidaste tu contraseña?",
            color = Color(0xFF3C4522),
            modifier = Modifier.clickable {
                // Acción al hacer clic. Por ejemplo, navegar a una pantalla de recuperación.
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Checkbox para recordar sesión
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF8E9B6B),
                    uncheckedColor = Color(0xFF3C4522)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Recordar mi sesión",
                color = Color(0xFF3C4522)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                // Validación local primero
                val specialCharRegex = Regex("[^A-Za-z0-9]")
                if (email.isBlank()) {
                    Toast.makeText(context, "Por favor ingresa tu email", Toast.LENGTH_SHORT).show()
                } else if (password.length < 8 || password.length > 12) {
                    passwordError = "La contraseña debe tener entre 8 y 12 caracteres."
                } else if (!specialCharRegex.containsMatchIn(password)) {
                    passwordError = "La contraseña debe contener al menos un carácter especial."
                } else {
                    passwordError = null
                    // Llamar a la API
                    viewModel.login(email, password)
                }
            },
            modifier = Modifier.width(200.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8E9B6B) // Verde claro
            ),
            enabled = uiState !is LoginUiState.Loading
        ) {
            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator(
                    color = Color.Black,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Iniciar sesión", color = Color.Black)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Link para ir a Registro
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¿No tienes cuenta? ",
                color = Color(0xFF3C4522)
            )
            Text(
                text = "Regístrate",
                color = Color(0xFF8E9B6B),
                modifier = Modifier.clickable {
                    navController.navigate(AppScreens.RegisterScreen.route)
                }
            )
        }

        Spacer(modifier = Modifier.weight(2.5f))
    }
}

@Composable
fun customOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(labelText) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "$labelText icon"
            )
        },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        trailingIcon = {
            if (isPassword) {
                val image = if (passwordVisible)
                    R.drawable.hide
                else
                    R.drawable.visible

                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painter = painterResource(id = image), contentDescription = description)
                }
            }
        },
        isError = isError
    )
}
