package com.example.huertabeja.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.example.huertabeja.R
import com.example.huertabeja.utils.SessionManager
import com.example.huertabeja.viewmodel.RegisterState
import com.example.huertabeja.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var run by remember { mutableStateOf("") }
    var dv by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var runError by remember { mutableStateOf<String?>(null) }
    var dvError by remember { mutableStateOf<String?>(null) }
    var nombresError by remember { mutableStateOf<String?>(null) }
    var apellidosError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var telefonoError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    
    val registerState by viewModel.registerState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }
    
    // Observe register state
    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is RegisterState.Success -> {
                // Guardar el token automáticamente
                sessionManager.saveAuthToken(state.token)
                dialogMessage = state.message
                isSuccess = true
                showDialog = true
            }
            is RegisterState.Error -> {
                dialogMessage = state.message
                isSuccess = false
                showDialog = true
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .background(color = Color(0xFFFBF8F0))
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Image(
            painter = painterResource(id = R.drawable.logo_huertabeja),
            contentDescription = "Logo de Huertabeja",
            modifier = Modifier.size(200.dp),
            alignment = Alignment.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Crear Cuenta",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3C4522)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Fila para RUN y DV
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Campo RUN
            Column(modifier = Modifier.weight(0.6f)) {
                RegisterOutlinedTextField(
                    value = run,
                    onValueChange = {
                        // Solo permitir números
                        if (it.all { char -> char.isDigit() } && it.length <= 8) {
                            run = it
                            runError = null
                        }
                    },
                    labelText = "Run",
                    icon = Icons.Default.Person,
                    keyboardType = KeyboardType.Number,
                    isError = runError != null
                )
                
                runError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
            }
            
            // Campo DV
            Column(modifier = Modifier.weight(0.4f)) {
                RegisterOutlinedTextField(
                    value = dv,
                    onValueChange = {
                        // Permitir números y K/k
                        if (it.length <= 1 && (it.all { char -> char.isDigit() } || it.equals("K", ignoreCase = true))) {
                            dv = it.uppercase()
                            dvError = null
                        }
                    },
                    labelText = "Dv",
                    icon = Icons.Default.Person,
                    keyboardType = KeyboardType.Text,
                    isError = dvError != null
                )
                
                dvError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Campo Nombres
        RegisterOutlinedTextField(
            value = nombres,
            onValueChange = {
                nombres = it
                nombresError = null
            },
            labelText = "Nombres",
            icon = Icons.Default.Person,
            isError = nombresError != null
        )
        
        nombresError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Campo Apellidos
        RegisterOutlinedTextField(
            value = apellidos,
            onValueChange = {
                apellidos = it
                apellidosError = null
            },
            labelText = "Apellidos",
            icon = Icons.Default.Person,
            isError = apellidosError != null
        )
        
        apellidosError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Campo Email
        RegisterOutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            labelText = "Email",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            isError = emailError != null
        )
        
        emailError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Campo Teléfono
        RegisterOutlinedTextField(
            value = telefono,
            onValueChange = {
                // Solo permitir números
                if (it.all { char -> char.isDigit() } && it.length <= 12) {
                    telefono = it
                    telefonoError = null
                }
            },
            labelText = "Teléfono",
            icon = Icons.Default.Phone,
            keyboardType = KeyboardType.Phone,
            isError = telefonoError != null
        )
        
        telefonoError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Campo Contraseña
        RegisterOutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
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
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Campo Verificar Contraseña
        RegisterOutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = null
            },
            labelText = "Verificar Contraseña",
            icon = Icons.Default.Lock,
            isPassword = true,
            isError = confirmPasswordError != null
        )
        
        confirmPasswordError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Botón de Registro
        Button(
            onClick = {
                var hasError = false
                
                // Validar RUN
                if (run.isBlank()) {
                    runError = "El RUN es obligatorio."
                    hasError = true
                } else if (run.length < 7) {
                    runError = "El RUN debe tener al menos 7 dígitos."
                    hasError = true
                }
                
                // Validar DV
                if (dv.isBlank()) {
                    dvError = "El DV es obligatorio."
                    hasError = true
                }
                
                // Validar nombres
                if (nombres.isBlank()) {
                    nombresError = "Los nombres son obligatorios."
                    hasError = true
                } else if (nombres.length < 2) {
                    nombresError = "Los nombres deben tener al menos 2 caracteres."
                    hasError = true
                }
                
                // Validar apellidos
                if (apellidos.isBlank()) {
                    apellidosError = "Los apellidos son obligatorios."
                    hasError = true
                } else if (apellidos.length < 2) {
                    apellidosError = "Los apellidos deben tener al menos 2 caracteres."
                    hasError = true
                }
                
                // Validar email
                val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                if (email.isBlank()) {
                    emailError = "El email es obligatorio."
                    hasError = true
                } else if (!emailRegex.matches(email)) {
                    emailError = "Ingresa un email válido."
                    hasError = true
                }
                
                // Validar teléfono
                if (telefono.isBlank()) {
                    telefonoError = "El teléfono es obligatorio."
                    hasError = true
                } else if (telefono.length < 9) {
                    telefonoError = "El teléfono debe tener al menos 9 dígitos."
                    hasError = true
                }
                
                // Validar contraseña
                val specialCharRegex = Regex("[^A-Za-z0-9]")
                if (password.isBlank()) {
                    passwordError = "La contraseña es obligatoria."
                    hasError = true
                } else if (password.length < 8 || password.length > 12) {
                    passwordError = "La contraseña debe tener entre 8 y 12 caracteres."
                    hasError = true
                } else if (!specialCharRegex.containsMatchIn(password)) {
                    passwordError = "La contraseña debe contener al menos un carácter especial."
                    hasError = true
                }
                
                // Validar confirmación de contraseña
                if (confirmPassword.isBlank()) {
                    confirmPasswordError = "Debes verificar tu contraseña."
                    hasError = true
                } else if (password != confirmPassword) {
                    confirmPasswordError = "Las contraseñas no coinciden."
                    hasError = true
                }
                
                // Si no hay errores, procesar registro
                if (!hasError) {
                    // Llamar al ViewModel para registrar el usuario
                    viewModel.registerUser(
                        run = run,
                        dv = dv,
                        nombres = nombres,
                        apellidos = apellidos,
                        email = email,
                        telefono = telefono,
                        password = password
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8E9B6B)
            )
        ) {
            Text("Registrarse", color = Color.Black, fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Link para ir a Login
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¿Ya tienes cuenta? ",
                color = Color(0xFF3C4522)
            )
            Text(
                text = "Inicia sesión",
                color = Color(0xFF8E9B6B),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
    
    // Loading indicator
    if (registerState is RegisterState.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF8E9B6B))
        }
    }
    
    // Dialog de éxito o error
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                viewModel.resetState()
                if (isSuccess) {
                    navController.popBackStack()
                }
            },
            title = {
                Text(
                    text = if (isSuccess) "¡Éxito!" else "Error",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = dialogMessage)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        viewModel.resetState()
                        if (isSuccess) {
                            navController.popBackStack()
                        }
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

// Función para calcular el dígito verificador del RUN chileno
fun calculateDV(run: String): String {
    var sum = 0
    var multiplier = 2
    
    // Recorrer el RUN de derecha a izquierda
    for (i in run.length - 1 downTo 0) {
        sum += run[i].toString().toInt() * multiplier
        multiplier = if (multiplier == 7) 2 else multiplier + 1
    }
    
    val remainder = 11 - (sum % 11)
    
    return when (remainder) {
        11 -> "0"
        10 -> "K"
        else -> remainder.toString()
    }
}

@Composable
fun RegisterOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(labelText) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "\$labelText icon"
            )
        },
        visualTransformation = if (isPassword && !passwordVisible) 
            PasswordVisualTransformation() 
        else 
            VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType
        ),
        trailingIcon = {
            if (isPassword) {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        },
        isError = isError,
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}
