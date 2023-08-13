package com.mercadolivro.controller.request

import com.mercadolivro.Enum.CustomerStatus
import java.math.BigDecimal

data class PutBookRequest (
    var name: String?,

    var price: BigDecimal?,

    var status: CustomerStatus
)


