package com.eit.brnnda.Network

import com.eit.brnnda.dataclass.*
import com.eit.brnnda.dataclass.PlaceOrder.OrderResponse
import com.eit.brnnda.dataclass.PlaceOrder.PlaceOrderData
import com.eit.brnnda.dataclass.WishList.WishListDeleteResponse
import retrofit2.Response
import retrofit2.http.*


interface ApiInterface {

    @FormUrlEncoded
    @POST("/api/{at}/customer_login_api")
    suspend fun userLogin(
        @Path(value = "at") secretKey: String,
        @Field("phone") uPhone: String,
        @Field("password") uPassword: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("/api/{at}/customer_info_update/{token}/{user_id}")
    suspend fun updateProfile(
        @Path(value = "at") secretKey: String,
        @Path(value = "token") token: String,
        @Path(value = "user_id") usrId: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("address") address: String,
        @Field("zip") zip: String,
        @Field("city") city: String
    ): Response<EditProfileResponse>

    @FormUrlEncoded
    @POST("/api/{at}/customer_reg_api")
    suspend fun userRegistration(
        @Path(value = "at") secretKey: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
        @Field("password") password: String
    ): Response<LogoutResponse>


    @GET("/api/{at}/customer_logout_api/{token}")
    suspend fun userLogout(
        @Path(value = "at") secretKey: String,
        @Path(value = "token") token: String
    ): Response<LogoutResponse>


    @GET("/api/{at}/customer_order/{token}/{user_id}")
    suspend fun orderHistory(
        @Path(value = "at") secretKey: String,
        @Path(value = "token") token: String,
        @Path(value = "user_id") userId: String
    ): List<OrderHistoryDataItem>


    @GET("/api/{at}/customer_single_order/{token}/{user_id}/{order_id}")
    suspend fun singleOrderHistory(
        @Path(value = "at") secretKey: String,
        @Path(value = "token") token: String,
        @Path(value = "user_id") userId: String,
        @Path(value = "order_id") orderId: String
    ): Response<SingleOrderDetails>


    @GET("/api/{at}/customer_info_api/{token}/{id}")
    suspend fun userInfo(
        @Path(value = "at") secretKey: String,
        @Path(value = "token") token: String,
        @Path(value = "id") userId: String
    ): Response<CustomerInfo>

    @Headers("Content-Type: application/json")
    @POST("/api/{at}/orderapi")
    suspend fun submitOrderNow(
        @Path(value = "at") secretKey: String,
        @Body orderNowData: PlaceOrderData
    ): Response<OrderResponse>

    @GET("/api/{at}/category_api")
    suspend fun getAllCategory(@Path(value = "at") secretKey: String): List<CategoryDataItem>

    @GET("/api/{at}/top_slider_banner_api")
    suspend fun getTopSlider(@Path(value = "at") secretKey: String): Response<TopSliderData>

    @GET("/api/{at}/featured_api")
    suspend fun getFeaturedProduct(@Path(value = "at") secretKey: String): List<ProductDataItem>


    @GET("/api/{at}/flashed_products_api")
    suspend fun getFlashDeal(@Path(value = "at") secretKey: String): List<ProductDataItem>


    @GET("/api/{at}/random_products_api")
    suspend fun getBestProducts(@Path(value = "at") secretKey: String): List<ProductDataItem>

    @GET("/api/{at}/hot_products_api")
    suspend fun getHotProducts(@Path(value = "at") secretKey: String): List<ProductDataItem>

    @GET("/api/{at}/trending_products_api")
    suspend fun getTrendingProducts(@Path(value = "at") secretKey: String): List<ProductDataItem>

    @GET("/api/{at}/couponapi")
    suspend fun getCoupon(@Path(value = "at") secretKey: String): List<CouponDataItem>

    @GET("/api/{at}/latest_products_api")
    suspend fun getNewProducts(@Path(value = "at") secretKey: String): List<ProductDataItem>

    @GET("/api/{at}/sales_products_api")
    suspend fun getSalesProducts(@Path(value = "at") secretKey: String): List<ProductDataItem>

    @GET("/api/{at}/shippinglistapi")
    suspend fun getShippingMethod(@Path(value = "at") secretKey: String): List<ShppingDataItem>

    @GET("/api/{at}/single_products_api/{slug}")
    suspend fun getSingleProductDetails(
        @Path(value = "at") secretKey: String,
        @Path(value = "slug") slug: String
    ): Response<ProductDetailsData>


    @GET("/api/{at}/otp_api/{code}/{phone}")
    suspend fun optVerification(
        @Path(value = "at") secretKey: String,
        @Path(value = "code") code: String,
        @Path(value = "phone") phone: String
    ): Response<OTPResponse>


    @GET("/api/{at}/single_products_gallery_api/{slug}")
    suspend fun getSingleProductImage(
        @Path(value = "at") secretKey: String,
        @Path(value = "slug") slug: String
    ): List<ProductImageGallaryItem>

    @GET("/api/{at}/product_search_api/{searchterm}")
    suspend fun searchApi(
        @Path(value = "at") secretKey: String,
        @Path(value = "searchterm") item: String
    ): List<ProductDataItem>

    @GET("/api/{at}/category_wise_product_api/{cat_slug}")
    suspend fun getCatWiseProduct(
        @Path(value = "at") secretKey: String,
        @Path(value = "cat_slug") slug: String
    ): List<ProductDataItem>

    @GET("/api/{at}/add_wish_list/{token}/{user_id}/{product_id}")
    suspend fun addWishlist(
        @Path(value = "at") secretKey: String,
        @Path(value = "token") token: String,
        @Path(value = "user_id") user_id: String,
        @Path(value = "product_id") productId: String
    ): Response<WishListDeleteResponse>


    @GET("/api/{at}/delete_wish_list/{token}/{user_id}/{product_id}")
    suspend fun deleteWishProduct(
        @Path(value = "at") secretKey: String,
        @Path(value = "token") token: String,
        @Path(value = "user_id") user_id: String,
        @Path(value = "product_id") productId: String
    ): Response<WishListDeleteResponse>


    @GET("/api/{at}/show_wish_list/{token}/{user_id}")
    suspend fun getWishlist(
        @Path(value = "at") secretKey: String,
        @Path(value = "token") token: String,
        @Path(value = "user_id") user_id: String
    ): List<ProductDataItem>

    @GET("/api/{at}/sub_category_wise_product_api/{subcat_slug}")
    suspend fun sub_category_wise_product_api(
        @Path(value = "at") secretKey: String,
        @Path(value = "subcat_slug") slug: String
    ): List<ProductDataItem>

    @GET("/api/{at}/child_category_wise_product_api/{childcat_slug}")
    suspend fun child_category_wise_product_api(
        @Path(value = "at") secretKey: String,
        @Path(value = "childcat_slug") slug: String
    ): List<ProductDataItem>

    @GET("/api/{at}/sub_category_list/{cat_id}")
    suspend fun subCategoryList(
        @Path(value = "at") secretKey: String,
        @Path(value = "cat_id") cat_id: String

    ): List<SubCategoryDataItem>

    @GET("/api/{at}/child_category_list/{subcat_id}")
    suspend fun childCategoryList(
        @Path(value = "at") secretKey: String,
        @Path(value = "subcat_id") subcat_id: String

    ): List<ChildCatDataItem>

}