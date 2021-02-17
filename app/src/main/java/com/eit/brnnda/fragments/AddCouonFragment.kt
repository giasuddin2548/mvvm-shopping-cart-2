package com.eit.brnnda.fragments

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
import com.eit.brnnda.Adapter.CouponAdapter
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.FragmentAddCouonBinding
import com.eit.brnnda.dataclass.CouponDataItem
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel


class AddCouonFragment : Fragment() {
    private lateinit var binding: FragmentAddCouonBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory
    private lateinit var couponAdapter: CouponAdapter


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
            R.layout.fragment_add_couon, container, false
        )
        binding.myViewModel = vm
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm.message.observe(viewLifecycleOwner, Observer { myEvent ->
            myEvent.getContentIfNotHandled()?.let {
                requireContext().MyToast(it)
            }
        })




        initCouponRecyclerView()
    }


    private fun initCouponRecyclerView() {
        binding.recyclerViewCouponId.layoutManager = GridLayoutManager(context, 1)
        couponAdapter = CouponAdapter { selected: CouponDataItem -> itemClick(selected) }
        binding.recyclerViewCouponId.adapter = couponAdapter
        displayCartItemList()
    }


    private fun displayCartItemList() {
        vm.couponData(decodedStringKey).observe(viewLifecycleOwner, Observer {
            couponAdapter.setList(it)
            couponAdapter.notifyDataSetChanged()

        })
    }


    private fun itemClick(data: CouponDataItem) {
        val bundle = Bundle()
        bundle.putString("VOUCHER-ID", data.id.toString())
        bundle.putString("VOUCHER-CODE", data.code)
        bundle.putString("VOUCHER-PRICE", data.price.toString())
        bundle.putString("VOUCHER-TYPE", data.type.toString())
        findNavController().navigate(R.id.action_addCouonFragment_to_checkOutFragment, bundle)

    }

}