package com.eit.brnnda.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eit.brnnda.Activity.ShoppingFaildActivity
import com.eit.brnnda.Activity.ShoppingSuccessActivity
import com.eit.brnnda.Adapter.CheckOutAdapter
import com.eit.brnnda.Adapter.CouponAdapter
import com.eit.brnnda.Adapter.ShippingAdapter
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent
import com.eit.brnnda.Utils.Constent.apSignature
import com.eit.brnnda.Utils.Constent.apStore
import com.eit.brnnda.Utils.Constent.decodedAmarPaySignatureId
import com.eit.brnnda.Utils.Constent.decodedAmarPayStoreId
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.FragmentCheckOutBinding
import com.eit.brnnda.dataclass.CartData
import com.eit.brnnda.dataclass.CouponDataItem
import com.eit.brnnda.dataclass.PlaceOrder.PlaceOrderData
import com.eit.brnnda.dataclass.ShppingDataItem
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel
import com.example.awesomedialog.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.softbd.aamarpay.PayByAamarPay
import com.softbd.aamarpay.interfaces.OnPaymentRequestListener
import com.softbd.aamarpay.model.OptionalFields
import com.softbd.aamarpay.model.PaymentResponse
import com.softbd.aamarpay.model.RequiredFields
import com.softbd.aamarpay.utils.Params
import com.whygraphics.multilineradiogroup.MultiLineRadioGroup


class CheckOutFragment : Fragment(),  OnPaymentRequestListener {
    private lateinit var binding: FragmentCheckOutBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory
    private lateinit var cartItemAdapter: CheckOutAdapter
    private lateinit var shippingAdapter: ShippingAdapter
    var currentUserId: String? = null
    var currentUserToken: String? = null
    private var currentUserWallet: Double = 0.0
    var usedWallet: Double = 0.0
    var leftWallet: Double = 0.0
    var currentProdutTotalPrice: Double = 0.0
    var walletUsed: Boolean = false
    var currentUserPhone: String? = null
    var currentUserAddress: String? = null
    var currentUserName: String? = null
    var currentUserEmail: String? = null
    var receiverPhone: String = " "
    var receiverAddress: String = " "
    var receiverName: String = " "
    var receiverCity: String = ""
    var receiverZip: String = ""
    var voucherType: String? = null
    var voucherCode: String? = null
    var voucherValue: String? = null
    var voucherId: String? = null
    var shippingCost: Int = 0
    var shippingMethod: String? = null
    var finalPayableCost: Double = 0.0
    var shippingSelected: Boolean = false
    var orderList = arrayListOf<CartData>()
    var discountAmount: String? = null
    var afterDiscountAmount: Double? = null
    var amarPayInitial:RequiredFields?=null
    var paymentMethod: String = "Online"
    var paymentMethodSelected: Boolean = false
    var totalQuantity: Int? = null
    var totalCashBack: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = activity?.run {

            val dao = RoomDatabaseAbstract.invoke(application).getCartDao
            repository = NetworkRepository(dao)
            factory = BaseViewModelFactory(repository)
            ViewModelProvider(this, factory).get(MyViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_check_out, container, false
        )
        binding.myViewModel = vm
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val PREFS_NAME = getString(R.string.sharedPrefName)
        val sharedPref: SharedPreferences =
            requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        currentUserId = sharedPref.getString(getString(R.string.SHARED_UID), null)
        currentUserToken = sharedPref.getString(getString(R.string.SHARED_UTOKEN), null)




        binding.mainActivityMultiLineRadioGroup.setOnCheckedChangeListener(MultiLineRadioGroup.OnCheckedChangeListener { group, button ->
            paymentMethod = button.text.toString()
            paymentMethodSelected = true
        })



        currentUserToken?.let {
            currentUserId?.let { it1 ->
                vm.userInfo(decodedStringKey, it, it1)
                    .observe(viewLifecycleOwner, Observer { userInfoResponse ->
                        val responseName = userInfoResponse.body()?.name
                        val responseEmail = userInfoResponse.body()?.email
                        val responsePhone = userInfoResponse.body()?.phone
                        val responseWallet = userInfoResponse.body()?.wallet
                        val address = userInfoResponse.body()?.address



                        currentUserName = responseName
                        currentUserEmail = responseEmail
                        currentUserPhone = responsePhone
                        if (responseWallet != null) {
                            currentUserWallet = responseWallet.toDouble()
                        }
                        currentUserAddress = address

                        binding.tvShippingReceiverNameId.text = responseName.toString()
                        binding.tvShippingReceiverPhoneId.text = responsePhone.toString()
                        binding.tvShippingReceiverAddressId.text = address.toString()
                        binding.tvWalletId.text = "$responseWallet"


                    })
            }
        }


        initShippingRecyclerView()



        vm.cartAllData.observe(viewLifecycleOwner, Observer {
            orderList.addAll(it)

        })






        vm.cartTotalCashBack.observe(viewLifecycleOwner, Observer {
            totalCashBack = it
            binding.tvcashBack.text="৳$it"
        })


        vm.cartTotalQuantity.observe(viewLifecycleOwner, Observer {

            totalQuantity = it
        })


        vm.shippingData.observe(viewLifecycleOwner, Observer {
            shippingCost = it
        })

        vm.getShippingData(decodedStringKey).observe(viewLifecycleOwner, Observer {
            shippingAdapter.setList(it)
            shippingAdapter.notifyDataSetChanged()
        })


        vm.message.observe(viewLifecycleOwner, Observer { myEvent ->
            myEvent.getContentIfNotHandled()?.let {
                requireContext().MyToast(it)
            }
        })

        vm.calculateTotalPrice.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                binding.layButton.visibility = View.INVISIBLE
                binding.rlMain.visibility = View.INVISIBLE
                binding.llNoItems.visibility = View.VISIBLE
                binding.nsvCart.visibility = View.GONE


            } else {
                binding.layButton.visibility = View.VISIBLE
                binding.tvTotalAmount.text = "৳$it"
                binding.tvTotalFinalPayableAmount.text = "৳$it"
                currentProdutTotalPrice = it
            }
        })

        initCartItemRecyclerView()

        binding.tvAddCoupon.setOnClickListener {
            bottomSheetVoucher()
        }





        binding.tvContinue.setOnClickListener {
            if (finalPayableCost>=500.0){
                if (shippingSelected) {

                    if (paymentMethodSelected) {

                        if (paymentMethod.contains("Online")) {



                            AwesomeDialog
                                    .build(requireActivity())
                                    .position(AwesomeDialog.POSITIONS.CENTER)
                                    .title("I want to order now")
                                    .icon(R.mipmap.brnnda)
                                    .onPositive("Yes") {
                                        amarPaySetUp()
                                    }
                                    .onNegative("No") {

                                    }

                        } else if (paymentMethod.contains("Cash")) {
                            AwesomeDialog
                                    .build(requireActivity())
                                    .position(AwesomeDialog.POSITIONS.CENTER)
                                    .title("I want to order now")
                                    .icon(R.mipmap.brnnda)
                                    .onPositive("Yes") {
                                        COD_ORDER()
                                    }
                                    .onNegative("No") {

                                    }


                        } else {
                            requireContext().MyToast("Please select Payment")
                        }


                    } else {
                        requireContext().MyToast("Please select Payment")
                    }

                } else {
                    requireContext().MyToast("Please select shipping method")
                }
            }else{
                requireContext().MyToast("Minimum order ৳500")
            }
        }

        shippingAdapter.setOnItemClickLitener(object : ShippingAdapter.OnItemClickListener {
            override fun onItemClick(
                view: View,
                position: Int,
                myPosition: ShppingDataItem
            ) {
                shippingAdapter.setSelection(position)
                shippingSelected = true
                vm.updateShippingCost(myPosition.price)

                shippingMethod = myPosition.title

                Log.d("Shippingcost", myPosition.title)


            }

        })

        binding.tvChangeShippingAddressButtonid.setOnClickListener {
            bottomSheet()
        }
        binding.tvUserWalletButtonId.setOnClickListener {


            val wallet = currentUserWallet
            val currentProductPrice = currentProdutTotalPrice


            if (wallet > currentProductPrice) {

                if (walletUsed == false) {
                    useWallet()
                    binding.tvUserWalletButtonId.text = "Remove"
                    binding.tvUserWalletButtonId.setTextColor(requireContext().resources.getColor(R.color.red))
                    walletUsed = true

                } else {
                    removeWallet()
                    binding.tvUserWalletButtonId.text = "Use wallet"
                    binding.tvUserWalletButtonId.setTextColor(requireContext().resources.getColor(R.color.black))
                    walletUsed = false
                }
            } else {
                requireContext().MyToast("Not enough money")
            }


        }


        setFinalAmountPrice()

    }




    private fun useWallet() {
        //condition-1


        val wallet = currentUserWallet
        val currentProductPrice = currentProdutTotalPrice


        if (wallet > currentProductPrice) {
            val dynamicPersentAmount = 50
            val lessAmount = (dynamicPersentAmount * currentProductPrice) / 100
            val newWallet = wallet - lessAmount

            usedWallet = lessAmount
            leftWallet = newWallet
            currentProdutTotalPrice = lessAmount
            binding.tvWallet1Id.text = "$lessAmount"
            binding.tvWallet1Id.text = "$lessAmount Taka can use only"

            setFinalAmountPrice()
        } else {
            binding.tvWallet1Id.text = "Not enough money"

        }


    }

    private fun removeWallet() {
        vm.calculateTotalPrice.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                binding.layButton.visibility = View.INVISIBLE
                binding.rlMain.visibility = View.INVISIBLE
                binding.llNoItems.visibility = View.VISIBLE
                binding.nsvCart.visibility = View.GONE


            } else {
                binding.layButton.visibility = View.VISIBLE
                binding.tvTotalAmount.text = "৳$it"
                binding.tvTotalFinalPayableAmount.text = "৳$it"
                currentProdutTotalPrice = it
                setFinalAmountPrice()
            }
        })


    }

    private fun setFinalAmountPrice() {

        vm.shippingData.observe(viewLifecycleOwner, Observer { it2 ->


            binding.tvSubTotal.text = String.format("৳%.2f", currentProdutTotalPrice)
            binding.tvShipping.text = "৳$it2"


            val pValue = voucherValue?.toDouble()
            discountAmount = pValue.toString()

            when (voucherType) {
                "1" -> {
                    val percentage = (currentProdutTotalPrice - (pValue!!))
                    val totalCost = percentage + it2.toDouble()
                    finalPayableCost = totalCost
                    afterDiscountAmount = pValue
                    binding.tvDiscount.text = String.format("৳%.2f", pValue)
                    binding.tvTotalFinalPayableAmount.text = String.format("৳%.2f", totalCost)
                }
                "0" -> {
                    val percentageValue = 100 - pValue!!
                    val percentage = (percentageValue * currentProdutTotalPrice) / 100
                    val totalCost = percentage + it2.toDouble()
                    finalPayableCost = totalCost
                    val lessAmount = (pValue * currentProdutTotalPrice) / 100
                    afterDiscountAmount = lessAmount
                    binding.tvDiscount.text = String.format("৳%.2f", lessAmount)
                    binding.tvTotalFinalPayableAmount.text = String.format("৳%.2f", totalCost)
                }
                else -> {
                    val totalCost = currentProdutTotalPrice!! + it2.toDouble()
                    finalPayableCost = totalCost
                    binding.tvDiscount.text = "৳0"
                    afterDiscountAmount = 0.0
                    binding.tvTotalFinalPayableAmount.text = String.format("৳%.2f", totalCost)
                }
            }

        })


    }

    private fun initCartItemRecyclerView() {
        binding.rvCart.layoutManager = GridLayoutManager(context, 1)
        cartItemAdapter = CheckOutAdapter(
            { selectedPlus: CartData -> itemClickPlus(selectedPlus) },
            { selectedMinus: CartData -> itemClickMinus(selectedMinus) },
            { selectedDelete: CartData -> itemClickDelete(selectedDelete) }
        )
        binding.rvCart.adapter = cartItemAdapter
        displayCartItemList()
    }

    private fun displayCartItemList() {
        vm.cartAllData.observe(viewLifecycleOwner, Observer {
            cartItemAdapter.setList(it)
            cartItemAdapter.notifyDataSetChanged()


        })
    }

    private fun itemClickPlus(data: CartData) {

    }

    private fun itemClickMinus(data: CartData) {


    }

    private fun itemClickDelete(data: CartData) {

    }

    private fun amarPaySetUp() {
        val currentTimestamp = System.currentTimeMillis()


        amarPayInitial = RequiredFields(
            "$currentUserName",
            "$currentUserEmail",
            "$currentUserAddress",
            "City",
            "State",
            "1234",
            "BD",
            "$currentUserPhone",
            "Description",
            "$finalPayableCost",
            Params.CURRENCY_BDT,
            "CBK$currentTimestamp",
                apStore, // aamarpaytest
                apSignature, // dbb74894e82415a2f7ff0ec3a97e4183
            "success",
            "fail",
            "cancelled"
        )

        val optionalFields = OptionalFields()
        PayByAamarPay.getInstance(requireContext(), amarPayInitial,optionalFields).payNow(this)



    }


    private fun COD_ORDER() {
        val currentTimestamp = System.currentTimeMillis()
        var voucr = "00"

        if (voucherValue == null) {
            voucr = "00"
        } else {
            voucr = voucherValue.toString()
        }

        var vid: Int = 0

        if (voucherId == null) {
            vid = 0
        } else {
            vid = voucherId!!.toInt()
        }


        val submitAllData = currentUserId?.let { cuid ->

            PlaceOrderData(
                cuid,
                "CBK$currentTimestamp",
                "$finalPayableCost",
                afterDiscountAmount.toString(),
                "$totalCashBack",
                usedWallet,
                leftWallet,
                binding.txtApplyCouponCode.text.toString().trim(),
                voucr,
                vid,
                currentUserAddress!!,
                "default city",
                1111,
                receiverName,
                currentUserEmail!!,
                receiverPhone,
                receiverAddress,
                receiverCity,
                receiverZip,
                "no note",
                paymentMethod,
                shippingCost.toString(),
                "0",
                "$totalQuantity",
                orderList
            )
        }



        if (submitAllData != null) {
            vm.submitOrderNowData(decodedStringKey, submitAllData)
                .observe(viewLifecycleOwner, Observer { order_received_response ->
                    val orderResponse = order_received_response.body()?.msg
                    val orderResponse2 = order_received_response.body()?.status
                    Log.d("BODYDATA", orderResponse.toString())
                    if (orderResponse2 == "1") {
                        vm.deleteAllCart()
                        val intent= Intent(requireContext(), ShoppingSuccessActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    } else {
                        val intent= Intent(requireContext(), ShoppingFaildActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                })
        }

    }

    private fun initShippingRecyclerView() {
        binding.recyclerViewShippingId.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        shippingAdapter = ShippingAdapter()
        binding.recyclerViewShippingId.adapter = shippingAdapter
    }

    private fun bottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_layout)
        bottomSheetDialog.setCanceledOnTouchOutside(true)

        lateinit var submitButton: Button
        lateinit var textViewName: EditText
        lateinit var textViewPhone: EditText
        lateinit var textViewZip: EditText
        lateinit var textViewCity: EditText
        lateinit var textViewAddress: EditText

        textViewName = bottomSheetDialog.findViewById(R.id.etBottomSheet_NameId)!!
        textViewPhone = bottomSheetDialog.findViewById(R.id.etBottomSheet_PhoneId)!!
        textViewZip = bottomSheetDialog.findViewById(R.id.etBottomSheet_ZipId)!!
        textViewCity = bottomSheetDialog.findViewById(R.id.etBottomSheet_CityId)!!
        textViewAddress = bottomSheetDialog.findViewById(R.id.etBottomSheet_AddressId)!!



        submitButton = bottomSheetDialog.findViewById(R.id.bottomSheet_ButtonSubmitId)!!


        submitButton.setOnClickListener {

            val name = textViewName.text.toString().trim()
            val phone = textViewPhone.text.toString().trim()
            val zip = textViewZip.text.toString().trim()
            val city = textViewCity.text.toString().trim()

            val fulladdress = textViewAddress.text.toString().trim()


            if (name.isEmpty()) {
                textViewName.error = "Name is required"
            } else if (phone.isEmpty()) {
                textViewPhone.error = "phone is required"
            } else if (zip.isEmpty()) {
                textViewZip.error = "zip is required"
            } else if (city.isEmpty()) {
                textViewCity.error = "city is required"
            } else if (fulladdress.isEmpty()) {
                textViewAddress.error = "address is required"
            } else {


                receiverName = name
                receiverPhone = phone
                receiverZip = zip
                receiverCity = city
                receiverAddress = fulladdress


                binding.tvShippingReceiverNameId.text = name
                binding.tvShippingReceiverPhoneId.text = phone
                binding.tvShippingReceiverAddressId.text = "$fulladdress $city-$zip"

                bottomSheetDialog.dismiss()
            }


        }

        bottomSheetDialog.show()
    }

    private fun bottomSheetVoucher() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_voucher_layout)
        bottomSheetDialog.setCanceledOnTouchOutside(true)

        lateinit var progressBar: ProgressBar
        lateinit var recyclerView: RecyclerView
        lateinit var couponAdapter: CouponAdapter
        progressBar = bottomSheetDialog.findViewById(R.id.progressbarBottomSheetid)!!
        recyclerView = bottomSheetDialog.findViewById(R.id.recyclerViewCouponBottomSheetId)!!

        recyclerView.layoutManager = GridLayoutManager(context, 1)
        couponAdapter = CouponAdapter { selected: CouponDataItem ->
            itemClick(selected)
            bottomSheetDialog.dismiss()

        }
        recyclerView.adapter = couponAdapter

        progressBar.visibility=View.VISIBLE
        vm.couponData(decodedStringKey).observe(viewLifecycleOwner, Observer {
            progressBar.visibility=View.GONE
            couponAdapter.setList(it)
            couponAdapter.notifyDataSetChanged()

        })


        bottomSheetDialog.show()
    }

    private fun itemClick(data: CouponDataItem) {
        voucherId = data.id.toString()
        voucherCode = data.code
        voucherType = data.type.toString()
        voucherValue = data.price.toString()
        binding.txtApplyCouponCode.text = voucherCode.toString()
        setFinalAmountPrice()

    }

    override fun onPaymentResponse(p0: Int, p1: PaymentResponse?) {




        if (p1 != null) {


            if (p1.status=="2") {
                Log.d("BODYDATA", p1.id)
                Log.d("BODYDATA", p1.status)

                val submitAllData = currentUserId?.let { cuid ->

                    PlaceOrderData(
                        cuid,
                        p1.id,
                        p1.amountBdt,
                        afterDiscountAmount.toString(),
                        "$totalCashBack",
                        usedWallet,
                        leftWallet,
                        binding.txtApplyCouponCode.text.toString().trim(),
                        voucherValue!!,
                        voucherId!!.toInt(),
                        currentUserAddress!!,
                        "default city",
                        1111,
                        receiverName,
                        currentUserEmail!!,
                        receiverPhone,
                        receiverAddress,
                        receiverCity,
                        receiverZip,
                        "no note",
                        shippingMethod!!,
                        shippingCost.toString(),
                        "0",
                        "$totalQuantity",
                        orderList
                    )
                }



                if (submitAllData != null) {
                    vm.submitOrderNowData(decodedStringKey, submitAllData)
                        .observe(viewLifecycleOwner, Observer { order_received_response ->
                            val orderResponse = order_received_response.body()?.msg
                            val orderResponse2 = order_received_response.body()?.status
                            Log.d("BODYDATA", orderResponse.toString())
                            if (orderResponse2 == "1") {
                                vm.deleteAllCart()
                                val intent= Intent(requireContext(), ShoppingSuccessActivity::class.java)
                                startActivity(intent)
                                activity?.finish()
                            } else {
                                val intent= Intent(requireContext(), ShoppingFaildActivity::class.java)
                                startActivity(intent)
                                activity?.finish()
                            }
                        })
                }


            } else {
                requireContext().MyToast(p1.status)
            }

        }
    }


}