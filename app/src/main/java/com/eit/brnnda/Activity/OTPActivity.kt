package com.eit.brnnda.Activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.eit.brnnda.databinding.ActivityOTPBinding
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel
import com.rezwan.knetworklib.KNetwork

class OTPActivity : AppCompatActivity() {
    lateinit var binding: ActivityOTPBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory
    var phoneNo: String?=null

    lateinit var knRequest: KNetwork.Request

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = RoomDatabaseAbstract.invoke(application).getCartDao
        repository = NetworkRepository(dao)
        factory = BaseViewModelFactory(repository)
        vm = ViewModelProvider(this, factory).get(MyViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_o_t_p)
        binding.myViewModel = vm
        binding.lifecycleOwner=this
        KNetwork.initialize(this)
        supportActionBar?.hide()


        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.internet_connected_layout)
        val PREFS_NAME = getString(R.string.sharedPrefName)
        val sharedPref: SharedPreferences =
                this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


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





        binding.buttonVerifyId.setOnClickListener {

            binding.progressbar.visibility = View.VISIBLE
            binding.buttonVerifyId.visibility = View.GONE
            val code: String = binding.etOTPId.text.toString().trim()

            val myintent: Intent = intent
            val obj : String? =  myintent.getStringExtra("MOBILE");
            Log.d("mytag", "VAlue is==>$obj")
            callOTP(code, obj)


        }
    }

    private fun callOTP(code: String, obj: String?) {

        if (obj != null) {
            vm.otpVerify(decodedStringKey, code, obj).observe(this, Observer { res ->
                binding.progressbar.visibility = View.GONE
                binding.buttonVerifyId.visibility = View.VISIBLE
                val response = res.body()?.msg
                val response1 = res.body()?.status
                Log.d("RES", response.toString())
                Log.d("RES", response1.toString())
                this.MyToast(response.toString())

                if (response1=="1"){
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }else if (response1=="0"){
                    this.MyToast(response.toString())
                }else{
                    this.MyToast(response.toString())
                }

            })
        }
            }


}




