package com.eit.brnnda.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.models.SlideModel
import com.eit.brnnda.Activity.LoginActivity
import com.eit.brnnda.Adapter.ColorAdapter
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.Constent.decodedStringURL
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.FragmentProductBinding
import com.eit.brnnda.dataclass.CartData
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel
import com.example.awesomedialog.*


class ProductFragment : Fragment() {
    private lateinit var binding: FragmentProductBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory
    private lateinit var colorAdapter: ColorAdapter
    private val galleriesImageUrl: String = "$decodedStringURL/assets/images/products/"
    private val singleIImageUrl: String = "$decodedStringURL/assets/images/galleries/"


    var slug: String? = null

    var id: String? = null
    var token: String? = null
    var productId: String? = null

    var cashBack:Int=0
    var colorCode:String ="No color selected"
    var taxAmount : Int=0
    var taxType : Int=0


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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product, container, false)
        binding.myViewModel = vm



        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initColorRecyclerView()


        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.internet_connected_layout)

        val PREFS_NAME = getString(R.string.sharedPrefName)
        val sharedPref: SharedPreferences =
            requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        id = sharedPref.getString(getString(R.string.SHARED_UID), null)
        token = sharedPref.getString(getString(R.string.SHARED_UTOKEN), null)

        vm.message.observe(viewLifecycleOwner, Observer { myEvent ->
            myEvent.getContentIfNotHandled()?.let {
                requireContext().MyToast(it)
            }
        })

        binding.progressbar.visibility = View.VISIBLE





        try {

            arguments.let {
                slug = it?.getString("SLUG")
                if (slug != null) {
                    vm.singleProductDetails(decodedStringKey, slug!!)
                        .observe(viewLifecycleOwner, Observer { it2 ->
                            binding.progressbar.visibility = View.INVISIBLE

                            //                    binding.progressbar.visibility=View.INVISIBLE
                            //                    binding.progressbar.visibility=View.GONE


                            val productInfo = it2.body()

                            if (productInfo != null) {

                                colorAdapter.setList(productInfo.color)
                                colorAdapter.notifyDataSetChanged()

                            }


                            if (productInfo != null) {
                                vm.checkProductIdBool(productInfo.id).observe(viewLifecycleOwner, Observer {bool->
                                    if (bool==true){

                                        binding.buttonAddToCartId.isClickable=false
                                        binding.buttonAddToCartId.text="Already in cart"
                                    }else{
                                        binding.buttonAddToCartId.text="Add to cart"

                                    }
                                })
                            }

                            val imageList = ArrayList<SlideModel>()
                            if (productInfo != null) {

                                val sliderList = productInfo.gallery

                                if (sliderList!=null){


                                    if (sliderList.size==1){

                                        for (index in sliderList) {


                                            imageList.add(SlideModel(galleriesImageUrl + index, ""))


                                        }
                                    }else{
                                        for (index in sliderList) {


                                            imageList.add(SlideModel(singleIImageUrl + index, ""))


                                        }
                                    }

                                    for (index in sliderList) {


                                        imageList.add(SlideModel(singleIImageUrl + index, ""))


                                    }
                                }else{
                                    for (index in 1..3){
                                        imageList.add(SlideModel(galleriesImageUrl + productInfo.photo, ""))
                                        Log.d("Photoadded",index.toString())
                                    }
                                }


                            }


                            binding.imageSlider.setImageList(imageList)






                            if (productInfo != null) {
                                val string2: String = productInfo.details
//                                    binding.webViewId.settings.defaultFontSize = requireContext().resources.getDimension(R.dimen.font_size_large) as Int

                                binding.webViewId.settings.layoutAlgorithm =
                                    WebSettings.LayoutAlgorithm.NORMAL
                                val webView: WebView = binding.webViewId
                                webView.loadDataWithBaseURL(
                                    null as String?,
                                    "<html><head><style type=\"text/css\">body{font-family: 'Source Sans Pro', sans-serif;font-size:14px;line-height:18px}</style></head><body>$string2</body></html>",
                                    "text/html",
                                    "UTF-8",
                                    null as String?
                                )


                            }



                            if (productInfo != null) {
                                binding.tvProductPriceId.text = "${productInfo.price}"

                            }

                            if (productInfo != null) {
                                binding.tvProductName.text = productInfo.name
                                binding.tvProductPriceId.text = "৳${productInfo.price}"
                                binding.tvProductOrginalPrice.text =
                                    "৳${productInfo.previous_price}"
                                binding.tvProductOrginalPrice.paintFlags =
                                    binding.tvProductOrginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                            }

                            if (productInfo != null) {
        ////////////////////////////////////////////////////////////////////////////////////////////

                                if (productInfo.product_tax_status==null){
                                    taxAmount=0
                                    taxType=0
                                }else{
                                    if (productInfo.product_tax_status=="1"){
                                        taxAmount=productInfo.product_tax.toInt()
                                        taxType=1
                                    }else if (productInfo.product_tax_status=="0"){
                                        taxAmount=0
                                        taxType=0
                                    }
                                }

                                binding.textViewHiddenVendorId.text = productInfo.user_id.toString()
                                binding.textViewHiddenProductid.text = productInfo.id.toString()

                                binding.textViewHiddenPrice.text = productInfo.price.toString()

                                binding.textViewHiddenSlug.text = productInfo.sku
                                binding.tvProductName.text = productInfo.name

                                if (productInfo.cashback == null) {
                                    binding.tvCashBakcId.visibility = View.INVISIBLE
                                } else {

                                    cashBack=productInfo.cashback
                                    binding.tvCashBakcId.visibility = View.VISIBLE
                                    binding.tvCashBakcId.text = "৳" + productInfo.cashback.toString() + " CashBack"
                                }

                                binding.textViewHiddenImageUrl.text = productInfo.thumbnail



                                (requireActivity() as AppCompatActivity).supportActionBar?.title =
                                    "Product Details"

                            }


                        })


                }
            }


        } catch (e: Exception) {

        }






        colorAdapter.setOnItemClickLitener(object : ColorAdapter.OnItemClickListener {
            override fun onItemClick(
                view: View,
                position: Int,
                myPosition: String
            ) {
                colorAdapter.setSelection(position)
                colorCode = myPosition


            }

        })


        slug?.let {


        }



        binding.buttonAddToCartId.setOnClickListener {



                    addToCart()







        }


        binding.buttonAddToWishId.setOnClickListener {
            addToWish()
        }

    }

    private fun addToWish() {

        if (id == null) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)

        } else {


            AwesomeDialog
                .build(requireActivity())
                .position(AwesomeDialog.POSITIONS.CENTER)
                .title("Do you want to add wish list")
                .icon(R.mipmap.brnnda)
                .onPositive("Yes, I agree") {

                    binding.progressbar.visibility = View.VISIBLE
                    token?.let {
                        id?.let { it1 ->
                            vm.addWishProduct(
                                    decodedStringKey,
                                it,
                                it1,
                                binding.textViewHiddenProductid.text.toString().trim()
                            ).observe(viewLifecycleOwner, Observer {
                                val response = it.body()?.message
                                val response1 = it.body()?.status

                                binding.progressbar.visibility = View.GONE
                                if (response1 == "1") {
                                    requireContext().MyToast(response.toString())
                                } else {
                                    requireContext().MyToast(response.toString())
                                }

                            })
                        }
                    }
                }
                .onNegative("Cancel") {

                }


        }


    }


    private fun addToCart() {

        val price: Double = binding.textViewHiddenPrice.text.toString().trim().toDouble()
        var taxPrice:Double=0.0

        if (taxType==1){
            val lessAmount = (taxAmount * price) / 100
            taxPrice = lessAmount
        }else if (taxType==0){
            taxPrice=0.0
        }

        Log.d("TAGS","$taxPrice")


        val vendor_user_id: Int = binding.textViewHiddenVendorId.text.toString().trim().toInt()
        val id: Int = binding.textViewHiddenProductid.text.toString().trim().toInt()
        val qty: Int = 1

        val color: String = binding.textViewHiddencolor.text.toString().trim()
        val cartItemBrandName: String = binding.textViewHiddenSlug.text.toString().trim()
        val cartItemName: String = binding.tvProductName.text.toString().trim()
        val cartItemImage: String = binding.textViewHiddenImageUrl.text.toString().trim()


        vm.insertCart(
            CartData(
                vendor_user_id,
                id,
                qty,
                price,
                colorCode,
                cashBack
                ,
                cartItemBrandName,
                cartItemName,
                cartItemImage,
                taxType,
                taxPrice,
                    taxAmount

            )
        )


    }


    private fun initColorRecyclerView() {
        binding.recyclerViewColorId.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        colorAdapter = ColorAdapter()
        binding.recyclerViewColorId.adapter = colorAdapter
    }


}