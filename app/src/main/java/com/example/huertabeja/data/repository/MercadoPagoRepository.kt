package com.example.huertabeja.data.repository

import com.example.huertabeja.data.model.*
import com.example.huertabeja.data.remote.ApiConfig

class MercadoPagoRepository {

    private val pagosService = ApiConfig.getPagosService()

    /**
     * Crea una preferencia de pago en Mercado Pago
     * Retorna la URL (init_point) para abrir el checkout
     */
    suspend fun crearPreferencia(
        items: List<ItemPago>,
        payer: PayerInfo? = null,
        pedidoId: String? = null
    ): Result<PreferenciaResponse> {
        return try {
            val request = PreferenciaRequest(
                items = items,
                payer = payer,
                pedidoId = pedidoId,
                backUrls = BackUrls(
                    success = "huertabeja://payment/success",
                    failure = "huertabeja://payment/failure",
                    pending = "huertabeja://payment/pending"
                )
            )

            val response = pagosService.crearPreferencia(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear preferencia: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Consulta el estado de un pago por su ID
     */
    suspend fun consultarPago(paymentId: String): Result<PagoResponse> {
        return try {
            val response = pagosService.consultarPago(paymentId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al consultar pago: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
