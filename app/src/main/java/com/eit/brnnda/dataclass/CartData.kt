package com.eit.brnnda.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "tbl_cart")
data class CartData(
        var vendor_user_id: Int,

        var id: Int,
        var qty: Int,
        var price: Double,
        var color: String,
        var cashBack: Int,
        var cartItemSlugName: String,
        var cartItemName: String,
        var cartItemImage: String,
        var taxType: Int,
        var taxPrice: Double,
        var taxValue: Int

){
        @PrimaryKey(autoGenerate = true)
        var cartId: Int = 0
}