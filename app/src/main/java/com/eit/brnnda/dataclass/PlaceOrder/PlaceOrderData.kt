package com.eit.brnnda.dataclass.PlaceOrder

import com.eit.brnnda.dataclass.CartData

data class PlaceOrderData(
        val user_id: String,
        val ssl_transaction_id: String,
        val total_price: String,
        val discount: String,
        val total_cashback: String,
        val used_wallet: Double,
        val left_wallet: Double,
        val coupon_code: String,
        val coupon_discount: String,
        val coupon_id: Int,

        val address: String,
        val city: String,
        val zip: Int,
        val shipping_name: String,
        val shipping_email: String,
        val shipping_phone: String,
        val shipping_address: String,
        val shipping_city: String,
        val shipping_zip: String,
        val order_notes: String,

        val method: String,
        val shipping_cost: String,
        val tax: String,
        val totalQty: String,
        val order_list: List<CartData>


)