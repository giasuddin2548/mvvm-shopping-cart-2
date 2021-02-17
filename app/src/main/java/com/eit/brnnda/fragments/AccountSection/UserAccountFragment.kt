package com.eit.brnnda.fragments.AccountSection

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.eit.brnnda.Activity.LoginActivity
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.FragmentUserAccountBinding

import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel
import com.rezwan.knetworklib.KNetwork

class UserAccountFragment : Fragment() {
    private lateinit var binding: FragmentUserAccountBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory
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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_account, container, false)
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
        val id = sharedPref.getString(getString(R.string.SHARED_UID), null)
        val token = sharedPref.getString(getString(R.string.SHARED_UTOKEN), null)

        //Constant part end

        if (token == null) {
            val intent=Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()

        }


        knRequest = activity?.let {
            KNetwork.bind(it, lifecycle)
                .showKNDialog(false)
                .showCroutons(false)

                .setConnectivityListener(object : KNetwork.OnNetWorkConnectivityListener {
                    override fun onNetConnected() {
                        binding.progressBarDetailsId.visibility = View.VISIBLE
                        dialog.dismiss()

                        try {

                            if (token !== null && id != null) {
                                vm.userInfo(decodedStringKey, token, id)
                                    .observe(viewLifecycleOwner, Observer { userInfoResponse ->
                                        binding.progressBarDetailsId.visibility = View.GONE
                                        binding.progressBarDetailsId.visibility = View.INVISIBLE
                                        val responseName = userInfoResponse.body()?.name
                                        val responseEmail = userInfoResponse.body()?.email
                                        val responsePhone = userInfoResponse.body()?.phone
                                        val responseWallet = userInfoResponse.body()?.wallet
                                        val responseId = userInfoResponse.body()?.id
                                        val responsePhoto = userInfoResponse.body()?.photo

                                        binding.tVUserNameId.text = responseName
                                        binding.tVUserShowBalanceId.text = "$$responseWallet"


                                        Glide.with(requireContext())
                                            .load(responsePhoto)
                                            .centerCrop()
                                            .placeholder(R.drawable.ic_baseline_person_outline_24)
                                            .into(binding.imageViewUserImageId)


                                    })
                            } else {

                                binding.progressBarDetailsId.visibility = View.GONE
                                binding.tVUserNameId.text = "Guest user"
                                binding.tVUserShowBalanceId.text = "$0"


                                Glide.with(requireContext())
                                    .load("aa")
                                    .centerCrop()
                                    .error(R.mipmap.brnnda)
                                    .placeholder(R.mipmap.brnnda)
                                    .into(binding.imageViewUserImageId)


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



        binding.tVMyOrderId.setOnClickListener {
            it.findNavController().navigate(R.id.action_bottom_account_to_orderHistoryFragment)
        }



        binding.tVRateUsId.setOnClickListener {
            val uri = Uri.parse("market://details?id="+requireActivity().packageName)
            val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
            try {
                startActivity(myAppLinkToMarket)
            } catch (e: ActivityNotFoundException) {
            }

        }
        binding.tVInviteId.setOnClickListener {
            val intent= Intent()
            intent.action=Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,"Hey Check out this Great app:")
            intent.type="text/plain"
            startActivity(Intent.createChooser(intent,"Share To:"))
        }





        binding.imageViewLogOutId.setOnClickListener {

            if (token != null) {
                vm.logout(decodedStringKey, token)
                    .observe(viewLifecycleOwner, Observer { logoutResponse ->
                        val response = logoutResponse.body()?.status
                        val response1 = logoutResponse.body()?.message
                        Log.d("LOGOUT", response1.toString())
                        requireContext().MyToast(response1.toString())

                        if (response=="1"){
                            requireContext().MyToast(response1.toString())
                            sharedPref.edit().clear().apply()
                            val intent=Intent(requireActivity(), LoginActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }else{
                            requireContext().MyToast(response1.toString())
                        }


                    })
            }


        }


        binding.tVEditProfile.setOnClickListener {
            it.findNavController().navigate(R.id.action_bottom_account_to_updateProfileFragment)
        }



    }


}