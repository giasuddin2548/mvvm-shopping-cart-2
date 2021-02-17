package com.eit.brnnda.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.ActivityLoginBinding
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel
import com.rezwan.knetworklib.KNetwork

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.myViewModel = vm
        supportActionBar?.hide()


        val PREFS_NAME = getString(R.string.sharedPrefName)
        val sharedPref: SharedPreferences =
                this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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














        binding.buttonLoginActivity.setOnClickListener {

            try {
                val phone: String = binding.editTextPhoneLoginActivity.text.toString().trim()
                val pass: String = binding.editTextPasswordLoginActivity.text.toString().trim()





                if (phone == null) {
                    binding.editTextPhoneLoginActivity.error = "phone required"
                } else if (pass == null) {
                    binding.editTextPasswordLoginActivity.error = "password required"
                } else {

                    binding.progressbar.visibility = View.VISIBLE
                    binding.buttonLoginActivity.visibility = View.INVISIBLE

                    vm.loginPost(decodedStringKey, phone, pass).observe(this, Observer { loginResponse ->
                        binding.progressbar.visibility = View.GONE
                        binding.buttonLoginActivity.visibility = View.VISIBLE


                        val message = loginResponse.body()?.message




                        if (message == "1") {

                            val editor: SharedPreferences.Editor = sharedPref.edit()
                            editor.putString(
                                    getString(R.string.SHARED_UID),
                                    loginResponse.body()!!.user_id.toString()
                            )
                            editor.putString(
                                    getString(R.string.SHARED_UTOKEN),
                                    loginResponse.body()!!.token.toString()
                            )
                            editor.apply()

                            this.MyToast("Login success")
                            val intent = Intent(this, Dashboard::class.java)
                            startActivity(intent)
                            finish()
                        } else if (message == "0") {
                            this.MyToast("Login failed")

                        }


                    })
                }




            } catch (e: Exception) {

            }
        }

        binding.buttonRegisterLoginActivity.setOnClickListener {
            val i = Intent(this, SignUpActivity::class.java)
            startActivity(i)
        }


    }

    private fun isValidMobile(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }

}



