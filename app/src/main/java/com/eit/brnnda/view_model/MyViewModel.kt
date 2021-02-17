package com.eit.brnnda.view_model

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.*
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.Event
import com.eit.brnnda.dataclass.*
import com.eit.brnnda.dataclass.PlaceOrder.OrderResponse
import com.eit.brnnda.dataclass.PlaceOrder.PlaceOrderData
import com.eit.brnnda.dataclass.WishList.WishListDeleteResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response

class MyViewModel(private val repository: NetworkRepository) : ViewModel(), Observable {


    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage


    val deleteStatus = MutableLiveData<Boolean>()


    private val statusShippingCost = MutableLiveData<Int>()
    val shippingData: LiveData<Int>
        get() = statusShippingCost


    val cartAllData = repository.getAllCartData


    val cartItemCountData = repository.getCount()
    val cartTotalQuantity = repository.quantityCount()
    val cartTotalCashBack = repository.cashBackCount()


    val calculateTotalPrice = repository.calculateTotalPrice()


    @Bindable
    val productSlug = MutableLiveData<String>()


    @Bindable
    val productName = MutableLiveData<String>()

    @Bindable
    val productBrandName = MutableLiveData<String>()

    @Bindable
    val productImage = MutableLiveData<String>()

    @Bindable
    val productColorCode = MutableLiveData<String>()


    @Bindable
    val productQuantity = MutableLiveData<String>()

    @Bindable
    val productPrice = MutableLiveData<String>()


    fun insertCart(cartData: CartData): Job = viewModelScope.launch {
        val newRowId: Long = repository.insertCart(cartData)
        if (newRowId > -1) {
            statusMessage.value = Event("Cart added ")
        } else {
            statusMessage.value = Event("Already in cart")
        }
    }


    fun updateShippingCost(value: Int) {
        statusShippingCost.value = value
    }


    fun updateCart(cartData: CartData): Job = viewModelScope.launch {
        val rowId: Int = repository.updateCart(cartData)
        if (rowId > 0) {

        } else {

        }
    }


    fun deleteCartItem(cartData: CartData): Job = viewModelScope.launch {
        val rowId: Int = repository.deleteCartItem(cartData)
        if (rowId > 0) {

        } else {

        }

    }

    fun deleteAllCart(): Job = viewModelScope.launch {
        val rowId: Int = repository.deleteAllCartItem()
        if (rowId > 0) {
            statusMessage.value = Event("All deleted successfully")
        } else {
            statusMessage.value = Event("Error occurred ")
        }
    }



    fun checkProductIdBool(id: Int): LiveData<Boolean> {
        return repository.itemCheckBoool(id)
    }

    fun getAllCategoryData(key: String): LiveData<List<CategoryDataItem>> {
        return liveData {
            val response = repository.getCategoryData(key)
            emit(response)
        }
    }

    fun topSliderData(key: String): LiveData<Response<TopSliderData>> {
        return liveData {
            val response = repository.topSliderApi(key)
            emit(response)
        }
    }


    fun getShippingData(key: String): LiveData<List<ShppingDataItem>> {
        return liveData {
            val response = repository.getShipping(key)
            emit(response)
        }
    }


    fun featuredProductData(key: String): LiveData<List<ProductDataItem>> {
        return liveData {
            val response = repository.getFeaturedData(key)
            emit(response)
        }
    }

    fun flashDealData(key: String): LiveData<List<ProductDataItem>> {
        return liveData {
            val response = repository.getFlashDealProduct(key)
            emit(response)
        }
    }


    fun catWiseData(key: String, brandId: String): LiveData<List<ProductDataItem>> {
        return liveData {
            val response = repository.getCatWiseData(key, brandId)
            emit(response)
        }
    }


    fun getWishList(key: String, token: String, userId: String): LiveData<List<ProductDataItem>> {
        return liveData {
            val response = repository.getwishList(key, token, userId)
            emit(response)
        }
    }


    fun deleteWishProduct(
        key: String,
        token: String,
        userId: String,
        productId: String
    ): LiveData<Response<WishListDeleteResponse>> {
        return liveData {
            val response = repository.deleteWishProduct(key, token, userId, productId)
            emit(response)
        }
    }


    fun addWishProduct(
        key: String,
        token: String,
        userId: String,
        productId: String
    ): LiveData<Response<WishListDeleteResponse>> {
        return liveData {
            val response = repository.addwishList(key, token, userId, productId)
            emit(response)
        }
    }


    fun otpVerify(key: String, code: String, phone: String): LiveData<Response<OTPResponse>> {
        return liveData {
            val response = repository.otp(key, code, phone)
            emit(response)
        }
    }


    fun customerOrderHistory(
        key: String,
        toke: String,
        uid: String
    ): LiveData<List<OrderHistoryDataItem>> {
        return liveData {
            val response = repository.customerOrderHistory(key, toke, uid)
            emit(response)
        }
    }


    fun customerSingleOrderHistory(
        key: String,
        token: String,
        id: String,
        order_id: String
    ): LiveData<Response<SingleOrderDetails>> {
        return liveData {
            val response = repository.singleOrderHistory(key, token, id, order_id)
            emit(response)
        }
    }


    fun bestProductData(key: String): LiveData<List<ProductDataItem>> {
        return liveData {
            val response = repository.getBestProduct(key)
            emit(response)
        }
    }


    fun getSubCatWiseProduct(key: String, slug: String): LiveData<List<ProductDataItem>> {
        return liveData {
            val response = repository.getSubCatWiseProduct(key, slug)
            emit(response)
        }
    }


    fun getChildCatWiseProduct(key: String, slug: String): LiveData<List<ProductDataItem>> {
        return liveData {
            val response = repository.getChildCatWiseProduct(key, slug)
            emit(response)
        }
    }


//    fun orderSubmitData(key: String, cartData: CartData): LiveData<Response<OrderSubmitResponse>> {
//        return liveData {
//            val response = repository.submitOrder(key,  cartData)
//            emit(response)
//        }
//    }


    fun submitOrderNowData(
        key: String,
        orderNowData: PlaceOrderData
    ): LiveData<Response<OrderResponse>> {
        return liveData {
            val response = repository.submitOrderNow(key, orderNowData)
            emit(response)
        }
    }


    fun couponData(key: String): LiveData<List<CouponDataItem>> {
        return liveData {
            val response = repository.getCoupon(key)
            emit(response)
        }
    }

    fun hotProductData(key: String): LiveData<List<ProductDataItem>> {
        return liveData {
            val response = repository.getHotProduct(key)
            emit(response)
        }
    }


    fun latestProductData(key: String): LiveData<List<ProductDataItem>> {
        return liveData {
            val response = repository.getLatestProduct(key)
            emit(response)
        }
    }

    fun trendingProductData(key: String): LiveData<List<ProductDataItem>> {
        return liveData {
            val response = repository.getTrendingProduct(key)
            emit(response)
        }
    }

    fun saleProductData(key: String): LiveData<List<ProductDataItem>> {
        return liveData {
            val response = repository.getSaleProduct(key)
            emit(response)
        }
    }

    fun singleProductDetails(key: String, slug: String): LiveData<Response<ProductDetailsData>> {
        return liveData {
            val response = repository.getSingleProductDetails(key, slug)
            emit(response)
        }
    }


    fun searchApi(key: String, itemName: String): LiveData<List<ProductDataItem>> {
        return liveData {
            val response = repository.searchApi(key, itemName)
            emit(response)
        }
    }


    fun singleProductImage(key: String, slug: String): LiveData<List<ProductImageGallaryItem>> {
        return liveData {
            val response = repository.getSingleProductImage(key, slug)
            emit(response)
        }
    }

    fun loginPost(key: String, phone: String, password: String): LiveData<Response<LoginResponse>> {
        return liveData {
            val response = repository.login(key, phone, password)
            emit(response)
        }
    }


    fun logout(key: String, token: String): LiveData<Response<LogoutResponse>> {
        return liveData {
            val response = repository.logout(key, token)
            emit(response)
        }
    }

    fun userRegistration(
        key: String,
        name: String,
        email: String,
        phone: String,
        address: String,
        password: String
    ): LiveData<Response<LogoutResponse>> {
        return liveData {
            val response = repository.userRegistration(key, name, email, phone, address, password)
            emit(response)
        }
    }


    fun editProfile(
        key: String,
        token: String,
        userId: String,
        name: String,
        email: String,
        address: String,
        zip: String,
        city: String
    ): LiveData<Response<EditProfileResponse>> {
        return liveData {
            val response =
                repository.editProfile(key, token, userId, name, email, address, zip, city)
            emit(response)
        }
    }


    fun userInfo(key: String, token: String, id: String): LiveData<Response<CustomerInfo>> {
        return liveData {
            val response = repository.customerInfo(key, token, id)
            emit(response)
        }
    }


    fun getSubCategoryList(key: String, subCatId: String): LiveData<List<SubCategoryDataItem>> {
        return liveData {
            val response = repository.getSubCategory(key, subCatId)
            emit(response)
        }
    }


    fun getChildCategoryList(key: String, childCatId: String): LiveData<List<ChildCatDataItem>> {
        return liveData {
            val response = repository.getChildCategory(key, childCatId)
            emit(response)
        }
    }


    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }


}