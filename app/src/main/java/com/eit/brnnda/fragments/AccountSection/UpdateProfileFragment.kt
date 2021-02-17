package com.eit.brnnda.fragments.AccountSection

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eit.brnnda.Activity.LoginActivity
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.FragmentUpdateProfileBinding
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel
import com.rezwan.knetworklib.KNetwork

class UpdateProfileFragment : Fragment() {
    private lateinit var binding: FragmentUpdateProfileBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory
     var userId: String? =null
     var userToken: String? =null
    lateinit var knRequest: KNetwork.Request
    private val requestCode=100

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
            R.layout.fragment_update_profile, container, false
        )
        binding.myViewModel = vm
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val PREFS_NAME = getString(R.string.sharedPrefName)
        val sharedPref: SharedPreferences =
            requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        userId = sharedPref.getString(getString(R.string.SHARED_UID), null)
        userToken = sharedPref.getString(getString(R.string.SHARED_UTOKEN), null)




        if (userToken == null) {
            val intent= Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()

        }

        KNetwork.initialize(requireContext())
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.internet_connected_layout)

        knRequest = requireActivity().let {
            KNetwork.bind(it, lifecycle)
                .showKNDialog(false)
                .showCroutons(false)

                .setConnectivityListener(object : KNetwork.OnNetWorkConnectivityListener {
                    override fun onNetConnected() {
                        dialog.hide()

                    }

                    override fun onNetDisConnected() {
                        dialog.show()
                    }

                    override fun onNetError(msg: String?) {
                        dialog.hide()
                    }
                })
        }


        vm.message.observe(viewLifecycleOwner, Observer { myEvent ->
            myEvent.getContentIfNotHandled()?.let {
                requireContext().MyToast(it)
            }
        })


        binding.buttonSubmitActivity.setOnClickListener {

            updateProfile()

        }

        binding.imageViewAddImageId.setOnClickListener {

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                activity?.let { it1 -> ActivityCompat.requestPermissions(it1, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100) }
            } else {
                val mimeTypes = arrayOf(
                    "application/.png",
                    "application/.JPEG",
                    "application/.jpeg"

                )
                println("chooseFile activated!");
                var selectFile = Intent(Intent.ACTION_GET_CONTENT)
                selectFile.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
                if (mimeTypes.isNotEmpty()) {
                    selectFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                }
                selectFile = Intent.createChooser(selectFile, "Choose a file")
                startActivityForResult(selectFile, requestCode)

            }


        }

    }

    private fun updateProfile() {

        try {
            val name: String = binding.etEditProfileNameId.text.toString().trim()
            val email: String = binding.etEditProfileEmailId.text.toString().trim()
            val city: String = binding.etEditProfileCityId.text.toString().trim()
            val zip: String = binding.etEditProfileZipId.text.toString().trim()
            val address: String = binding.etEditProfileAddressId.text.toString().trim()





            if (name.isEmpty()) {
                binding.etEditProfileNameId.error = "Name required"
            } else if (!isValidEmail(email)) {
                binding.etEditProfileEmailId.error = "Email required"
            } else if (city.isEmpty()) {
                binding.etEditProfileCityId.error = "City required"
            } else if (zip.isEmpty()) {
                binding.etEditProfileZipId.error = "Zip required"
            } else if (address.isEmpty()) {
                binding.etEditProfileAddressId.error = "Address required"
            }  else {

                binding.progressbar.visibility = View.VISIBLE
                binding.buttonSubmitActivity.visibility = View.INVISIBLE

                userToken?.let {
                    userId?.let { it1 ->
                        vm.editProfile(decodedStringKey, it, it1,name, email, address, zip, city).observe(viewLifecycleOwner, Observer {
                            binding.progressbar.visibility = View.GONE
                            binding.buttonSubmitActivity.visibility = View.VISIBLE


                            val message = it.body()?.message




                            if (message != null) {
                                if (message.contains("Success")) {

                                    requireContext().MyToast("Profile updated successfully")

                                } else if (message.contains("Failed")) {
                                    requireContext().MyToast("Failed to update profile")

                                }
                            }


                        })
                    }
                }
            }




        } catch (e: Exception) {

        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestCode && resultCode == Activity.RESULT_OK) {
            if (data != null) {

                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, data.data)
                binding.imageViewUserImageId.setImageBitmap(bitmap)

//                val imageType =requireActivity().contentResolver.getType(data.data!!)
//
//                val extension = imageType!!.substring(imageType.indexOf("/") + 1)
//
//                data.data!!.let {
//                    activity?.contentResolver?.openInputStream(it)?.use {
//                            inputStream ->
//                        filePartImage = MultipartBody.Part.createFormData(
//                            "image",
//                            "image.$extension",
//                            inputStream.readBytes().toRequestBody("*/*".toMediaType())
//                        )
//                    }
//                }
                requireContext().MyToast("Upload Success")
            } else {
                requireContext().MyToast("Upload Failed")
            }
        }


    }


    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}


