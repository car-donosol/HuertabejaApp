package com.example.huertabeja.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.model.LoginResponse
import com.example.huertabeja.data.model.RegistroRequest
import com.example.huertabeja.data.model.RegistroResponse
import com.example.huertabeja.data.repository.UsuariosRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    
    private val repository = UsuariosRepository()
    
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult
    
    private val _registroResult = MutableLiveData<Result<RegistroResponse>>()
    val registroResult: LiveData<Result<RegistroResponse>> = _registroResult
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.loginUsuario(email, password)
            _loginResult.value = result
            _isLoading.value = false
        }
    }
    
    fun registro(request: RegistroRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.registrarUsuario(request)
            _registroResult.value = result
            _isLoading.value = false
        }
    }
    
    fun clearResults() {
        _loginResult.value = null
        _registroResult.value = null
    }
}
