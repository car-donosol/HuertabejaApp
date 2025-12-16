# 📸 Sistema de Upload de Imágenes - Guía Completa

## ✅ Implementación Completada

### Backend (Node.js + Cloudinary)

#### 1. Dependencias Instaladas
```bash
npm install cloudinary multer streamifier
```

#### 2. Archivos Creados
- `src/config/cloudinary.js` - Configuración de Cloudinary
- `src/config/multer.js` - Configuración de Multer para uploads
- Endpoints agregados en `productos.controller.js`:
  - `POST /api/productos/upload` - Subir imagen
  - `DELETE /api/productos/upload/:public_id` - Eliminar imagen

#### 3. Variables de Entorno Requeridas
```env
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret
```

**Obtener credenciales:**
1. Crear cuenta gratuita en https://cloudinary.com/
2. En Dashboard, copiar Cloud Name, API Key y API Secret
3. Agregar a `.env` (local) o Variables en Railway (producción)

### Android (Kotlin + Jetpack Compose)

#### 1. Dependencias Agregadas en `build.gradle.kts`
```kotlin
implementation("io.coil-kt:coil-compose:2.5.0")
implementation("androidx.activity:activity-compose:1.8.2")
```

#### 2. Permisos Agregados en `AndroidManifest.xml`
```xml
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
```

#### 3. Archivos Creados
- `data/model/ImageUpload.kt` - Modelos de respuesta
- `data/repository/ImageUploadRepository.kt` - Lógica de upload
- `ui/viewmodel/ImageUploadViewModel.kt` - Estado y lógica de UI
- `screens/AddProductImageScreen.kt` - Pantalla de selección de imágenes
- Endpoint agregado en `ProductosApiService.kt`

## 🚀 Cómo Usar

### Desde la App Android

1. **Acceder a la pantalla:**
   - Ve a AdminScreen
   - Pulsa el botón azul flotante (🖼️)

2. **Subir imágenes:**
   - Pulsa "Seleccionar Imagen"
   - Permite acceso a tus fotos
   - Selecciona una imagen (máximo 5MB)
   - La imagen se sube automáticamente a Cloudinary

3. **Ver imágenes subidas:**
   - Las URLs aparecen en la pantalla
   - Copia las URLs para usarlas al crear productos

4. **Eliminar imágenes:**
   - Pulsa la ✖️ en cada imagen
   - Se elimina de Cloudinary automáticamente

### Usar URLs en Productos

Al crear/editar un producto, usa las URLs en el campo `imagenes`:

```json
{
  "nombre": "Planta de Albahaca",
  "imagenes": [
    "https://res.cloudinary.com/tu-cloud/image/upload/v123/huertabeja_productos/abc123.jpg"
  ]
}
```

## 📋 Endpoints Backend

### Subir Imagen
```http
POST /api/productos/upload
Content-Type: multipart/form-data

Body:
- imagen: [archivo de imagen]

Response 200:
{
  "mensaje": "Imagen subida exitosamente",
  "url": "https://res.cloudinary.com/.../huertabeja_productos/abc123.jpg",
  "public_id": "huertabeja_productos/abc123"
}

Errores:
- 400: No se envió ninguna imagen / Formato inválido
- 500: Error al subir a Cloudinary
```

### Eliminar Imagen
```http
DELETE /api/productos/upload/:public_id

Response 200:
{
  "mensaje": "Imagen eliminada exitosamente"
}

Errores:
- 404: Imagen no encontrada
- 500: Error al eliminar
```

## 🎯 Características

### Optimizaciones Automáticas
- **Redimensión:** 800x800px máximo
- **Calidad:** Ajuste automático
- **Formato:** Conversión automática a WebP (cuando es posible)
- **Carpeta:** Todas las imágenes en `huertabeja_productos/`

### Validaciones
- **Formatos permitidos:** JPEG, JPG, PNG, GIF, WEBP
- **Tamaño máximo:** 5MB por imagen
- **Límite por producto:** 5 imágenes (configurable)

### Seguridad
- Las imágenes se procesan en memoria (no se guardan localmente)
- Se valida tipo MIME y extensión
- Las URLs son permanentes hasta que se eliminan manualmente

## 🧪 Probar Localmente

### 1. Configurar Cloudinary
```bash
# En productos-service/.env
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret
```

### 2. Reiniciar el servicio
```bash
cd productos-service
npm run dev
```

### 3. Probar con Postman
```bash
POST http://localhost:3002/api/productos/upload
Body: form-data
Key: imagen | Type: File | Value: [seleccionar imagen]
```

### 4. Probar desde Android
- Configura `USE_LOCAL_BACKEND = true` en ApiConfig.kt
- Asegúrate de usar tu IP local correcta
- Ejecuta la app y prueba el upload

## 🚀 Desplegar en Railway

### 1. Agregar Variables de Entorno
En Railway > productos-service > Variables:
```
CLOUDINARY_CLOUD_NAME = tu_cloud_name
CLOUDINARY_API_KEY = tu_api_key
CLOUDINARY_API_SECRET = tu_api_secret
```

### 2. Commit y Push
```bash
git add .
git commit -m "feat: Sistema de upload de imágenes con Cloudinary"
git push
```

### 3. Railway se despliega automáticamente
- Espera a que termine el deployment
- Prueba con la URL de producción

### 4. Configurar Android para Producción
```kotlin
// En ApiConfig.kt
private const val USE_LOCAL_BACKEND = false  // Usar Railway
```

## 📝 Notas Importantes

### Plan Gratuito de Cloudinary
- **Almacenamiento:** 25GB
- **Transformaciones:** 25,000 mensuales
- **Bandwidth:** 25GB/mes
- **Suficiente para desarrollo y proyectos pequeños**

### Mejoras Futuras Opcionales
- [ ] Agregar watermark automático
- [ ] Comprimir imágenes antes de subir (desde Android)
- [ ] Soporte para múltiples imágenes simultáneas
- [ ] Galería de imágenes subidas con paginación
- [ ] Edición de imágenes (crop, rotate) antes de subir

## 🐛 Solución de Problemas

### Error: "No se envió ninguna imagen"
- Verifica que el campo se llame exactamente `imagen`
- Asegúrate de que es tipo `multipart/form-data`

### Error: "Error al subir imagen a Cloudinary"
- Verifica las credenciales en variables de entorno
- Revisa los logs del servidor para más detalles

### Error de permisos en Android
- Verifica que los permisos estén en AndroidManifest.xml
- En Android 13+, pide `READ_MEDIA_IMAGES`
- En Android 12 y anterior, pide `READ_EXTERNAL_STORAGE`

### Imagen no se muestra en la app
- Verifica que la URL sea válida
- Usa Coil para cargar imágenes: `AsyncImage(model = url, ...)`
- Asegúrate de tener permiso de INTERNET

## ✨ Feature Destacable para Presentación

Este sistema de upload demuestra:
1. **Integración Cloud completa** (Cloudinary)
2. **Buenas prácticas:** Validación, optimización automática, manejo de errores
3. **UX moderna:** Image picker nativo, preview de imágenes, carga con progress
4. **Arquitectura limpia:** Repository pattern, ViewModel, Estado reactivo
5. **Producción ready:** Variables de entorno, manejo de errores, logging

## 📚 Recursos
- [Cloudinary Docs](https://cloudinary.com/documentation)
- [Multer Docs](https://github.com/expressjs/multer)
- [Coil Compose](https://coil-kt.github.io/coil/compose/)
