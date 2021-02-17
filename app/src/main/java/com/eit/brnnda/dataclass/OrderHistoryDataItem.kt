package com.eit.brnnda.dataclass

data class OrderHistoryDataItem(
    val created_at: String,
    val id: Int,
    val order_number: String,
    val pay_amount: Double,
    val status: String
)