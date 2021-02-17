package com.eit.brnnda.fragments.AccountSection

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.eit.brnnda.Adapter.OrderHistoryAdapter
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.FragmentOrderHistoryBinding
import com.eit.brnnda.dataclass.OrderHistoryDataItem
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel

class OrderHistoryFragment : Fragment() {
    private lateinit var binding: FragmentOrderHistoryBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter


    var id: String? = null
    var token: String? = null

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_history, container, false)
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

        try {


            val PREFS_NAME = getString(R.string.sharedPrefName)
            val sharedPref: SharedPreferences =
                    requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            id = sharedPref.getString(getString(R.string.SHARED_UID), null)
            token = sharedPref.getString(getString(R.string.SHARED_UTOKEN), null)



            initOrderHistoryRecyclerView()


        } catch (e: Exception) {

        }


    }

    private fun initOrderHistoryRecyclerView() {
        binding.reyclerVieworderHistoryId.layoutManager = GridLayoutManager(context, 1)
        orderHistoryAdapter =
                OrderHistoryAdapter { selectedPlus: OrderHistoryDataItem -> catItemClick(selectedPlus) }
        binding.reyclerVieworderHistoryId.adapter = orderHistoryAdapter
        displayOrderHistoryList()
    }

    private fun catItemClick(data: OrderHistoryDataItem) {
        val bundle = Bundle()
        bundle.putString("ORDERNO", data.id.toString()
        )
        findNavController().navigate(R.id.action_orderHistoryFragment_to_orderTrackFragment, bundle)

    }

    private fun displayOrderHistoryList() {
        token?.let {
            id?.let { it1 ->
                vm.customerOrderHistory(decodedStringKey, it, it1).observe(viewLifecycleOwner, Observer {
                    binding.progressbar.visibility=View.GONE
                    orderHistoryAdapter.setList(it)
                    orderHistoryAdapter.notifyDataSetChanged()

                })
            }
        }
    }


}