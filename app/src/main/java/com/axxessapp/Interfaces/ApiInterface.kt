package com.axxessapp.Interfaces

import com.axxessapp.Model.ResultModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiInterface {
    @GET("3/gallery/search/1")
    fun getImagesList(@Query("q") searchString: String?): Call<ResultModel>
}