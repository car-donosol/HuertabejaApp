package com.example.huertabeja.screens

import android.Manifest
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.huertabeja.ui.viewmodel.ImageUploadUiState
import com.example.huertabeja.ui.viewmodel.ImageUploadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductImageScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: ImageUploadViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ImageUploadViewModel(context.applicationContext as Application)
            }
        }
    )
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uploadedImages by viewModel.uploadedImages.collectAsStateWithLifecycle()
    
    var hasPermission by remember { mutableStateOf(false) }
    
    // Launcher para pedir permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Permiso denegado para acceder a imágenes", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Launcher para seleccionar imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.subirImagen(it)
        }
    }
    
    // Solicitar permiso al iniciar
    LaunchedEffect(Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        permissionLauncher.launch(permission)
    }
    
    // Manejar estado de UI
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ImageUploadUiState.Success -> {
                Toast.makeText(context, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            is ImageUploadUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Imágenes de Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Selecciona imágenes del producto",
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = "Puedes agregar hasta 5 imágenes. Máximo 5MB cada una.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Botón para seleccionar imagen
            Button(
                onClick = {
                    if (hasPermission) {
                        if (uploadedImages.size < 5) {
                            imagePickerLauncher.launch("image/*")
                        } else {
                            Toast.makeText(context, "Máximo 5 imágenes", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Se requiere permiso para acceder a imágenes", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is ImageUploadUiState.Loading && uploadedImages.size < 5
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (uiState is ImageUploadUiState.Loading) "Subiendo..." else "Seleccionar Imagen")
            }
            
            // Indicador de carga
            if (uiState is ImageUploadUiState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            
            // Lista de imágenes subidas
            if (uploadedImages.isNotEmpty()) {
                Text(
                    text = "Imágenes subidas (${uploadedImages.size})",
                    style = MaterialTheme.typography.titleSmall
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uploadedImages) { imageUrl ->
                        ImagePreviewCard(
                            imageUrl = imageUrl,
                            onDelete = {
                                // Extraer public_id de la URL de Cloudinary
                                val publicId = imageUrl
                                    .substringAfter("huertabeja_productos/")
                                    .substringBefore(".")
                                viewModel.eliminarImagen(imageUrl, "huertabeja_productos/$publicId")
                            }
                        )
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Mostrar URLs para copiar (útil para crear productos)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "URLs de imágenes:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        uploadedImages.forEach { url ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = url.take(40) + "...",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("URL de imagen", url)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "URL copiada al portapapeles", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copiar URL",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImagePreviewCard(
    imageUrl: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(120.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen del producto",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}
