# 🚀 Configuración Completada - Backend en Railway

## ✅ URLs de Railway Configuradas

Tu app Android ya está configurada para usar los microservicios desplegados en Railway:

```kotlin
// Usuarios
https://usuarios-service-production-4a93.up.railway.app/

// Productos  
https://productos-service-production.up.railway.app/

// Pedidos
https://pedidos-service-production.up.railway.app/
```

## 📱 Estado de la Configuración

✅ **ApiConfig.kt actualizado** - Backend en Railway por defecto  
✅ **AndroidManifest.xml** - Permisos de Internet configurados  
✅ **Modelos de datos** - Usuario, Producto, Pedido creados  
✅ **Repositories** - Lógica de negocio implementada  
✅ **ViewModels** - AuthViewModel, ProductosViewModel, PedidosViewModel  

## 🔍 Verificar que Railway esté activo

Antes de ejecutar la app, verifica que tus servicios en Railway estén corriendo:

### 1. Accede a tu Dashboard de Railway
- Ve a https://railway.app
- Inicia sesión con tu cuenta
- Verifica que los 3 servicios estén desplegados y en estado "Active"

### 2. Verifica los Logs
En cada servicio de Railway:
- Click en el servicio → Pestaña "Deployments"
- Revisa que el último deployment sea exitoso
- Click en "View Logs" para ver si hay errores

### 3. Verifica las Variables de Entorno
Cada servicio debe tener configuradas:
```
PORT=3001  (o el puerto correspondiente)
MONGODB_URI=mongodb+srv://admin:...
NODE_ENV=production
```

### 4. Verifica los Endpoints

Puedes usar Postman o el navegador para probar:

```
GET https://usuarios-service-production-4a93.up.railway.app/api/usuarios
GET https://productos-service-production.up.railway.app/api/productos
GET https://pedidos-service-production.up.railway.app/api/pedidos
```

## 🔧 Si los servicios no responden

### Opción 1: Redesplegar en Railway
1. Ve a cada servicio en Railway
2. Click en "Settings" → "Redeploy"
3. Espera a que termine el deployment

### Opción 2: Verificar el código del backend
Asegúrate de que cada servicio tenga configurado correctamente:

```javascript
// En usuarios-service/src/index.js (y similar en los otros)
const PORT = process.env.PORT || 3001;

app.listen(PORT, '0.0.0.0', () => {
    console.log(`Servicio de usuarios corriendo en puerto ${PORT}`);
});
```

### Opción 3: Usar backend local temporalmente
Si Railway no responde, puedes cambiar a backend local:

1. En [ApiConfig.kt](app/src/main/java/com/example/huertabeja/data/remote/ApiConfig.kt):
```kotlin
private const val USE_LOCAL_BACKEND = true  // Cambiar a true
```

2. Inicia los servicios localmente:
```powershell
cd "C:\Users\Camo\Desktop\Codigos Visual\backappsmoviles"
.\scripts\start-all-services.ps1
```

## 📲 Próximos Pasos

1. **Sync del Proyecto**
   - Android Studio → File → Sync Project with Gradle Files

2. **Verificar Railway**
   - Asegúrate de que los 3 servicios estén activos

3. **Ejecutar la App**
   - Conecta tu dispositivo/emulador
   - Run → Run 'app'

4. **Probar Login/Registro**
   - La app se conectará automáticamente a Railway
   - No necesitas estar en la misma red WiFi
   - Funciona desde cualquier lugar con Internet

## 🆘 Troubleshooting

### Error: "Unable to resolve host"
- ✅ Verifica tu conexión a Internet
- ✅ Verifica que las URLs en ApiConfig.kt sean correctas
- ✅ Verifica que los servicios estén activos en Railway

### Error 404: "Application not found"
- ✅ Los servicios de Railway pueden estar detenidos
- ✅ Verifica en el dashboard de Railway que estén "Active"
- ✅ Redesplegar los servicios si es necesario

### Error 500: "Internal Server Error"
- ✅ Revisa los logs en Railway
- ✅ Verifica las variables de entorno (MONGODB_URI, etc.)
- ✅ Asegúrate de que MongoDB Atlas esté accesible

### La app no se conecta
- ✅ Verifica los permisos de Internet en AndroidManifest.xml
- ✅ Verifica que `USE_LOCAL_BACKEND = false` en ApiConfig.kt
- ✅ Limpia y reconstruye el proyecto: Build → Clean Project → Rebuild

## 📚 Archivos de Referencia

- [ApiConfig.kt](app/src/main/java/com/example/huertabeja/data/remote/ApiConfig.kt) - Configuración de URLs
- [SETUP_COMPLETO.md](SETUP_COMPLETO.md) - Guía completa con ejemplos
- [RAILWAY_DEPLOYMENT.md](../backappsmoviles/RAILWAY_DEPLOYMENT.md) - Guía de despliegue en Railway

---

**¡Tu app está lista para usar Railway! 🎉**

Si necesitas ayuda, verifica que los servicios en Railway estén activos y revisa los logs para identificar cualquier problema.
