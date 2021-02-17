package com.eit.brnnda.dataclass.PlaceOrder

data class Order(
    val color: String,
    val id: Int,
    val price: Int,
    val qty: Int,
    val vendor_user_id: Int
)