package com.eit.brnnda.dataclass

data class CouponDataItem(
    val code: String,
    val end_date: String,
    val id: Int,
    val price: Double,
    val start_date: String,
    val status: Int,
    val times: String,
    val type: Int,
    val used: Int
)