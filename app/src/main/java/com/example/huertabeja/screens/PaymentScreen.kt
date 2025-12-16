package com.example.huertabeja.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.huertabeja.data.model.ItemPago
import com.example.huertabeja.data.model.PayerInfo
import com.example.huertabeja.data.model.AddressInfo
import com.example.huertabeja.ui.viewmodel.MercadoPagoUiState
import com.example.huertabeja.ui.viewmodel.MercadoPagoViewModel
import com.example.huertabeja.ui.viewmodel.PaymentResult
import com.example.huertabeja.utils.SessionManager
import com.example.huertabeja.viewmodel.CartUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController, 
    totalAmount: Double,
    cartUiState: CartUiState,
    mercadoPagoViewModel: MercadoPagoViewModel = viewModel(),
    onCheckout: (calle: String, ciudad: String, estado: String, codigoPostal: String, pais: String, metodoPago: String) -> Unit,
    onOrderSuccess: () -> Unit
) {
    val context = LocalContext.current
    val mpUiState by mercadoPagoViewModel.uiState.collectAsStateWithLifecycle()
    val paymentResult by mercadoPagoViewModel.paymentResult.collectAsStateWithLifecycle()
    
    // Obtener datos del usuario logueado
    val sessionManager = remember { SessionManager(context) }
    val userEmail = remember { sessionManager.getUserEmail() ?: "" }
    val userName = remember { sessionManager.getUserName() ?: "" }
    
    // Campos de dirección
    var calle by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("Chile") }
    var nombreCompleto by remember { mutableStateOf(userName) }
    var email by remember { mutableStateOf(userEmail) }

    // Validación del formulario
    val isFormComplete = calle.isNotBlank() &&
        ciudad.isNotBlank() &&
        estado.isNotBlank() &&
        codigoPostal.isNotBlank() &&
        pais.isNotBlank() &&
        nombreCompleto.isNotBlank() &&
        email.isNotBlank() &&
        email.contains("@")
    
    // Observar cuando la orden se crea exitosamente
    LaunchedEffect(cartUiState.orderCreated) {
        if (cartUiState.orderCreated) {
            onOrderSuccess()
        }
    }
    
    // Manejar resultado de Mercado Pago
    LaunchedEffect(paymentResult) {
        when (paymentResult) {
            PaymentResult.SUCCESS -> {
                Toast.makeText(context, "¡Pago exitoso!", Toast.LENGTH_LONG).show()
                onCheckout(calle, ciudad, estado, codigoPostal, pais, "mercadopago")
                mercadoPagoViewModel.resetPaymentResult()
            }
            PaymentResult.FAILURE -> {
                Toast.makeText(context, "El pago fue rechazado", Toast.LENGTH_LONG).show()
                mercadoPagoViewModel.resetPaymentResult()
            }
            PaymentResult.PENDING -> {
                Toast.makeText(context, "Pago pendiente de confirmación", Toast.LENGTH_LONG).show()
                onCheckout(calle, ciudad, estado, codigoPostal, pais, "mercadopago_pendiente")
                mercadoPagoViewModel.resetPaymentResult()
            }
            PaymentResult.NONE -> {}
        }
    }
    
    // Manejar estado de Mercado Pago
    LaunchedEffect(mpUiState) {
        when (val state = mpUiState) {
            is MercadoPagoUiState.PreferenciaCreada -> {
                // Abrir Mercado Pago en Chrome Custom Tabs
                openMercadoPago(context, state.initPoint)
                mercadoPagoViewModel.resetState()
            }
            is MercadoPagoUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                mercadoPagoViewModel.resetState()
            }
            else -> {}
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
    
    // Mostrar loading cuando se está creando la orden o procesando pago
    if (cartUiState.isCreatingOrder || mpUiState is MercadoPagoUiState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (mpUiState is MercadoPagoUiState.Loading) 
                        "Conectando con Mercado Pago..." 
                    else 
                        "Procesando pedido...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finalizar Compra") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Resumen del pedido
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumen del Pedido",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${cartUiState.products.size} productos",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Total: ${String.format("$%,.0f", totalAmount)} CLP",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Datos del comprador",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = nombreCompleto,
                onValueChange = { nombreCompleto = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Dirección de entrega",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = calle,
                onValueChange = { calle = it },
                label = { Text("Calle y Número") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = ciudad,
                    onValueChange = { ciudad = it },
                    label = { Text("Ciudad") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = estado,
                    onValueChange = { estado = it },
                    label = { Text("Región") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = codigoPostal,
                    onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 10) codigoPostal = it },
                    label = { Text("Código Postal") },
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

            // Botón de Mercado Pago
            Button(
                onClick = {
                    // Crear items para Mercado Pago
                    val items = cartUiState.products.map { (product, quantity) ->
                        ItemPago(
                            id = product.id ?: "",
                            title = product.title,
                            description = product.description,
                            pictureUrl = product.image,
                            quantity = quantity,
                            unitPrice = product.price  // CLP sin decimales
                        )
                    }
                    
                    val nombres = nombreCompleto.split(" ")
                    val payer = PayerInfo(
                        name = nombres.firstOrNull() ?: "",
                        surname = nombres.drop(1).joinToString(" "),
                        email = email,
                        address = AddressInfo(
                            streetName = calle,
                            streetNumber = "",
                            zipCode = codigoPostal
                        )
                    )
                    
                    mercadoPagoViewModel.crearPreferencia(items, payer)
                },
                enabled = isFormComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF009EE3)  // Color azul de Mercado Pago
                )
            ) {
                Icon(
                    Icons.Default.Payment,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "PAGAR CON MERCADO PAGO",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = "Serás redirigido a Mercado Pago para completar el pago de forma segura",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Separador
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    "  o  ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            
            // Botón alternativo (pago manual/simulado)
            OutlinedButton(
                onClick = {
                    onCheckout(calle, ciudad, estado, codigoPostal, pais, "tarjeta")
                },
                enabled = isFormComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    Icons.Default.CreditCard,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Pago manual (demo)")
            }
        }
    }
}

/**
 * Abre Mercado Pago en Chrome Custom Tabs
 */
private fun openMercadoPago(context: Context, url: String) {
    try {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        
        customTabsIntent.launchUrl(context, Uri.parse(url))
    } catch (e: Exception) {
        Toast.makeText(
            context, 
            "Error al abrir Mercado Pago: ${e.message}", 
            Toast.LENGTH_LONG
        ).show()
    }
}
