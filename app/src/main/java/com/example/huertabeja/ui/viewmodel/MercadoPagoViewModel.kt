package com.example.huertabeja.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertabeja.data.model.*
import com.example.huertabeja.data.repository.MercadoPagoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MercadoPagoUiState {
    object Idle : MercadoPagoUiState()
    object Loading : MercadoPagoUiState()
    data class PreferenciaCreada(val initPoint: String, val preferenceId: String) : MercadoPagoUiState()
    data class PagoConsultado(val pago: PagoResponse) : MercadoPagoUiState()
    data class Error(val message: String) : MercadoPagoUiState()
}

enum class PaymentResult {
    NONE,
    SUCCESS,
    FAILURE,
    PENDING
}

class MercadoPagoViewModel : ViewModel() {

    private val repository = MercadoPagoRepository()

    private val _uiState = MutableStateFlow<MercadoPagoUiState>(MercadoPagoUiState.Idle)
    val uiState: StateFlow<MercadoPagoUiState> = _uiState.asStateFlow()

    private val _paymentResult = MutableStateFlow(PaymentResult.NONE)
    val paymentResult: StateFlow<PaymentResult> = _paymentResult.asStateFlow()

    /**
     * Crea una preferencia de pago con los items del carrito
     */
    fun crearPreferencia(
        items: List<ItemPago>,
        payer: PayerInfo? = null,
        pedidoId: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = MercadoPagoUiState.Loading

            val result = repository.crearPreferencia(items, payer, pedidoId)

            result.fold(
                onSuccess = { response ->
                    _uiState.value = MercadoPagoUiState.PreferenciaCreada(
                        initPoint = response.initPoint,
                        preferenceId = response.preferenceId
                    )
                },
                onFailure = { error ->
                    _uiState.value = MercadoPagoUiState.Error(
                        error.message ?: "Error al crear preferencia de pago"
                    )
                }
            )
        }
    }

    /**
     * Consulta el estado de un pago
     */
    fun consultarPago(paymentId: String) {
        viewModelScope.launch {
            _uiState.value = MercadoPagoUiState.Loading

            val result = repository.consultarPago(paymentId)

            result.fold(
                onSuccess = { pago ->
                    _uiState.value = MercadoPagoUiState.PagoConsultado(pago)
                },
                onFailure = { error ->
                    _uiState.value = MercadoPagoUiState.Error(
                        error.message ?: "Error al consultar pago"
                    )
                }
            )
        }
    }

    /**
     * Maneja el resultado del pago desde el Deep Link
     */
    fun handlePaymentResult(result: String) {
        when (result.lowercase()) {
            "success" -> _paymentResult.value = PaymentResult.SUCCESS
            "failure" -> _paymentResult.value = PaymentResult.FAILURE
            "pending" -> _paymentResult.value = PaymentResult.PENDING
            else -> _paymentResult.value = PaymentResult.NONE
        }
    }

    fun resetState() {
        _uiState.value = MercadoPagoUiState.Idle
    }

    fun resetPaymentResult() {
        _paymentResult.value = PaymentResult.NONE
    }
}
