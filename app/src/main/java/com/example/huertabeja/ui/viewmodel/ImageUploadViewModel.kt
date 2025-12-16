package com.example.huertabeja.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.model.ImageUploadResponse
import com.example.huertabeja.data.repository.ImageUploadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ImageUploadUiState {
    object Idle : ImageUploadUiState()
    object Loading : ImageUploadUiState()
    data class Success(val response: ImageUploadResponse) : ImageUploadUiState()
    data class Error(val message: String) : ImageUploadUiState()
}

class ImageUploadViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = ImageUploadRepository(application)
    
    private val _uiState = MutableStateFlow<ImageUploadUiState>(ImageUploadUiState.Idle)
    val uiState: StateFlow<ImageUploadUiState> = _uiState
    
    private val _uploadedImages = MutableStateFlow<List<String>>(emptyList())
    val uploadedImages: StateFlow<List<String>> = _uploadedImages
    
    fun subirImagen(imageUri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.value = ImageUploadUiState.Loading
                
                val result = repository.subirImagen(imageUri)
                
                result.onSuccess { response ->
                    _uiState.value = ImageUploadUiState.Success(response)
                    // Agregar URL a la lista
                    _uploadedImages.value = _uploadedImages.value + response.url
                }.onFailure { error ->
                    _uiState.value = ImageUploadUiState.Error(error.message ?: "Error desconocido")
                }
            } catch (e: Exception) {
                _uiState.value = ImageUploadUiState.Error(e.message ?: "Error al subir imagen")
            }
        }
    }
    
    fun eliminarImagen(url: String, publicId: String) {
        viewModelScope.launch {
            try {
                val result = repository.eliminarImagen(publicId)
                
                result.onSuccess {
                    // Remover URL de la lista
                    _uploadedImages.value = _uploadedImages.value.filter { it != url }
                }.onFailure { error ->
                    _uiState.value = ImageUploadUiState.Error(error.message ?: "Error al eliminar")
                }
            } catch (e: Exception) {
                _uiState.value = ImageUploadUiState.Error(e.message ?: "Error al eliminar imagen")
            }
        }
    }
    
    fun clearImages() {
        _uploadedImages.value = emptyList()
    }
    
    fun resetState() {
        _uiState.value = ImageUploadUiState.Idle
    }
}
