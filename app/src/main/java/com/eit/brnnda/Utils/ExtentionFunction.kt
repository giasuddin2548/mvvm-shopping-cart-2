package com.eit.brnnda.Utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Toast
import com.eit.brnnda.R

fun Context.MyToast(msg:String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

