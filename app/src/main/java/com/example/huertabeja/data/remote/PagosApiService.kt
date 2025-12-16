package com.example.huertabeja.data.remote

import com.example.huertabeja.data.model.PreferenciaRequest
import com.example.huertabeja.data.model.PreferenciaResponse
import com.example.huertabeja.data.model.PagoResponse
import retrofit2.Response
import retrofit2.http.*

interface PagosApiService {

    @POST("api/pagos/preferencia")
    suspend fun crearPreferencia(
        @Body request: PreferenciaRequest
    ): Response<PreferenciaResponse>

    @GET("api/pagos/pago/{paymentId}")
    suspend fun consultarPago(
        @Path("paymentId") paymentId: String
    ): Response<PagoResponse>
}
