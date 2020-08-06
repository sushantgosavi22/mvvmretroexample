package com.axxessapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.axxessapp.Clients.APIClient
import com.axxessapp.Interfaces.ApiInterface
import com.axxessapp.Model.ResultModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShapeListingViewModel(application: Application) : AndroidViewModel(application) {
    fun getImagesList(context: Context, query: String?,apiCallBack : ApiCallBack){
        var apiServices = APIClient.client.create(ApiInterface::class.java)
        val call = apiServices.getImagesList(query)
        call.enqueue(object : Callback<ResultModel> {
            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                val jsonResponse = response.body()
                if(jsonResponse?.success==true){
                    var dataList = jsonResponse.data
                    apiCallBack.onCallBack(jsonResponse.success,dataList,null)
                }else{
                    apiCallBack.onCallBack(jsonResponse?.success, ArrayList(),Throwable("Unable to load Data..."))
                }
            }

            override fun onFailure(call: Call<ResultModel>?, t: Throwable?) {
                apiCallBack.onCallBack(false, ArrayList(),t)
            }
        })
    }




    public interface ApiCallBack{
       fun onCallBack(success: Boolean?,data : List<ShapeModelData>, t: Throwable?)
    }

}