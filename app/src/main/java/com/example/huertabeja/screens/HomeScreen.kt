package com.example.huertabeja.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.huertabeja.data.BannerData
import com.example.huertabeja.data.CategoryData
import com.example.huertabeja.data.Product
import com.example.huertabeja.viewmodel.ProductsViewModel
import com.example.huertabeja.navigation.AppScreens
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    productsViewModel: ProductsViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas") }
    val uiState by productsViewModel.uiState.collectAsState()
    val products = uiState.products

    val filteredProducts = products.filter { product ->
        (selectedCategory == "Todas" || product.category == selectedCategory) &&
                product.title.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFBF8F0))
    ) {
        // Barra de búsqueda
        item {
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                modifier = Modifier.padding(16.dp)
            )
        }

        // Carrusel / Banner
        item {
            BannerCarousel(
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 16.dp)
            )
        }

        // Categorías
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Categorías",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3C4522),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            CategoriesSection(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
        }

        // Productos Destacados
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Destacados",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3C4522)
                )
                Text(
                    text = "Ver todos",
                    fontSize = 14.sp,
                    color = Color(0xFF8E9B6B),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        navController.navigate(AppScreens.ProductsScreen.route)
                    }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            FeaturedProductsRow(
                products = products.take(4),
                navController = navController
            )
        }

        // Listado de productos (filtrado)
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (selectedCategory == "Todas") "Todos los Productos" else selectedCategory,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3C4522),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (filteredProducts.isEmpty()) {
            item {
                Text(
                    text = "No se encontraron productos en esta categoría.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        } else {
            items(filteredProducts) { product ->
                ProductListItem(
                    product = product,
                    onClick = {
                        // TODO: Navigate to product detail screen
                        navController.navigate(AppScreens.ProductsScreen.route)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Buscar productos...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedBorderColor = Color(0xFF8E9B6B),
            unfocusedBorderColor = Color(0xFFE0E0E0)
        ),
        singleLine = true
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun BannerCarousel(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val banners = listOf(
        BannerData("¡Nuevos productos!", "Descubre nuestra colección", Color(0xFF8DA356)),
        BannerData("Ofertas Especiales", "Hasta 30% de descuento", Color(0xFF8E9B6B)),
        BannerData("Huerto en Casa", "Todo lo que necesitas", Color(0xFF9CAF76))
    )

    val pagerState = rememberPagerState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(modifier = modifier) {
        HorizontalPager(
            count = banners.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            BannerCard(
                banner = banners[page],
                onClick = { navController.navigate(AppScreens.ProductsScreen.route) }
            )
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            activeColor = Color.White,
            inactiveColor = Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun BannerCard(banner: BannerData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = banner.backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            banner.backgroundColor,
                            banner.backgroundColor.copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = banner.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = banner.subtitle,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
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
        contentPadding = PaddingValues(horizontal = 16.dp),
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

@Composable
private fun FeaturedProductsRow(products: List<Product>, navController: NavController) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            FeaturedProductCard(
                product = product,
                onClick = { navController.navigate(AppScreens.ProductsScreen.route) }
            )
        }
    }
}

@Composable
private fun FeaturedProductCard(product: Product, onClick: () -> Unit) {
    val clpFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    clpFormat.maximumFractionDigits = 0

    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            val imageUrl = if (product.image.startsWith("http")) {
                product.image
            } else {
                "https://servicio-productos.fly.dev${product.image}"
            }
            
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Imagen de ${product.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = product.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3C4522),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = clpFormat.format(product.price),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF3C4522)
                )
            }
        }
    }
}

@Composable
private fun ProductListItem(product: Product, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val clpFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    clpFormat.maximumFractionDigits = 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl = if (product.image.startsWith("http")) {
                product.image
            } else {
                "https://servicio-productos.fly.dev${product.image}"
            }
            
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3C4522),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = clpFormat.format(product.price),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            IconButton(onClick = { /* TODO: Add to cart logic */ }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir al carrito",
                    tint = Color(0xFF8E9B6B)
                )
            }
        }
    }
}
