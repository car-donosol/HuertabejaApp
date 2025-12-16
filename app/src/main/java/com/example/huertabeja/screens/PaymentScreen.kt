package com.example.huertabeja.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.huertabeja.viewmodel.CartUiState

@Composable
fun PaymentScreen(
    navController: NavController, 
    totalAmount: Double,
    cartUiState: CartUiState,
    onCheckout: (calle: String, ciudad: String, estado: String, codigoPostal: String, pais: String, metodoPago: String) -> Unit,
    onOrderSuccess: () -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    
    // Campos de dirección
    var calle by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("Chile") }

    // Validación simple para el formulario
    val isFormComplete = cardNumber.length == 16 && 
        expiryDate.matches(Regex("\\d{2}/\\d{2}")) && 
        cvv.length == 3 && 
        cardHolder.isNotBlank() &&
        calle.isNotBlank() &&
        ciudad.isNotBlank() &&
        estado.isNotBlank() &&
        codigoPostal.isNotBlank() &&
        pais.isNotBlank()
    
    // Observar cuando la orden se crea exitosamente
    LaunchedEffect(cartUiState.orderCreated) {
        if (cartUiState.orderCreated) {
            onOrderSuccess()
        }
    }
    
    // Mostrar diálogo de error si hay alguno
    cartUiState.orderError?.let { error ->
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Error al procesar el pedido") },
            text = { Text(error) },
            confirmButton = {
                Button(onClick = { navController.popBackStack() }) {
                    Text("OK")
                }
            }
        )
    }
    
    // Mostrar loading cuando se está creando la orden
    if (cartUiState.isCreatingOrder) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Introduce los datos de tu tarjeta", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Dirección de entrega", style = MaterialTheme.typography.titleMedium)
        
        OutlinedTextField(
            value = calle,
            onValueChange = { calle = it },
            label = { Text("Calle y Número") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = ciudad,
                onValueChange = { ciudad = it },
                label = { Text("Ciudad") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = estado,
                onValueChange = { estado = it },
                label = { Text("Estado/Región") },
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = codigoPostal,
                onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 10) codigoPostal = it },
                label = { Text("Código Postal") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = pais,
                onValueChange = { pais = it },
                label = { Text("País") },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Datos de pago", style = MaterialTheme.typography.titleMedium)
        
        OutlinedTextField(
            value = cardHolder,
            onValueChange = { cardHolder = it },
            label = { Text("Titular de la tarjeta") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = cardNumber,
            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 16) cardNumber = it },
            label = { Text("Número de la tarjeta (16 dígitos)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = expiryDate,
                onValueChange = { expiryDate = it },
                label = { Text("MM/AA") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = cvv,
                onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 3) cvv = it },
                label = { Text("CVV (3 dígitos)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Subtotal: ${String.format("$%.2f", totalAmount)}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                onCheckout(calle, ciudad, estado, codigoPostal, pais, "tarjeta")
            },
            enabled = isFormComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("PAGAR AHORA", fontSize = 16.sp)
        }
    }
}
