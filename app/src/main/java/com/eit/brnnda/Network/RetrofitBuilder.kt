package com.eit.brnnda.Network

import com.eit.brnnda.Utils.Constent.decodedStringURL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitBuilder {

    companion object {


        private val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }


        private val client = OkHttpClient
                .Builder().apply {
                    this.addInterceptor(interceptor)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(25, TimeUnit.SECONDS)
                }.build()


        private val gson: Gson = GsonBuilder()
                .setLenient()
                .disableHtmlEscaping()
                .create()


        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                    .baseUrl(decodedStringURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
        }

        val api: ApiInterface by lazy {
            retrofit.create(ApiInterface::class.java)
        }
    }
}