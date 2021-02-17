package com.eit.brnnda.fragments
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.eit.brnnda.Activity.LoginActivity
import com.eit.brnnda.Adapter.CartItemAdapter
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.FragmentCartBinding
import com.eit.brnnda.dataclass.CartData
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel
import com.example.awesomedialog.*


class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory
    private lateinit var cartItemAdapter: CartItemAdapter

    private lateinit var myCartcartData: CartData

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
            R.layout.fragment_cart, container, false
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

        vm.calculateTotalPrice.observe(viewLifecycleOwner, Observer {

                setTotalCartAmount(it)

        })

        initCartItemRecyclerView()



        val PREFS_NAME = getString(R.string.sharedPrefName)
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val id = sharedPref.getString(getString(R.string.SHARED_UID), null)
        val token = sharedPref.getString(getString(R.string.SHARED_UTOKEN), null)




        binding.tvPlaceOrderId.setOnClickListener {
            if (id==null){
                val intent=Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)

            }else{
                it.findNavController().navigate(R.id.action_bottom_cart_to_checkOutFragment)
            }


        }

    }

    private fun setTotalCartAmount(it: Double?) {
        binding.tvTotalCartAmount.text = String.format("৳%.2f", it)
    }

    private fun initCartItemRecyclerView() {
        binding.rvCart.layoutManager = GridLayoutManager(context, 1)
        cartItemAdapter = CartItemAdapter(

                { selectedPlus: CartData -> itemClickPlus(selectedPlus) },
                { selectedMinus: CartData -> itemClickMinus(selectedMinus) },
                { selectedDelete: CartData -> itemClickDelete(selectedDelete) }
        )
        binding.rvCart.adapter = cartItemAdapter
        displayCartItemList()
    }


    private fun displayCartItemList() {
        vm.cartAllData.observe(viewLifecycleOwner, Observer {


            if (it.isNotEmpty()){

                cartItemAdapter.setList(it)
                cartItemAdapter.notifyDataSetChanged()
                binding.textviewItemFoundId.visibility=View.INVISIBLE
                binding.textviewItemFoundId.visibility=View.GONE
                binding.bottomId.visibility=View.VISIBLE

            }else{
                cartItemAdapter.setList(it)
                cartItemAdapter.notifyDataSetChanged()
                binding.textviewItemFoundId.visibility=View.VISIBLE
                binding.bottomId.visibility=View.INVISIBLE
            }




        })
    }

    private fun itemClickPlus(data: CartData) {
        myCartcartData = data
        myCartcartData.qty = data.qty.plus(1)
        vm.updateCart(myCartcartData)
    }

    private fun itemClickMinus(data: CartData) {
        myCartcartData = data
        if (data.qty == 1) {
            requireContext().MyToast("Minimum order: can't remove")
        } else {
            myCartcartData.qty = data.qty.minus(1)
            vm.updateCart(myCartcartData)
        }


    }

    private fun itemClickDelete(data: CartData) {
        AwesomeDialog
            .build(requireActivity())
            .position(AwesomeDialog.POSITIONS.CENTER)
            .title("Do you want to delete?")
            .icon(R.mipmap.brnnda)
            . onPositive("Yes, I agree"){
                vm.deleteCartItem(data)
                cartItemAdapter.notifyDataSetChanged()
            }
            .onNegative("Cancel"){

            }

    }


}