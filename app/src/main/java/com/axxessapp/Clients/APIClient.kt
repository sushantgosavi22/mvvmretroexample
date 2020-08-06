package com.axxessapp.Clients

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIClient {


    companion object {

        //https://api.imgur.com/3/gallery/search/1?q=vanilla
        val baseURL: String = "https://api.imgur.com/"
        var retofit: Retrofit? = null

        val client: Retrofit
            get() {
                if (retofit == null) {
                    retofit = Retrofit.Builder()
                            .baseUrl(baseURL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(
                                    OkHttpClient.Builder().addInterceptor { chain ->
                                        var token = "Client-ID 137cda6b5008a7c"
                                        val request = chain.request().newBuilder().addHeader("Authorization", token).build()
                                        chain.proceed(request)
                                    }.build()
                                )
                            .build()
                }
                return retofit!!
            }
    }
}