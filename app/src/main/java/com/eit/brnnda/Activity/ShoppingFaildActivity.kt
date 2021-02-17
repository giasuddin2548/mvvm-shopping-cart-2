package com.eit.brnnda.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.eit.brnnda.R

class ShoppingFaildActivity : AppCompatActivity() {

    lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_faild)
        supportActionBar?.hide()
        button=findViewById(R.id.buttonShoppingFailToDashBoardId)
        button.setOnClickListener {
            val intent= Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()

        }
    }
}