package com.eit.brnnda.Network

import androidx.lifecycle.LiveData
import com.eit.brnnda.dataclass.*
import com.eit.brnnda.dataclass.PlaceOrder.OrderResponse
import com.eit.brnnda.dataclass.PlaceOrder.PlaceOrderData
import com.eit.brnnda.dataclass.WishList.WishListDeleteResponse
import com.eit.brnnda.room_database.DaoInterface
import retrofit2.Response

class NetworkRepository(private val daoInterface: DaoInterface) {

    val getAllCartData = daoInterface.getAllCartData()

    suspend fun insertCart(cartData: CartData): Long {
        return daoInterface.insertCartData(cartData)
    }

    suspend fun updateCart(cartData: CartData): Int {
        return daoInterface.updateCartData(cartData)
    }

    suspend fun deleteCartItem(cartData: CartData): Int {
        return daoInterface.deleteCartData(cartData)
    }

    suspend fun deleteAllCartItem(): Int {
        return daoInterface.deleteAllCartData()
    }

    fun getCount(): LiveData<Int> {
        return daoInterface.getCartCount()
    }


    fun cashBackCount(): LiveData<Int> {
        return daoInterface.cashBackCount()
    }


    fun quantityCount(): LiveData<Int> {
        return daoInterface.totalQytCount()
    }



    fun itemCheckBoool(id: Int): LiveData<Boolean> {
        return daoInterface.isRowIsExist(id)
    }


    fun calculateTotalPrice(): LiveData<Double> {
        return daoInterface.calculateTotal()
    }

    suspend fun getCategoryData(key: String): List<CategoryDataItem> =
        RetrofitBuilder.api.getAllCategory(key)

    suspend fun topSliderApi(key: String): Response<TopSliderData> =
        RetrofitBuilder.api.getTopSlider(key)

    suspend fun getFeaturedData(key: String): List<ProductDataItem> =
        RetrofitBuilder.api.getFeaturedProduct(key)

    suspend fun getBestProduct(key: String): List<ProductDataItem> =
        RetrofitBuilder.api.getBestProducts(key)


    suspend fun getFlashDealProduct(key: String): List<ProductDataItem> =
        RetrofitBuilder.api.getFlashDeal(key)


    suspend fun getHotProduct(key: String): List<ProductDataItem> =
        RetrofitBuilder.api.getHotProducts(key)

    suspend fun getLatestProduct(key: String): List<ProductDataItem> =
        RetrofitBuilder.api.getNewProducts(key)

    suspend fun getTrendingProduct(key: String): List<ProductDataItem> =
        RetrofitBuilder.api.getTrendingProducts(key)

    suspend fun getSaleProduct(key: String): List<ProductDataItem> =
        RetrofitBuilder.api.getSalesProducts(key)

    suspend fun getSingleProductDetails(key: String, slug: String): Response<ProductDetailsData> =
        RetrofitBuilder.api.getSingleProductDetails(key, slug)

    suspend fun getSingleProductImage(key: String, slug: String): List<ProductImageGallaryItem> =
        RetrofitBuilder.api.getSingleProductImage(key, slug)

    suspend fun searchApi(key: String, searchView: String): List<ProductDataItem> =
        RetrofitBuilder.api.searchApi(key, searchView)

    suspend fun getCoupon(key: String): List<CouponDataItem> = RetrofitBuilder.api.getCoupon(key)

    suspend fun getShipping(key: String): List<ShppingDataItem> =
        RetrofitBuilder.api.getShippingMethod(key)

    suspend fun submitOrderNow(
        key: String,
        orderNowData: PlaceOrderData
    ): Response<OrderResponse> = RetrofitBuilder.api.submitOrderNow(key, orderNowData)

    suspend fun getCatWiseData(key: String, slug: String): List<ProductDataItem> =
        RetrofitBuilder.api.getCatWiseProduct(key, slug)


    suspend fun getSubCatWiseProduct(key: String, slug: String): List<ProductDataItem> =
        RetrofitBuilder.api.sub_category_wise_product_api(key, slug)


    suspend fun getChildCatWiseProduct(key: String, slug: String): List<ProductDataItem> =
        RetrofitBuilder.api.child_category_wise_product_api(key, slug)



    suspend fun getwishList(key: String, token: String,userId:String): List<ProductDataItem> =
        RetrofitBuilder.api.getWishlist(key, token,userId )


    suspend fun deleteWishProduct(key: String, token: String,userId:String, productId: String): Response<WishListDeleteResponse> =
        RetrofitBuilder.api.deleteWishProduct(key, token,userId,productId )


    suspend fun addwishList(key: String, token: String,userId:String, productId: String): Response<WishListDeleteResponse> =
        RetrofitBuilder.api.addWishlist(key, token,userId,productId )


    suspend fun login(key: String, phone: String, password: String): Response<LoginResponse> = RetrofitBuilder.api.userLogin(key, phone, password)
    suspend fun logout(key: String, token: String): Response<LogoutResponse> = RetrofitBuilder.api.userLogout(key, token)


    suspend fun userRegistration(key: String, name:String, email:String, phone:String, address:String, password: String):
            Response<LogoutResponse> = RetrofitBuilder.api.userRegistration(key, name, email, phone, address, password)


    suspend fun editProfile(key: String, token:String, userId:String, name:String, email:String, address: String,zip:String, city:String):
            Response<EditProfileResponse> = RetrofitBuilder.api.updateProfile(key,token,userId,name, email, address, zip, city)



    suspend fun customerInfo(key: String, token: String,id:String): Response<CustomerInfo> = RetrofitBuilder.api.userInfo(key, token,id)

    suspend fun customerOrderHistory(key: String, token: String,id:String): List<OrderHistoryDataItem> = RetrofitBuilder.api.orderHistory(key, token,id)

    suspend fun singleOrderHistory(key: String, token: String,id:String, order_id:String): Response<SingleOrderDetails> = RetrofitBuilder.api.singleOrderHistory(key, token,id,order_id)


    suspend fun otp(key: String, code: String, phone: String): Response<OTPResponse> = RetrofitBuilder.api.optVerification(key, code,phone)



    suspend fun getSubCategory(key: String, catId: String): List<SubCategoryDataItem> =
        RetrofitBuilder.api.subCategoryList(key, catId )



    suspend fun getChildCategory(key: String, catId: String): List<ChildCatDataItem> =
        RetrofitBuilder.api.childCategoryList(key, catId )

}