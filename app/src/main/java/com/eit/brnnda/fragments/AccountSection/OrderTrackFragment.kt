package com.eit.brnnda.fragments.AccountSection

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.eit.brnnda.Adapter.SingleOrderAdapter
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.FragmentOrderTrackBinding
import com.eit.brnnda.dataclass.Product
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel



class OrderTrackFragment :  Fragment() {
    private lateinit var binding: FragmentOrderTrackBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory

    private lateinit var adapter: SingleOrderAdapter

    var id:String?=null
    var token:String?=null


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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_track, container, false)
        binding.myViewModel = vm
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val PREFS_NAME = getString(R.string.sharedPrefName)
        val sharedPref: SharedPreferences =
                requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        id = sharedPref.getString(getString(R.string.SHARED_UID), null)
        token = sharedPref.getString(getString(R.string.SHARED_UTOKEN), null)

        binding.progressbar.visibility=View.VISIBLE
        vm.message.observe(viewLifecycleOwner, Observer { myEvent ->
            myEvent.getContentIfNotHandled()?.let {
                requireContext().MyToast(it)
            }
        })


        initRecyclerView()

        try {


            arguments?.let {recived->
                val trackingNo=recived.getString("ORDERNO")

                token?.let {
                    id?.let {
                        it1 ->
                        if (trackingNo != null) {
                            vm.customerSingleOrderHistory(decodedStringKey, it, it1,trackingNo).observe(viewLifecycleOwner, Observer {response_data->
                                binding.progressbar.visibility=View.GONE
                                binding.tvVoucherId.text="Order#${response_data.body()?.order_number}"
                                binding.tvDiscountPriceId.text="৳${response_data.body()?.coupon_discount}"
                                binding.tvFinalPriceId.text="৳${response_data.body()?.pay_amount}"
                                binding.tvShippingPriceId.text="৳${response_data.body()?.shipping_cost}"
                                binding.stausid.text="${response_data.body()?.status}"

                                response_data.body()?.product_list?.let { it2 -> adapter.setList(it2) }
                                adapter.notifyDataSetChanged()

                            })
                        }
                    } }
            }


        } catch (e: Exception) {

        }


    }


    //Init RecyclerView
    private fun initRecyclerView() {
        binding.recyclerViewOrderListId.layoutManager = GridLayoutManager(context, 1)
        adapter = SingleOrderAdapter (
            { selectedPlus: Product -> itemClickPlus(selectedPlus) },
            { selectedMinus: Product -> itemClickMinus(selectedMinus) },
            { selectedDelete: Product -> itemClickDelete(selectedDelete) }
        )
        binding.recyclerViewOrderListId.adapter = adapter

    }

    private fun itemClickPlus(data: Product) {

    }

    private fun itemClickMinus(data: Product) {


    }

    private fun itemClickDelete(data: Product) {

    }


    //Display List



}