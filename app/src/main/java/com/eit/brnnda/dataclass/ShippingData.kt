package com.eit.brnnda.dataclass

class ShippingData(
    var userId: String,
    var transectionId: String,
    var orderItemList: List<CartData>,
    var totalPrice: String,
    var shippingAddress: String

)