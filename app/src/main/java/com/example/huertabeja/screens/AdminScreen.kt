package com.example.huertabeja.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.huertabeja.data.Product
import com.example.huertabeja.viewmodel.AdminProductViewModel
import com.example.huertabeja.viewmodel.AdminState
import com.example.huertabeja.viewmodel.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController,
    productsViewModel: ProductsViewModel = viewModel(),
    adminViewModel: AdminProductViewModel = viewModel()
) {
    val context = LocalContext.current
    val productsUiState by productsViewModel.uiState.collectAsState()
    val products = productsUiState.products
    val isLoadingProducts = productsUiState.isLoading
    val adminState: AdminState by adminViewModel.adminState.collectAsState()
    val isLoadingAdmin: Boolean by adminViewModel.isLoading.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    
    // Cargar productos al inicio
    LaunchedEffect(Unit) {
        productsViewModel.loadProducts()
    }
    
    // Manejar estados de admin
    LaunchedEffect(adminState) {
        when (adminState) {
            is AdminState.Success -> {
                Toast.makeText(
                    context,
                    (adminState as AdminState.Success).message,
                    Toast.LENGTH_SHORT
                ).show()
                // Recargar productos después de una operación exitosa
                productsViewModel.loadProducts()
                adminViewModel.resetState()
                showAddDialog = false
                showEditDialog = false
                showDeleteDialog = false
            }
            is AdminState.Error -> {
                Toast.makeText(
                    context,
                    (adminState as AdminState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {}
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBF8F0))
        ) {
            when {
                isLoadingProducts -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                products.isEmpty() -> {
                    Text(
                        "No hay productos disponibles",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = products,
                            key = { product -> product.slug }
                        ) { product ->
                            AdminProductCard(
                                product = product,
                                onEdit = {
                                    selectedProduct = product
                                    showEditDialog = true
                                },
                                onDelete = {
                                    selectedProduct = product
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
            
            // Indicador de carga para operaciones admin
            if (isLoadingAdmin) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
        
        // Botón flotante para agregar
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = Color(0xFF4CAF50),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, "Agregar producto", tint = Color.White)
        }
    }
    
    // Diálogo para agregar producto
    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, price, priceOffer, image, description, stock, category, home, slug ->
                adminViewModel.createProduct(
                    title = title,
                    price = price,
                    priceOffer = priceOffer,
                    image = image,
                    description = description,
                    stock = stock,
                    category = category,
                    home = home,
                    slug = slug
                )
            }
        )
    }
    
    // Diálogo para editar producto
    if (showEditDialog && selectedProduct != null) {
        EditProductDialog(
            product = selectedProduct!!,
            onDismiss = { showEditDialog = false },
            onConfirm = { title, price, priceOffer, image, description, stock, category, home ->
                // Usar ID si está disponible, sino usar slug
                val identifier = selectedProduct!!.id ?: selectedProduct!!.slug
                adminViewModel.updateProduct(
                    slug = identifier,
                    title = title,
                    price = price,
                    priceOffer = priceOffer,
                    image = image,
                    description = description,
                    stock = stock,
                    category = category,
                    home = home
                )
            }
        )
    }
    
    // Diálogo de confirmación para eliminar
    if (showDeleteDialog && selectedProduct != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Producto") },
            text = { 
                Column {
                    Text("¿Estás seguro de que deseas eliminar '${selectedProduct!!.title}'?")
                    selectedProduct!!.id?.let { id ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "ID: $id",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Usar ID si está disponible, sino usar slug
                        val identifier = selectedProduct!!.id ?: selectedProduct!!.slug
                        adminViewModel.deleteProduct(identifier)
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun AdminProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            Card(
                modifier = Modifier.size(70.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5))
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Mostrar ID si está disponible
                product.id?.let { id ->
                    Text(
                        text = "ID: ${id.take(8)}...",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Light
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$$${product.price}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stock: ${product.stock}",
                        fontSize = 13.sp,
                        color = if (product.stock > 10) Color(0xFF4CAF50) 
                               else if (product.stock > 0) Color(0xFFFF9800)
                               else Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = product.category,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Botones de acción
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int, Int, String, String, Int, String, Boolean, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var priceOffer by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var home by remember { mutableStateOf(false) }
    var slug by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Nuevo Producto") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                    OutlinedTextField(
                        value = slug,
                        onValueChange = { slug = it },
                        label = { Text("Slug (ID único)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Precio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = priceOffer,
                        onValueChange = { priceOffer = it },
                        label = { Text("Precio Oferta (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = image,
                        onValueChange = { image = it },
                        label = { Text("URL de Imagen") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = home,
                            onCheckedChange = { home = it }
                        )
                        Text("Mostrar en página principal")
                    }
                }
            }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        title,
                        price.toIntOrNull() ?: 0,
                        priceOffer.toIntOrNull() ?: 0,
                        image,
                        description,
                        stock.toIntOrNull() ?: 0,
                        category,
                        home,
                        slug
                    )
                },
                enabled = title.isNotBlank() && slug.isNotBlank() && price.isNotBlank()
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EditProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (String?, Int?, Int?, String?, String?, Int?, String?, Boolean?) -> Unit
) {
    var title by remember { mutableStateOf(product.title) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var priceOffer by remember { mutableStateOf(product.price_offer.toString()) }
    var image by remember { mutableStateOf(product.image) }
    var description by remember { mutableStateOf(product.description) }
    var stock by remember { mutableStateOf(product.stock.toString()) }
    var category by remember { mutableStateOf(product.category) }
    var home by remember { mutableStateOf(product.home) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Producto") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Precio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = priceOffer,
                        onValueChange = { priceOffer = it },
                        label = { Text("Precio Oferta") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = image,
                        onValueChange = { image = it },
                        label = { Text("URL de Imagen") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = home,
                            onCheckedChange = { home = it }
                        )
                        Text("Mostrar en página principal")
                    }
                }
            }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        if (title != product.title) title else null,
                        price.toIntOrNull()?.takeIf { it != product.price },
                        priceOffer.toIntOrNull()?.takeIf { it != product.price_offer },
                        if (image != product.image) image else null,
                        if (description != product.description) description else null,
                        stock.toIntOrNull()?.takeIf { it != product.stock },
                        if (category != product.category) category else null,
                        if (home != product.home) home else null
                    )
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
