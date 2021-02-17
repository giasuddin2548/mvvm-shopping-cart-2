package com.eit.brnnda.room_database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eit.brnnda.dataclass.CartData

@Dao
interface DaoInterface {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCartData(cartData: CartData): Long

    @Update
    suspend fun updateCartData(cartData: CartData): Int

    @Delete
    suspend fun deleteCartData(cartData: CartData): Int

    @Query("delete from tbl_cart")
    suspend fun deleteAllCartData(): Int

    @Query("select * from tbl_cart order by  cartId")
    fun getAllCartData(): LiveData<List<CartData>>

    @Query("SELECT COUNT(*) FROM tbl_cart")
    fun getCartCount(): LiveData<Int>

    @Query("SELECT SUM(cashBack*qty) FROM tbl_cart")
    fun cashBackCount(): LiveData<Int>

    @Query("SELECT SUM(qty) FROM tbl_cart")
    fun totalQytCount(): LiveData<Int>

    @Query("SELECT SUM(qty*price+(taxPrice*qty)) FROM tbl_cart")
    fun calculateTotal(): LiveData<Double>

    @Query("SELECT EXISTS(SELECT * FROM tbl_cart WHERE id = :pid)")
    fun isRowIsExist(pid : Int) : LiveData<Boolean>


}