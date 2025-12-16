package com.example.huertabeja.data.model

import com.google.gson.annotations.SerializedName

// Request para crear preferencia de pago
data class PreferenciaRequest(
    val items: List<ItemPago>,
    val payer: PayerInfo? = null,
    val pedidoId: String? = null,
    @SerializedName("back_urls")
    val backUrls: BackUrls? = null
)

data class ItemPago(
    val id: String,
    val title: String,
    val description: String = "",
    @SerializedName("picture_url")
    val pictureUrl: String = "",
    @SerializedName("category_id")
    val categoryId: String = "others",
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: Int  // Mercado Pago Chile usa CLP (enteros, sin decimales)
)

data class PayerInfo(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val phone: PhoneInfo? = null,
    val address: AddressInfo? = null
)

data class PhoneInfo(
    @SerializedName("area_code")
    val areaCode: String = "",
    val number: String = ""
)

data class AddressInfo(
    @SerializedName("street_name")
    val streetName: String = "",
    @SerializedName("street_number")
    val streetNumber: String = "",
    @SerializedName("zip_code")
    val zipCode: String = ""
)

data class BackUrls(
    val success: String = "huertabeja://payment/success",
    val failure: String = "huertabeja://payment/failure",
    val pending: String = "huertabeja://payment/pending"
)

// Response de crear preferencia
data class PreferenciaResponse(
    val mensaje: String?,
    val preferenceId: String,
    val initPoint: String,
    val sandboxInitPoint: String?
)

// Response de consultar pago
data class PagoResponse(
    val id: Long,
    val status: String,
    @SerializedName("status_detail")
    val statusDetail: String?,
    @SerializedName("transaction_amount")
    val transactionAmount: Double?,
    @SerializedName("currency_id")
    val currencyId: String?,
    @SerializedName("date_created")
    val dateCreated: String?,
    @SerializedName("date_approved")
    val dateApproved: String?,
    @SerializedName("payment_method_id")
    val paymentMethodId: String?,
    @SerializedName("payment_type_id")
    val paymentTypeId: String?,
    @SerializedName("external_reference")
    val externalReference: String?
)
