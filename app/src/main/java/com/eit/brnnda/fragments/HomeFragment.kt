package com.eit.brnnda.fragments

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.eit.brnnda.Adapter.*
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.Constent.decodedStringURL
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.FragmentHomeBinding
import com.eit.brnnda.dataclass.CartData


import com.eit.brnnda.dataclass.CategoryDataItem
import com.eit.brnnda.dataclass.ProductDataItem
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel
import com.rezwan.knetworklib.KNetwork

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory
    private lateinit var bestProductAdapter: BestProductsAdapter
    private lateinit var flashDealAdapter: FlashDealAdapter
    private lateinit var catAdapter: HomeCatAdapter
    private lateinit var featuredAdapter: FeaturedProductsAdapter
    private val sliderUrl = "$decodedStringURL/assets/images/sliders/"

    lateinit var knRequest: KNetwork.Request

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.myViewModel = vm

        KNetwork.initialize(requireContext())
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.progressbar.visibility=View.VISIBLE
        val PREFS_NAME = getString(R.string.sharedPrefName)
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Welcome to brnnda.com"



        vm.message.observe(viewLifecycleOwner, Observer { myEvent ->
            myEvent.getContentIfNotHandled()?.let {
                requireContext().MyToast(it)
            }
        })


        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.internet_connected_layout)

        knRequest = activity?.let {
            KNetwork.bind(it, lifecycle)
                    .showKNDialog(false)
                    .showCroutons(false)

                    .setConnectivityListener(object : KNetwork.OnNetWorkConnectivityListener {
                        override fun onNetConnected() {
                            dialog.dismiss()

                        }
                        override fun onNetDisConnected() {

                            dialog.show()

                        }

                        override fun onNetError(msg: String?) {
                            requireContext().MyToast("Error ")
                        }

                    })
        }!!

        try {


            //Slider Image Start
            val imageList = ArrayList<SlideModel>()
            vm.topSliderData(decodedStringKey).observe(viewLifecycleOwner, Observer {

                val sliderList = it.body()?.listIterator()
                if (sliderList != null) {
                    while (sliderList.hasNext()) {
                        val sliderItem = sliderList.next()

                        imageList.add(
                                SlideModel(
                                        sliderUrl + sliderItem.photo,
                                        sliderItem.details_text
                                )
                        )
                        binding.imageSlider.setImageList(imageList)
                        Log.d("Slider", sliderItem.details_text)
                    }
                }


            })



            binding.imageSlider.setItemClickListener(object : ItemClickListener {
                override fun onItemSelected(position: Int) {
                    context?.MyToast("This is slider $position")
//                    vm.insertCart()
                }
            })

            //Slider Image End


            //Call functions start
            initCategoryRecyclerView()
            initFeaturedRecyclerView()
//            initProductRecyclerView()
            initBestProductRecyclerView()
            initFlashDealRecyclerView()
            //Call functions end

//                                configureTabLayout()


            binding.textViewCatViewId.setOnClickListener {
                it.findNavController().navigate(R.id.action_nav_home_to_catFragment)
            }


            binding.searchButtonId.setOnClickListener {
                it.findNavController().navigate(R.id.action_nav_home_to_searchFragment)


            }


        } catch (e: Exception) {

        }






    }

    private fun initFlashDealRecyclerView() {
        binding.recyclerViewFlashDealId.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        flashDealAdapter = FlashDealAdapter (        { selected: ProductDataItem -> productClick(selected) },
                { addtoCart: ProductDataItem -> addToCart(addtoCart) })
        binding.recyclerViewFlashDealId.adapter = flashDealAdapter
        displayFlashDealItemList()
    }

    private fun displayFlashDealItemList() {
        vm.flashDealData(decodedStringKey).observe(viewLifecycleOwner, Observer {
            flashDealAdapter.setList(it)
            flashDealAdapter.notifyDataSetChanged()

        })
    }

    private fun initBestProductRecyclerView() {
        binding.recyclerViewBestProductId.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        bestProductAdapter =
                BestProductsAdapter(  { selected: ProductDataItem -> productClick(selected) },
                        { addtoCart: ProductDataItem -> addToCart(addtoCart) })
        binding.recyclerViewBestProductId.adapter = bestProductAdapter
        displayBestProductItemList()
    }

    private fun displayBestProductItemList() {
        vm.bestProductData(decodedStringKey).observe(viewLifecycleOwner, Observer {
            binding.progressbar.visibility = View.INVISIBLE
            bestProductAdapter.setList(it)
            bestProductAdapter.notifyDataSetChanged()

        })
    }


    //Init RecyclerView
    private fun initCategoryRecyclerView() {
        binding.recyclerViewCategoryId.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        catAdapter =
                HomeCatAdapter { selectedPlus: CategoryDataItem -> catClick(selectedPlus) }
        binding.recyclerViewCategoryId.adapter = catAdapter
        displayCategoryList()
    }


    //Display List
    private fun displayCategoryList() {
        vm.getAllCategoryData(decodedStringKey).observe(viewLifecycleOwner, Observer {
            catAdapter.setList(it)
            catAdapter.notifyDataSetChanged()

        })
    }


    private fun initFeaturedRecyclerView() {
        binding.recyclerViewFeaturedId.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        featuredAdapter = FeaturedProductsAdapter (

                { selected: ProductDataItem -> productClick(selected) },
                { addtoCart: ProductDataItem -> addToCart(addtoCart) }

        )

        binding.recyclerViewFeaturedId.adapter = featuredAdapter
        displayFeaturedItemList()
    }

    private fun addToCart(addtoCart: ProductDataItem) {


        vm.checkProductIdBool(addtoCart.id).observe(viewLifecycleOwner, Observer { bool ->
            if (bool == true) {
                requireContext().MyToast("Already in cart")
            } else {

                val vendor_user_id: String = addtoCart.user_id
                val id: Int = addtoCart.id
                val qty: Int = 1
                val price: Double = addtoCart.price.toDouble()
                val color: String = "Default"
                val cartItemBrandName: String = addtoCart.sku
                val cartItemName: String = addtoCart.name
                val cashback = addtoCart.cashback
                val cartItemImage: String = addtoCart.thumbnail

                var taxAmount: Int = 0
                var taxType: Int = 0

                if (addtoCart.product_tax_status==null){
                    taxAmount=0
                    taxType=0
                }else{
                    if (addtoCart.product_tax_status=="1"){
                        taxAmount=addtoCart.product_tax.toInt()
                        taxType=1
                    }else if (addtoCart.product_tax_status=="0"){
                        taxAmount=0
                        taxType=0
                    }
                }



                var taxPrice: Double = 0.0

                if (taxType == 1) {
                    val lessAmount = (taxAmount * price) / 100
                    taxPrice = lessAmount
                } else if (taxType == 0) {
                    taxPrice = 0.0
                }

                Log.d("TAGS", "$taxPrice")


                vm.insertCart(CartData(
                        vendor_user_id.toInt(),
                        id,
                        qty,
                        price,
                        color,
                        cashback,
                        cartItemBrandName,
                        cartItemName,
                        cartItemImage,
                        taxType,
                        taxPrice,
                        taxAmount
                        ))

            }
        })


    }


    private fun displayFeaturedItemList() {
        vm.featuredProductData(decodedStringKey).observe(viewLifecycleOwner, Observer {
            featuredAdapter.setList(it)
            featuredAdapter.notifyDataSetChanged()

        })
    }


    ///Onclick Section










    private fun catClick(data: CategoryDataItem) {
        val bundle = Bundle()
        bundle.putString("SLUG", data.slug)
        bundle.putString("NAME", data.name)
        findNavController().navigate(R.id.action_nav_home_to_catWiseProductFragment, bundle)
    }
















    private fun productClick(data: ProductDataItem) {
        val bundle = Bundle()
        bundle.putString("SLUG", data.slug)
        findNavController().navigate(R.id.action_nav_home_to_productFragment, bundle)
    }


//    private fun configureTabLayout() {
//
//        binding.tablayoutId.addTab(binding.tablayoutId.newTab().setText("Hot"))
//        binding.tablayoutId.addTab(binding.tablayoutId.newTab().setText("New"))
//        binding.tablayoutId.addTab(binding.tablayoutId.newTab().setText("Trending"))
//        binding.tablayoutId.addTab(binding.tablayoutId.newTab().setText("Sale"))
//
//
//        val adapter = ProductViewPagerAdapter(childFragmentManager, binding.tablayoutId.tabCount)
//        binding.viewpagerId.adapter = adapter
//        binding.viewpagerId.addOnPageChangeListener(
//                TabLayout.TabLayoutOnPageChangeListener(binding.tablayoutId)
//        )
//        binding.tablayoutId.addOnTabSelectedListener(object :
//                TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                binding.viewpagerId.currentItem = tab.position
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab) {
//
//            }
//
//        })
//    }


}