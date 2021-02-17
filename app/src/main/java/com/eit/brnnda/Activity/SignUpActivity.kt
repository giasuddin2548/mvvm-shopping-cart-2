package com.eit.brnnda.Activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.ActivitySignUpBinding
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel
import com.rezwan.knetworklib.KNetwork
class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory
    lateinit var knRequest: KNetwork.Request
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = RoomDatabaseAbstract.invoke(application).getCartDao
        repository = NetworkRepository(dao)
        factory = BaseViewModelFactory(repository)
        vm = ViewModelProvider(this, factory).get(MyViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.myViewModel = vm

        supportActionBar?.hide()

        KNetwork.initialize(this)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.internet_connected_layout)



        knRequest = this.let {
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
                            dialog.show()
                        }
                    })
        }






        binding.signUpViewButtonId.setOnClickListener {


            val name: String = binding.etFullNameId.text.toString().trim()
            val email: String = binding.etEmailId.text.toString().trim()
            val phone: String = binding.etPhoneId.text.toString().trim()
            val password: String = binding.editTextPasswordId.text.toString().trim()
            val address: String = binding.etAddressId.text.toString().trim()



            if (name.isEmpty()) {
                binding.etFullNameId.error = "Full name required"
            } else if (!isValidEmail(email)) {
                binding.etEmailId.error = "email required"
            } else if (phone.isEmpty()) {
                binding.etPhoneId.error = "phone required"
            } else if (password.isEmpty()) {
                binding.editTextPasswordId.error = "password required"
            } else if (address.isEmpty()) {
                binding.etAddressId.error = "address required"
            } else {




                                binding.progressbar.visibility = View.VISIBLE
                                binding.signUpViewButtonId.visibility = View.INVISIBLE

                                vm.userRegistration(decodedStringKey, name, email, phone, address, password)
                                    .observe(this@SignUpActivity, Observer { logoutResponse ->
                                        binding.progressbar.visibility = View.GONE
                                        binding.signUpViewButtonId.visibility = View.VISIBLE
                                        val responseMessage = logoutResponse.body()?.message
                                        val response = logoutResponse.body()?.status
                                        Log.d("UserInfo", response.toString())
                                        this@SignUpActivity.MyToast(responseMessage.toString())


                                        if (response == "0") {
                                            Toast.makeText(
                                                this@SignUpActivity,
                                                responseMessage,
                                                Toast.LENGTH_SHORT
                                            ).show()



                                        } else if (response == "3") {
                                            Toast.makeText(
                                                this@SignUpActivity,
                                                responseMessage,
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            val intent = Intent(this@SignUpActivity, OTPActivity::class.java)
                                            intent.putExtra("MOBILE",phone)
                                            startActivity(intent)


                                        } else {

                                        }


                                    })
                            }




            }




        binding.tvAlreadyRegistered.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

    }


    private fun isValidMobile(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


}