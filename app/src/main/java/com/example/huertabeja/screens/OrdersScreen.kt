package com.example.huertabeja.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.huertabeja.data.Order
import com.example.huertabeja.data.OrderItem
import com.example.huertabeja.data.OrderStatus
import com.example.huertabeja.data.toDisplayString
import com.example.huertabeja.utils.SessionManager
import com.example.huertabeja.viewmodel.OrdersViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    navController: NavController,
    ordersViewModel: OrdersViewModel = viewModel()
) {
    val uiState by ordersViewModel.uiState.collectAsState()
    val clpFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    clpFormat.maximumFractionDigits = 0
    
    // Obtener el userId del SessionManager (puedes ajustar según tu implementación)
    // val userId = SessionManager.getUserId() // Descomenta si tienes SessionManager configurado
    val userId: String? = null // Por ahora null para obtener todos los pedidos
    
    // Cargar pedidos cuando se monta el componente
    LaunchedEffect(Unit) {
        ordersViewModel.loadOrders(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8F0))
            .padding(16.dp)
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF8DA356)
                    )
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = uiState.error ?: "Error desconocido",
                            fontSize = 16.sp,
                            color = Color.Red
                        )
                        Button(
                            onClick = { ordersViewModel.refresh(userId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8DA356)
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            uiState.orders.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Sin pedidos",
                            modifier = Modifier.size(100.dp),
                            tint = Color(0xFF8DA356)
                        )
                        Text(
                            text = "No tienes pedidos realizados",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.orders) { order ->
                        OrderCard(order = order, clpFormat = clpFormat)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, clpFormat: NumberFormat) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL"))
    
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header del pedido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pedido #${order.id.take(8)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424F37)
                    )
                    Text(
                        text = dateFormat.format(order.date),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                
                OrderStatusChip(status = order.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Divider(color = Color(0xFFE0E0E0))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Lista de productos
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                order.items.forEach { item ->
                    OrderItemRow(item = item, clpFormat = clpFormat)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Divider(color = Color(0xFFE0E0E0))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424F37)
                )
                Text(
                    text = clpFormat.format(order.total),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8DA356)
                )
            }
        }
    }
}

@Composable
fun OrderStatusChip(status: OrderStatus) {
    val backgroundColor = when(status) {
        OrderStatus.PENDING -> Color(0xFFFFF9C4)
        OrderStatus.PROCESSING -> Color(0xFFB3E5FC)
        OrderStatus.SHIPPED -> Color(0xFFC5CAE9)
        OrderStatus.DELIVERED -> Color(0xFFC8E6C9)
        OrderStatus.CANCELLED -> Color(0xFFFFCDD2)
    }
    
    val textColor = when(status) {
        OrderStatus.PENDING -> Color(0xFFF57F17)
        OrderStatus.PROCESSING -> Color(0xFF0277BD)
        OrderStatus.SHIPPED -> Color(0xFF283593)
        OrderStatus.DELIVERED -> Color(0xFF2E7D32)
        OrderStatus.CANCELLED -> Color(0xFFC62828)
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = status.toDisplayString(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
fun OrderItemRow(item: OrderItem, clpFormat: NumberFormat) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            val imageUrl = if (item.product.image.startsWith("http")) {
                item.product.image
            } else {
                "https://servicio-productos.fly.dev${item.product.image}"
            }
            
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = item.product.title,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Nombre y cantidad
            Column {
                Text(
                    text = item.product.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF424F37)
                )
                Text(
                    text = "Cantidad: ${item.quantity}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        
        // Precio
        Text(
            text = clpFormat.format(item.price * item.quantity),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF424F37)
        )
    }
}
