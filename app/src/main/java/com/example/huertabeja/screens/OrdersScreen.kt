package com.example.huertabeja.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

// Colores del tema
private val PrimaryGreen = Color(0xFF8DA356)
private val DarkGreen = Color(0xFF424F37)
private val LightGreen = Color(0xFFE8F0D8)
private val BackgroundColor = Color(0xFFFAF8F5)
private val CardColor = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    navController: NavController,
    ordersViewModel: OrdersViewModel = viewModel()
) {
    val uiState by ordersViewModel.uiState.collectAsState()
    val clpFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    clpFormat.maximumFractionDigits = 0
    
    val userId: String? = null
    
    LaunchedEffect(Unit) {
        ordersViewModel.loadOrders(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Header con gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryGreen, DarkGreen)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Receipt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Mis Pedidos",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (uiState.orders.isEmpty()) "No tienes pedidos aún" 
                           else "${uiState.orders.size} pedido${if (uiState.orders.size > 1) "s" else ""}",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryGreen,
                            strokeWidth = 3.dp
                        )
                        Text(
                            text = "Cargando pedidos...",
                            color = Color.Gray
                        )
                    }
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3F3)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = uiState.error ?: "Error desconocido",
                                fontSize = 16.sp,
                                color = Color(0xFFE53935),
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { ordersViewModel.refresh(userId) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryGreen
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
            uiState.orders.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(LightGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = PrimaryGreen
                            )
                        }
                        Text(
                            text = "Sin pedidos",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                        Text(
                            text = "Aún no has realizado ningún pedido.\n¡Explora nuestros productos!",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                        Button(
                            onClick = { navController.navigate("home") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ir a la tienda")
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.orders) { order ->
                        OrderCard(order = order, clpFormat = clpFormat)
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, clpFormat: NumberFormat) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "CL"))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clickable { expanded = !expanded }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(LightGreen, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocalShipping,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Pedido #${order.id.takeLast(6).uppercase()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkGreen
                            )
                            Text(
                                text = dateFormat.format(order.date),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                
                OrderStatusChip(status = order.status)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Timeline del estado
            OrderStatusTimeline(status = order.status)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Resumen de productos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F8F8), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${order.items.size} producto${if (order.items.size > 1) "s" else ""}",
                        fontSize = 14.sp,
                        color = DarkGreen
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Colapsar" else "Expandir",
                    tint = PrimaryGreen
                )
            }
            
            // Detalle expandible
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                HorizontalDivider(color = Color(0xFFEEEEEE))
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    order.items.forEach { item ->
                        OrderItemRow(item = item, clpFormat = clpFormat)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider(color = Color(0xFFEEEEEE))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )
                Text(
                    text = clpFormat.format(order.total),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }
        }
    }
}

@Composable
fun OrderStatusTimeline(status: OrderStatus) {
    val steps = listOf(
        Triple(OrderStatus.PENDING, "Pendiente", Icons.Default.HourglassEmpty),
        Triple(OrderStatus.PROCESSING, "Procesando", Icons.Default.Autorenew),
        Triple(OrderStatus.SHIPPED, "Enviado", Icons.Outlined.LocalShipping),
        Triple(OrderStatus.DELIVERED, "Entregado", Icons.Default.CheckCircle)
    )
    
    val currentIndex = when(status) {
        OrderStatus.PENDING -> 0
        OrderStatus.PROCESSING -> 1
        OrderStatus.SHIPPED -> 2
        OrderStatus.DELIVERED -> 3
        OrderStatus.CANCELLED -> -1
    }
    
    if (status == OrderStatus.CANCELLED) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = null,
                tint = Color(0xFFE53935),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Pedido cancelado",
                color = Color(0xFFE53935),
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            steps.forEachIndexed { index, (_, label, icon) ->
                val isActive = index <= currentIndex
                val isCompleted = index < currentIndex
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                if (isActive) PrimaryGreen else Color(0xFFE0E0E0),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.Check else icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        fontSize = 9.sp,
                        color = if (isActive) DarkGreen else Color.Gray,
                        fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun OrderStatusChip(status: OrderStatus) {
    val (backgroundColor, textColor, icon) = when(status) {
        OrderStatus.PENDING -> Triple(Color(0xFFFFF8E1), Color(0xFFF57F17), Icons.Default.HourglassEmpty)
        OrderStatus.PROCESSING -> Triple(Color(0xFFE3F2FD), Color(0xFF1976D2), Icons.Default.Autorenew)
        OrderStatus.SHIPPED -> Triple(Color(0xFFE8EAF6), Color(0xFF3949AB), Icons.Outlined.LocalShipping)
        OrderStatus.DELIVERED -> Triple(Color(0xFFE8F5E9), Color(0xFF388E3C), Icons.Default.CheckCircle)
        OrderStatus.CANCELLED -> Triple(Color(0xFFFFEBEE), Color(0xFFD32F2F), Icons.Default.Cancel)
    }
    
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = status.toDisplayString(),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

@Composable
fun OrderItemRow(item: OrderItem, clpFormat: NumberFormat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen del producto
        val imageUrl = if (item.product.image.startsWith("http")) {
            item.product.image
        } else {
            "https://productos-service-production.up.railway.app${item.product.image}"
        }
        
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = item.product.title,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
            contentScale = ContentScale.Crop
        )
        
        // Info del producto
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.product.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGreen,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = LightGreen
                ) {
                    Text(
                        text = "x${item.quantity}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryGreen
                    )
                }
                Text(
                    text = "${clpFormat.format(item.price)} c/u",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        
        // Subtotal
        Text(
            text = clpFormat.format(item.price * item.quantity),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen
        )
    }
}
