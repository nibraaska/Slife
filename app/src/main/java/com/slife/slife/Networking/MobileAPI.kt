package com.slife.slife.Networking

import com.slife.slife.BottomNavigation.Universities.Models.College
import com.slife.slife.BottomNavigation.Universities.Models.CollegesInCountry
import com.slife.slife.BottomNavigation.Universities.Models.CountryList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
                response: Response<CountryList>
            ) {
                response.body()?.let { callback.success(it) }
            }
        })
    }

    fun getCollegesInCountry(country: String, callback: MobileCallBack<CollegesInCountry>){
        val call = mAPI.getCollegesInCountry(country)
        call.enqueue(object: Callback<CollegesInCountry>{
            override fun onFailure(call: Call<CollegesInCountry>, t: Throwable) {
                t.message?.let { callback.failure(it) }
            }

            override fun onResponse(call: Call<CollegesInCountry>, response: Response<CollegesInCountry>) {
                response.body()?.let { callback.success(it) }
            }

        })
    }

    fun getCollege(country: String, college: String, callback: MobileCallBack<College>){
        val call = mAPI.getCollege(country, college)
        call.enqueue(object: Callback<College>{
            override fun onFailure(call: Call<College>, t: Throwable) {
                t.message?.let { callback.failure(it) }
            }

            override fun onResponse(call: Call<College>, response: Response<College>) {
                response.body()?.let { callback.success(it) }
            }

        })
    }
}