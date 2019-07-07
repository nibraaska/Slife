package com.slife.slife.Networking

import android.util.Log
import com.slife.slife.BottomNavigation.Universities.Models.CountryList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

class MobileAPI(baseURL: String) {
    private var mAPI: Endpoints = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Endpoints::class.java)


    fun getCountryList(callback: MobileCallBack<CountryList>){
        val call = mAPI.getCountryList()
        call.enqueue(object: Callback<CountryList>{
            override fun onFailure(call: Call<CountryList>, t: Throwable) {
                t.message?.let { callback.failure(it) }
            }

            override fun onResponse(
                call: Call<CountryList>,
                response: retrofit2.Response<CountryList>
            ) {
                response.body()?.let { callback.success(it) }
            }
        })
    }
}