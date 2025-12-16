package com.example.huertabeja.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.huertabeja.data.CategoryData
import com.example.huertabeja.data.Product
import com.example.huertabeja.viewmodel.CartViewModel
import com.example.huertabeja.viewmodel.ProductsViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    navController: NavController, 
    cartViewModel: CartViewModel,
    productsViewModel: ProductsViewModel = viewModel()
) {
    val uiState by productsViewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas") }
    val sortOptions = listOf("Nombre", "Precio", "Stock")
    var selectedSortOption by remember { mutableStateOf(sortOptions[0]) }
    var expanded by remember { mutableStateOf(false) }

    val filteredAndSortedProducts = uiState.products
        .filter { product ->
            (selectedCategory == "Todas" || product.category == selectedCategory) &&
                    (product.title.contains(searchQuery, ignoreCase = true) || product.description.contains(searchQuery, ignoreCase = true))
        }
        .sortedWith(
            when (selectedSortOption) {
                "Precio" -> compareBy { it.price }
                "Stock" -> compareBy { it.stock }
                else -> compareBy { it.title }
            }
        )

    Column(
        modifier = Modifier
            .background(color = Color(0xFFFBF8F0))
            .fillMaxSize()
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
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { productsViewModel.refresh() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8DA356)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reintentar"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reintentar")
                        }
                    }
                }
            }
            else -> {
                // Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar producto...") },
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sorting Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    TextField(
                        value = selectedSortOption,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ordenar por") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        sortOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedSortOption = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Categories Section
                CategoriesSection(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (filteredAndSortedProducts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron productos",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(items = filteredAndSortedProducts, key = { it.slug }) { product ->
                            ProductCard(
                                product = product,
                                onAddToCart = { cartViewModel.addProduct(product) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onAddToCart: () -> Unit) {
    val clpFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    clpFormat.maximumFractionDigits = 0
    
    // Construir URL completa de la imagen
    val imageUrl = if (product.image.startsWith("http")) {
        product.image
    } else {
        "https://servicio-productos.fly.dev${product.image}"
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column (modifier = Modifier.background(color = Color(0xFFDED8CA))){
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Mostrar precio con oferta si existe
                if (product.price_offer > 0) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = clpFormat.format(product.price),
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Text(
                            text = clpFormat.format(product.price_offer),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE91E63)
                        )
                    }
                } else {
                    Text(
                        text = clpFormat.format(product.price),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Stock: ${product.stock}",
                    fontSize = 12.sp,
                    color = if (product.stock > 0) Color(0xFF4CAF50) else Color.Red
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8DA356)
                    ),
                    enabled = product.stock > 0
                ) {
                    Text(
                        text = if (product.stock > 0) "Agregar al carrito" else "Sin stock",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoriesSection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf(
        CategoryData("Todas", "Category"),
        CategoryData("Interior", "Home"),
        CategoryData("Exterior", "Park")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                isSelected = category.name == selectedCategory,
                onClick = { onCategorySelected(category.name) }
            )
        }
    }
}

@Composable
private fun CategoryCard(category: CategoryData, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFF8E9B6B) else Color.White
    val textColor = if (isSelected) Color.White else Color(0xFF3C4522)
    
    val icon = when(category.emoji) {
        "Home" -> Icons.Default.Home
        "Park" -> Icons.Default.Park
        "Category" -> Icons.Default.Category
        else -> Icons.Default.Category
    }

    Card(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = category.name,
                tint = textColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
