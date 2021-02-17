package com.eit.brnnda.fragments

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.eit.brnnda.Adapter.WishAdapter
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.Constent.decodedStringURL
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.FragmentUserAccountBinding
import com.eit.brnnda.databinding.FragmentWishListBinding
import com.eit.brnnda.dataclass.ProductDataItem
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel
import com.example.awesomedialog.*
import com.rezwan.knetworklib.KNetwork

class WishListFragment : Fragment() {
    private lateinit var binding: FragmentWishListBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory
    private val sliderUrl = "$decodedStringURL/assets/images/sliders/"
    lateinit var knRequest: KNetwork.Request


    var id :String?= null
    var token :String?= null

    private lateinit var wishAdapter: WishAdapter
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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_wish_list, container, false)
        binding.myViewModel = vm

        KNetwork.initialize(requireContext())
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        //Constant part start
        vm.message.observe(viewLifecycleOwner, Observer { myEvent ->
            myEvent.getContentIfNotHandled()?.let {
                requireContext().MyToast(it)
            }
        })

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.internet_connected_layout)

        val PREFS_NAME = getString(R.string.sharedPrefName)
        val sharedPref: SharedPreferences =
            requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
         id = sharedPref.getString(getString(R.string.SHARED_UID), null)
         token = sharedPref.getString(getString(R.string.SHARED_UTOKEN), null)

        //Constant part end

        initwishRecyclerView()

        knRequest = activity?.let {
            KNetwork.bind(it, lifecycle)
                .showKNDialog(false)
                .showCroutons(false)

                .setConnectivityListener(object : KNetwork.OnNetWorkConnectivityListener {
                    override fun onNetConnected() {
                        binding.progressbar.visibility = View.VISIBLE
                        dialog.dismiss()

                        try {

                            if (token !== null && id != null) {
                                vm.getWishList(decodedStringKey, token!!, id!!)
                                    .observe(viewLifecycleOwner, Observer {wishResponse->

                                        binding.progressbar.visibility = View.GONE
                                        binding.progressbar.visibility = View.INVISIBLE
                                        if (wishResponse.isNotEmpty()){

                                            wishAdapter.setList(wishResponse)
                                            wishAdapter.notifyDataSetChanged()
                                            binding.textviewItemFoundId.visibility=View.INVISIBLE
                                            binding.textviewItemFoundId.visibility=View.GONE


                                        }else{
                                            wishAdapter.setList(wishResponse)
                                            wishAdapter.notifyDataSetChanged()
                                            binding.textviewItemFoundId.visibility=View.VISIBLE

                                        }


                                    })
                            } else {

                                requireContext().MyToast("Please login")
                                binding.progressbar.visibility = View.GONE
                                binding.progressbar.visibility = View.INVISIBLE


                            }

                        } catch (e: Exception) {

                        }
                    }

                    override fun onNetDisConnected() {
                        dialog.show()
                    }

                    override fun onNetError(msg: String?) {
                        dialog.show()
                    }

                })
        }!!









    }




    private fun initwishRecyclerView() {
        binding.reyclerViewCatId.layoutManager = GridLayoutManager(context, 2)
        wishAdapter = WishAdapter(

            { selectedPlus: ProductDataItem -> productClick(selectedPlus) },
            { selectedDelete: ProductDataItem -> itemClickDelete(selectedDelete) }
        )
        binding.reyclerViewCatId.adapter = wishAdapter

    }
    private fun productClick(data: ProductDataItem) {
        val bundle = Bundle()
        bundle.putString("SLUG", data.slug)
        findNavController().navigate(R.id.action_bottom_wish_to_productFragment, bundle)
    }



    private fun itemClickDelete(productDataItem: ProductDataItem) {
        AwesomeDialog
            .build(requireActivity())
            .position(AwesomeDialog.POSITIONS.CENTER)
            .title("Do you want to delete?")
            .icon(R.mipmap.brnnda)
            . onPositive("Yes, I agree"){

                binding.progressbar.visibility = View.VISIBLE

                id?.let {
                    token?.let { it1 ->
                        vm.deleteWishProduct("aa", it1, it, productDataItem.id.toString()).observe(viewLifecycleOwner,
                            Observer {server->

                                binding.progressbar.visibility = View.GONE
                                binding.progressbar.visibility = View.INVISIBLE


                                val response= server.body()?.status
                                val message= server.body()?.message

                                if (response=="1"){
                                    wishAdapter.notifyDataSetChanged()
                                    requireContext().MyToast(message.toString())

                                }else{

                                    requireContext().MyToast(message.toString())
                                }



                            })
                    }
                }



            }
            .onNegative("Cancel"){

            }

    }



}