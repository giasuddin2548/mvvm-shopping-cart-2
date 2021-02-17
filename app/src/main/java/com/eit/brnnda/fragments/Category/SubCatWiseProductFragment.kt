package com.eit.brnnda.fragments.Category

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.eit.brnnda.Adapter.BestProductsAdapter
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.FragmentSubCatWiseProductBinding
import com.eit.brnnda.dataclass.CartData
import com.eit.brnnda.dataclass.ProductDataItem
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel

class SubCatWiseProductFragment :Fragment() {
    private lateinit var binding: FragmentSubCatWiseProductBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory
    private lateinit var adapter: BestProductsAdapter

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
            R.layout.fragment_sub_cat_wise_product, container, false
        )
        binding.myViewModel = vm
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.progressbar.visibility=View.VISIBLE





        vm.message.observe(viewLifecycleOwner, Observer { myEvent ->
            myEvent.getContentIfNotHandled()?.let {
                requireContext().MyToast(it)
            }
        })

        arguments.let {
            val brandId: String? = it?.getString("SLUG")
            val brandName: String? = it?.getString("NAME")
            if (brandId != null) {
                (requireActivity() as AppCompatActivity).supportActionBar?.title = brandName
                initCategoryRecyclerView(brandId)
            }
        }


    }


    //Init RecyclerView
    private fun initCategoryRecyclerView(brandId: String) {
        binding.recyclerViewId.layoutManager = GridLayoutManager(context, 2)
        adapter = BestProductsAdapter (

            { selected: ProductDataItem -> productClick(selected) },
            { addtoCart: ProductDataItem -> addToCart(addtoCart) }

        )
        binding.recyclerViewId.adapter = adapter
        displayCategoryList(brandId)
    }


    private fun displayCategoryList(brandId: String) {
        vm.getSubCatWiseProduct(decodedStringKey, brandId).observe(viewLifecycleOwner, Observer {
            binding.progressbar.visibility=View.GONE

            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })

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


    private fun productClick(selected: ProductDataItem) {
        val bundle = Bundle()
        bundle.putString("SLUG", selected.slug)
        findNavController().navigate(R.id.action_subCatWiseProductFragment_to_productFragment, bundle)
    }


}