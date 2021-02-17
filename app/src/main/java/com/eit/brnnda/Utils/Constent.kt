package com.eit.brnnda.Utils

import android.util.Base64

object Constent {

    init {
        System.loadLibrary("native-lib")
    }

    external fun apiURL(): String
    external fun apiKEY(): String
    external fun amarPaySignature(): String
    external fun amarPayStoreId(): String
    external fun apSignature(): String
    external fun apStore(): String

    private val myURL = Base64.decode(apiURL(), Base64.DEFAULT)
    val decodedStringURL = String(myURL)
    private val key = Base64.decode(apiKEY(), Base64.DEFAULT)
    val decodedStringKey = String(key)
    private val storeId = Base64.decode(amarPaySignature(), Base64.DEFAULT)
    val decodedAmarPayStoreId = String(storeId)
    private val signature = Base64.decode(amarPaySignature(), Base64.DEFAULT)
    val decodedAmarPaySignatureId = String(signature)

    val apSignature=apSignature()
    val apStore=apStore()


}