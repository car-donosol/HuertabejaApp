package com.example.huertabeja

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.example.huertabeja.navigation.AppNavigation
import com.example.huertabeja.ui.theme.HuertabejaTheme

class MainActivity : ComponentActivity() {
    
    // Estado para el resultado del pago de Mercado Pago
    private val paymentResultState = mutableStateOf<String?>(null)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manejar Deep Link inicial
        handleDeepLink(intent)
        
        enableEdgeToEdge()
        setContent {
            HuertabejaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(paymentResult = paymentResultState.value) {
                        // Callback para limpiar el resultado
                        paymentResultState.value = null
                    }
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }
    
    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            // Verificar si es un Deep Link de Mercado Pago
            // huertabeja://payment/success, huertabeja://payment/failure, huertabeja://payment/pending
            if (uri.scheme == "huertabeja" && uri.host == "payment") {
                val result = uri.lastPathSegment // success, failure, or pending
                paymentResultState.value = result
            }
        }
    }
}