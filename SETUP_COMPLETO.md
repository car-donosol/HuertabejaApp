# ✅ Configuración Completa de Integración Backend - App Móvil

## 🎯 Implementación Completada

Todos los componentes necesarios para conectar tu app Android con el backend han sido creados exitosamente.

## 📋 Archivos Creados/Actualizados

### 1. ✅ Configuración Base
- **build.gradle.kts** - Dependencias actualizadas (Retrofit, OkHttp, Coroutines)
- **AndroidManifest.xml** - Permisos de Internet y cleartext traffic habilitado

### 2. ✅ Configuración de Red
- **data/remote/ApiConfig.kt** - Configuración de Retrofit con tu IP: `192.168.100.123`

### 3. ✅ Modelos de Datos
- **data/model/Usuario.kt** - Modelos para autenticación y usuarios
- **data/model/Producto.kt** - Modelos para productos
- **data/model/Pedido.kt** - Modelos para pedidos

### 4. ✅ Interfaces de API
- **data/remote/UsuariosApiService.kt** - Endpoints de usuarios
- **data/remote/ProductosApiService.kt** - Endpoints de productos
- **data/remote/PedidosApiService.kt** - Endpoints de pedidos

### 5. ✅ Repositories
- **data/repository/UsuariosRepository.kt** - Lógica de negocio para usuarios
- **data/repository/ProductosRepository.kt** - Lógica de negocio para productos
- **data/repository/PedidosRepository.kt** - Lógica de negocio para pedidos

### 6. ✅ ViewModels de Ejemplo
- **viewmodel/AuthViewModel.kt** - ViewModel para login/registro
- **viewmodel/ProductosViewModelNew.kt** - ViewModel para productos
- **viewmodel/PedidosViewModelNew.kt** - ViewModel para pedidos

### 7. ✅ Utilidades
- **utils/SessionManager.kt** - Gestión de sesión mejorada con JWT
- **utils/NetworkUtils.kt** - Utilidades para verificar conectividad

---

## 🚀 Pasos Siguientes

### Paso 1: Iniciar el Backend

Abre PowerShell en la carpeta del backend y ejecuta:

```powershell
cd "C:\Users\Camo\Desktop\Codigos Visual\backappsmoviles"

# Iniciar todos los servicios
.\scripts\start-all-services.ps1
```

O inicia cada servicio individualmente:

```powershell
# Terminal 1 - Usuarios
cd usuarios-service
npm install
npm start

# Terminal 2 - Productos
cd productos-service
npm install
npm start

# Terminal 3 - Pedidos
cd pedidos-service
npm install
npm start
```

### Paso 2: Verificar Backend

Asegúrate de que los servicios respondan:

```powershell
# Verificar Usuarios (Puerto 3001)
curl http://localhost:3001/health

# Verificar Productos (Puerto 3002)
curl http://localhost:3002/health

# Verificar Pedidos (Puerto 3003)
curl http://localhost:3003/health
```

### Paso 3: Configurar Firewall de Windows

Permite las conexiones a los puertos del backend:

```powershell
# Ejecutar como Administrador
netsh advfirewall firewall add rule name="Node 3001" dir=in action=allow protocol=TCP localport=3001
netsh advfirewall firewall add rule name="Node 3002" dir=in action=allow protocol=TCP localport=3002
netsh advfirewall firewall add rule name="Node 3003" dir=in action=allow protocol=TCP localport=3003
```

### Paso 4: Sync del Proyecto Android

En Android Studio:
1. **File → Sync Project with Gradle Files**
2. Espera a que termine la sincronización
3. Limpia el proyecto: **Build → Clean Project**
4. Reconstruye: **Build → Rebuild Project**

### Paso 4: Conectar Dispositivo/Emulador

Como tu backend está en Railway (en la nube), puedes usar:
- ✅ **Emulador de Android Studio** - Funciona directamente
- ✅ **Dispositivo físico** - Funciona con cualquier conexión a Internet
- ✅ **No necesitas estar en la misma red WiFi**

La app ya está configurada para usar Railway por defecto (`USE_LOCAL_BACKEND = false`).

### Paso 5: Probar la Conexión

Crea una función de prueba en tu app:

```kotlin
// En cualquier Activity/Fragment
import com.example.huertabeja.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun testBackendConnection() {
    CoroutineScope(Dispatchers.Main).launch {
        val result = NetworkUtils.checkBackendConnection("http://192.168.100.123:3001")
        if (result) {
            Log.d("Backend", "✅ Conexión exitosa con el backend")
        } else {
            Log.e("Backend", "❌ No se pudo conectar al backend")
        }
    }
}
```

---

## 💡 Ejemplo de Uso - Login

Así es como usarías el nuevo sistema en tu app:

```kotlin
// En tu LoginScreen o LoginActivity
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huertabeja.viewmodel.AuthViewModel
import com.example.huertabeja.utils.SessionManager

@Composable
fun LoginScreen(
    sessionManager: SessionManager,
    onLoginSuccess: () -> Unit
) {
    val viewModel: AuthViewModel = viewModel()
    val loginResult by viewModel.loginResult.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    // Observer del resultado del login
    LaunchedEffect(loginResult) {
        loginResult?.let { result ->
            result.onSuccess { response ->
                // Guardar sesión
                sessionManager.saveUsuario(response.usuario, response.token)
                
                // Navegar a home
                onLoginSuccess()
            }
            result.onFailure { error ->
                // Mostrar error
                println("Error: ${error.message}")
            }
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        
        Button(
            onClick = { viewModel.login(email, password) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text("Iniciar Sesión")
            }
        }
    }
}
```

## 💡 Ejemplo de Uso - Obtener Productos

```kotlin
// En tu ProductsScreen
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huertabeja.viewmodel.ProductosViewModel

@Composable
fun ProductsScreen() {
    val viewModel: ProductosViewModel = viewModel()
    val productos by viewModel.productos.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    
    LaunchedEffect(Unit) {
        viewModel.obtenerProductos(pagina = 1, limite = 20)
    }
    
    productos?.let { result ->
        result.onSuccess { response ->
            LazyColumn {
                items(response.productos) { producto ->
                    ProductItem(producto)
                }
            }
        }
        result.onFailure { error ->
            Text("Error: ${error.message}")
        }
    }
    
    if (isLoading) {
        CircularProgressIndicator()
    }
}
```

## 💡 Ejemplo de Uso - Crear Pedido

```kotlin
// En tu CartScreen o CheckoutScreen
import com.example.huertabeja.viewmodel.PedidosViewModel
import com.example.huertabeja.data.model.*

@Composable
fun CheckoutScreen(
    sessionManager: SessionManager,
    cartItems: List<CartItem>
) {
    val viewModel: PedidosViewModel = viewModel()
    val crearPedidoResult by viewModel.crearPedidoResult.observeAsState()
    
    fun realizarPedido() {
        val token = sessionManager.getAuthToken() ?: return
        val usuarioId = sessionManager.getUserId() ?: return
        
        val request = CrearPedidoRequest(
            usuarioId = usuarioId,
            productos = cartItems.map { 
                ProductoCarrito(
                    productoId = it.productoId,
                    cantidad = it.cantidad
                )
            },
            direccionEntrega = Direccion(
                calle = "Calle Principal 123",
                ciudad = "Ciudad",
                estado = "Estado",
                codigoPostal = "12345",
                pais = "México"
            ),
            metodoPago = "tarjeta",
            notas = "Entregar en horario de oficina"
        )
        
        viewModel.crearPedido(token, request)
    }
    
    LaunchedEffect(crearPedidoResult) {
        crearPedidoResult?.let { result ->
            result.onSuccess { response ->
                println("Pedido creado: ${response.pedido.id}")
                // Navegar a pantalla de confirmación
            }
            result.onFailure { error ->
                println("Error: ${error.message}")
            }
        }
    }
    
    Button(onClick = { realizarPedido() }) {
        Text("Realizar Pedido")
    }
}
```

---

## 🔧 Configuración Adicional

### Cambiar entre Backend Local y Remoto

En [ApiConfig.kt](app/src/main/java/com/example/huertabeja/data/remote/ApiConfig.kt):

```kotlin
// Para usar backend local (tu PC):
private const val USE_LOCAL_BACKEND = true

// Para usar backend remoto (Railway/Fly.io):
private const val USE_LOCAL_BACKEND = false
```

### Cambiar IP Local

Si tu IP cambia, actualiza en [ApiConfig.kt](app/src/main/java/com/example/huertabeja/data/remote/ApiConfig.kt):

```kotlin
private const val LOCAL_IP = "TU_NUEVA_IP_AQUI"
```

Para obtener tu IP actual:
```powershell
ipconfig | findstr "IPv4"
```

---

## ⚠️ Solución de Problemas

### Error: "Unable to resolve host"
- ✅ Verifica que el backend esté corriendo
- ✅ Verifica que dispositivo/PC estén en la misma red WiFi
- ✅ Verifica el firewall de Windows
- ✅ Verifica la IP en ApiConfig.kt

### Error: "Connection refused"
- ✅ Asegúrate de que los servicios estén iniciados
- ✅ Verifica que los puertos 3001, 3002, 3003 estén libres
- ✅ Usa `netstat -ano | findstr "3001"` para verificar

### Error: "Cleartext HTTP traffic not permitted"
- ✅ Ya configurado en AndroidManifest.xml con `usesCleartextTraffic="true"`

### Si usas emulador y no funciona:
```kotlin
// Cambia la IP en ApiConfig.kt
private const val LOCAL_IP = "10.0.2.2"
```

---

## 📱 Próximos Pasos Recomendados

1. **Interceptor de Token Automático**: Agrega un interceptor en OkHttp para incluir el token automáticamente
2. **Refresh Token**: Implementa renovación automática de tokens
3. **Caché Local**: Usa Room Database para guardar datos offline
4. **Manejo de Errores**: Implementa un sistema global de manejo de errores
5. **Estados de UI**: Usa sealed classes para manejar estados (Loading, Success, Error)
6. **Testing**: Agrega tests unitarios para repositories y ViewModels

---

## 📚 Recursos

- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [ViewModel Guide](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [API Examples](../backappsmoviles/API_EXAMPLES.json)

---

## ✅ Checklist Final

- [ ] Backend iniciado en los 3 puertos (3001, 3002, 3003)
- [ ] Firewall configurado para permitir conexiones
- [ ] Proyecto Android sincronizado con Gradle
- [ ] IP local verificada y actualizada en ApiConfig.kt
- [ ] Dispositivo/emulador conectado a la misma red
- [ ] Primera prueba de conexión exitosa

---

**¡Tu app Android ahora está lista para conectarse con el backend! 🎉**

Para cualquier duda, revisa los archivos de ejemplo creados o consulta la documentación del backend en [KOTLIN_INTEGRATION.md](../backappsmoviles/KOTLIN_INTEGRATION.md).
